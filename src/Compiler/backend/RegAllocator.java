package Compiler.backend;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRFunction;
import Compiler.IR.IRRoot;
import Compiler.IR.Instruction.*;
import Compiler.IR.Value.*;
import Compiler.NASM.NASMRegSet;
import Compiler.utils.CompileError;
import Compiler.utils.Configuration;

import java.util.*;

public class RegAllocator {
    public IRRoot ir;
    public List<PhysicalReg> availPRegs;
    public int numColors;
    public PhysicalReg preg0, preg1;

    public Map<VirtualReg, ColorInfo> colorMap;
    public Set<VirtualReg> nodes;
    public Set<VirtualReg> lowDegNodes;
    public Stack<VirtualReg> nodeStack;

    public class ColorInfo {
        int degree = 0;
        Set<VirtualReg> neibs = new HashSet<>();
        boolean removed = false;
        PhysicalReg colorP = null;
        StackSlot colorS = null;
        Set<VirtualReg> suggests = new HashSet<>();
    }

    public RegAllocator(IRRoot ir) {
        this.ir = ir;
        availPRegs = new ArrayList<>(NASMRegSet.generalRegs);
        int maxFuncArgs = 3;
        for (IRFunction func : ir.funcs.values()) {
            int tmp = func.argVRegList.size();
            maxFuncArgs = (tmp > maxFuncArgs) ? tmp : maxFuncArgs;
        }
        if (maxFuncArgs > 4)
            availPRegs.remove(NASMRegSet.r8);
        if (maxFuncArgs > 5)
            availPRegs.remove(NASMRegSet.r9);

        // what the hell ??? was that
        if (ir.hasDivShift) {
            preg0 = availPRegs.get(0);
            preg1 = availPRegs.get(1);
        } else {
            preg0 = NASMRegSet.rbx;
            preg1 = availPRegs.get(0);
        }
        ir.preg0 = preg0;
        ir.preg1 = preg1;
        availPRegs.remove(preg0);
        availPRegs.remove(preg1);
        numColors = this.availPRegs.size();

        colorMap = new HashMap<>();
        nodes = new HashSet<>();
        lowDegNodes = new HashSet<>();
        nodeStack = new Stack<>();
    }


    public ColorInfo getColorInfo(VirtualReg vreg) {
        ColorInfo colorInfo = colorMap.get(vreg);
        if (colorInfo == null) {
            colorInfo = new ColorInfo();
            colorMap.put(vreg, colorInfo);
        }
        return colorInfo;
    }

    public void addEdge(VirtualReg vreg1, VirtualReg vreg2) {
        getColorInfo(vreg1).neibs.add(vreg2);
        getColorInfo(vreg2).neibs.add(vreg1);
    }

    // work together with Nodes.iter
    public void removeNode(VirtualReg vreg) {
        ColorInfo colorInfo = colorMap.get(vreg);
        colorInfo.removed = true;
        nodes.remove(vreg);
        nodeStack.add(vreg);
        for (VirtualReg nei : colorInfo.neibs) {
            ColorInfo ni = colorMap.get(nei);
            if (ni.removed) continue;
            --ni.degree;
            if (ni.degree < numColors) {
                lowDegNodes.add(nei);
            }
        }
    }

    // after globalvar def use reg consist of staticdata in func-around load and store inst
    // but livein liveout only take care of the virtualreg
    // staticString treated just like static data without adding extra load and store
    public void createGraph(IRFunction func) {
        for (VirtualReg argVreg : func.argVRegList) {
            getColorInfo(argVreg);
        }

        for (IRBasicBlock bb : func.revPreOrder) {
            for (IRInstruction inst = bb.firstInst; inst != null; inst = inst.nextInst) {
                Reg defReg = inst.defReg;
                if (!(defReg instanceof VirtualReg)) continue;
                ColorInfo colorInfo = getColorInfo((VirtualReg) defReg);

                if (inst instanceof MoveIR) {
                    IRValue rhs = ((MoveIR) inst).rhs;
                    if (rhs instanceof VirtualReg) {
                        colorInfo.suggests.add((VirtualReg) rhs);
                        getColorInfo((VirtualReg) rhs).suggests.add((VirtualReg) defReg);
                    }
                    for (VirtualReg vreg : inst.liveOut) {
                        if (vreg != defReg && vreg != rhs) {
                            addEdge(vreg, (VirtualReg) defReg);
                        }
                    }
                } else {
                    for (VirtualReg vreg : inst.liveOut) {
                        if (vreg != defReg)
                            addEdge(vreg, (VirtualReg) defReg);
                    }
                }
            }
        }
        for (ColorInfo colorInfo : colorMap.values()) {
            colorInfo.degree = colorInfo.neibs.size();
        }
        nodes.addAll(colorMap.keySet());
    }

    // what if the parameter was not in the graph and so color = null;
    // impossible tha arg are all assigned info
    public void colorize(IRFunction func) {
        for (VirtualReg node : nodes) {
            if (colorMap.get(node).degree < numColors) {
                lowDegNodes.add(node);
            }
        }

        while (!nodes.isEmpty()) {
            while (!lowDegNodes.isEmpty()) {
                Iterator<VirtualReg> iterator = lowDegNodes.iterator();
                VirtualReg vreg = iterator.next();
                iterator.remove();
                removeNode(vreg);
            }
            if (nodes.isEmpty()) break;
            Iterator<VirtualReg> iterator = nodes.iterator();
            VirtualReg vreg = iterator.next();
            iterator.remove();
            removeNode(vreg);
        }

        VirtualReg curVreg;
        ColorInfo curInfo;
        Set<PhysicalReg> usedColors = new HashSet<>();
        while (!nodeStack.empty()) {
            curVreg = nodeStack.pop();
            curInfo = colorMap.get(curVreg);
            curInfo.removed = false;
            usedColors.clear();
            for (VirtualReg neib : curInfo.neibs) {
                ColorInfo ni = colorMap.get(neib);
                if (!ni.removed && ni.colorP != null) {
                    usedColors.add(ni.colorP);
                }
            }

            PhysicalReg forcePhyReg = curVreg.forcePhyReg;
            if (forcePhyReg != null) {
                if (usedColors.contains(forcePhyReg)) {
                    throw new CompileError("forced physical register has been used");
                }
                curInfo.colorP = forcePhyReg;
            } else {
                for (VirtualReg sug : curInfo.suggests) {
                    PhysicalReg colorP = colorMap.get(sug).colorP;
                    StackSlot colorS = colorMap.get(sug).colorS;
                    if (!usedColors.contains(colorP)) {
                        curInfo.colorP = colorP;
                        break;
                    }
                }
                if (curInfo.colorP == null) {
                    for (PhysicalReg preg : availPRegs) {
                        if (!usedColors.contains(preg)) {
                            curInfo.colorP = preg;
                            break;
                        }
                    }
                    if (curInfo.colorP == null) {
                        curInfo.colorS = func.argSlotMap.get(curVreg);
                        if (curInfo.colorS == null) curInfo.colorS = new StackSlot(func, curVreg.name, false);
                    }
                }
            }
        }
    }

    public void updateInst(IRFunction func) {
        for (IRBasicBlock bb : func.revPreOrder) {
            for (IRInstruction inst = bb.firstInst; inst != null; inst = inst.nextInst) {
                if (inst instanceof FuncCallIR) {
                    // stackslot dealt with later
                    List<IRValue> args = ((FuncCallIR) inst).args;
                    for (int i = 0; i < args.size(); ++i) {
                        IRValue arg = args.get(i);
                        if (arg instanceof VirtualReg) {
                            if (colorMap.get(arg).colorP != null)
                                args.set(i, colorMap.get(arg).colorP);
                            else
                                args.set(i, colorMap.get(arg).colorS);
                        }
                    }
                } else {
                    Collection<Reg> useRegs = inst.useRegs;
                    if (!useRegs.isEmpty()) {
                        Map<Reg, Reg> renameMap = new HashMap<>();
                        boolean preg0Used = false;
                        for (Reg reg : useRegs) {
                            if (reg instanceof VirtualReg) {
                                PhysicalReg colorP = colorMap.get(reg).colorP;
                                StackSlot colorS = colorMap.get(reg).colorS;
                                if (colorS != null) {
                                    PhysicalReg preg;
                                    if (preg0Used) {
                                        preg = preg1;
                                    } else {
                                        preg = preg0;
                                        preg0Used = true;
                                    }
                                    inst.addBefore(new LoadIR(bb, preg, Configuration.REG_SIZE, colorS, 0));
                                    renameMap.put(reg, preg);
                                    func.refPRegs.add(preg);
                                } else {
                                    renameMap.put(reg, colorP);
                                    func.refPRegs.add(colorP);
                                }
                            } else {
                                renameMap.put(reg, reg);
                            }
                        }
                        inst.mapUseRegs(renameMap);
                    }
                }

                Reg defReg = inst.defReg;
                if (defReg instanceof VirtualReg) {
                    PhysicalReg colorP = colorMap.get(defReg).colorP;
                    StackSlot colorS = colorMap.get(defReg).colorS;
                    if (colorS != null) {
                        inst.mapDefReg(preg0);
                        inst.addNext(new StoreIR(bb, preg0, Configuration.REG_SIZE, colorS, 0));
                        func.refPRegs.add(preg0);
                        inst = inst.nextInst;
                    } else {
                        inst.mapDefReg(colorP);
                        func.refPRegs.add(colorP);
                    }
                }
            }
        }
    }

    public void run() {
        for (IRFunction func : ir.funcs.values()) {
            colorMap.clear();
            nodes.clear();
            lowDegNodes.clear();
            nodeStack.clear();
            createGraph(func);
            colorize(func);
            updateInst(func);
        }
    }
}