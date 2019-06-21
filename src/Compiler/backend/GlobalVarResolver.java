package Compiler.backend;

import Compiler.IR.*;
import Compiler.IR.Instruction.*;
import Compiler.IR.Value.Reg;
import Compiler.IR.Value.StaticVar;
import Compiler.IR.Value.VirtualReg;
import Compiler.utils.Configuration;

import java.util.*;

public class GlobalVarResolver {
    public IRRoot ir;
    public Map<IRFunction, Info> infoMap;

    public GlobalVarResolver(IRRoot ir) {
        this.ir = ir;
        infoMap = new HashMap<>();
    }

    public class Info {
        Set<StaticVar> defStatic = new HashSet<>();
        Set<StaticVar> recurUseStatic = new HashSet<>();
        Set<StaticVar> recurDefStatic = new HashSet<>();
        Map<StaticVar, VirtualReg> vregMap = new HashMap<>();
    }

    public VirtualReg getVreg(Map<StaticVar, VirtualReg> vregMap, StaticVar staticVar) {
        VirtualReg vreg = vregMap.get(staticVar);
        if (vreg == null) {
            vreg = new VirtualReg(staticVar.name);
            vregMap.put(staticVar, vreg);
        }
        return vreg;
    }


    public void assignVreg(IRFunction func) {
        Info info = new Info();
        infoMap.put(func, info);
        Map<Reg, Reg> renameMap = new HashMap<>();
        for (IRBasicBlock bb : func.revPostOrder) {
            for (IRInstruction inst = bb.firstInst; inst != null; inst = inst.nextInst) {
                renameMap.clear();
                List<Reg> usedRegs = inst.useRegs;
                Reg defReg = inst.defReg;
                for (Reg reg : usedRegs) {
                    if (reg instanceof StaticVar) {
                        renameMap.put(reg, getVreg(info.vregMap, (StaticVar) reg));
                    } else {
                        renameMap.put(reg, reg);
                    }
                }
                inst.mapUseRegs(renameMap);
                if (defReg != null && defReg instanceof StaticVar) {
                    VirtualReg vreg = getVreg(info.vregMap, (StaticVar) defReg);
                    info.defStatic.add((StaticVar) defReg);
                    inst.mapDefReg(vreg);
                }
            }
        }
    }

    public void beFunc(IRFunction func) {
        Info info = infoMap.get(func);
        IRBasicBlock bb = func.startBB;
        IRInstruction ins = bb.firstInst;
        info.vregMap.forEach(((data, vr) -> ins.addBefore(new LoadIR(bb, vr, Configuration.REG_SIZE, data))));
    }

    public void inFunc(IRFunction func) {
        Info info = infoMap.get(func);
        Set<StaticVar> refStatic = info.vregMap.keySet();
        Set<StaticVar> defStatic = info.defStatic;
        if (refStatic.isEmpty()) return;
        for (IRBasicBlock bb : func.revPostOrder) {
            for (IRInstruction inst = bb.firstInst; inst != null; inst = inst.nextInst) {
                if (!(inst instanceof FuncCallIR)) continue;
                IRFunction callee = ((FuncCallIR) inst).func;
                Info calleeInfo = infoMap.get(callee);

                for (StaticVar staticData : defStatic) {
                    if (calleeInfo.recurUseStatic.contains(staticData)) {
                        inst.addBefore(new StoreIR(bb, info.vregMap.get(staticData), Configuration.REG_SIZE, staticData));
                    }
                }

                Set<StaticVar> loadStatic = new HashSet<>(calleeInfo.recurDefStatic);
                loadStatic.retainAll(info.vregMap.keySet());
                for (StaticVar staticData : loadStatic) {
                    inst.addNext(new LoadIR(bb, info.vregMap.get(staticData), Configuration.REG_SIZE, staticData));
                }
            }
        }
    }

    public void afFunc(IRFunction func) {
        Info info = infoMap.get(func);
        ReturnIR retInst = func.retInstList.get(0);
        for (StaticVar staticData : info.defStatic) {
            retInst.addBefore(new StoreIR(retInst.parentBB, info.vregMap.get(staticData), Configuration.REG_SIZE, staticData));
        }
    }

    public void calcRecur() {
        for (IRFunction func : ir.funcs.values()) {
            Info info = infoMap.get(func);
            info.recurUseStatic.addAll(info.vregMap.keySet());
            info.recurDefStatic.addAll(info.defStatic);
            for (IRFunction calleeFunc : func.recurCalleeSet) {
                Info calleeInfo = infoMap.get(calleeFunc);
                info.recurUseStatic.addAll(calleeInfo.vregMap.keySet());
                info.recurDefStatic.addAll(calleeInfo.defStatic);
            }
        }
    }

    public void run() {
        for (IRFunction func : ir.funcs.values()) {
            assignVreg(func);
        }
        for (IRFunction biFunc : ir.biFuncs.values()) {
            infoMap.put(biFunc, new Info());
        }
        calcRecur();
        for (IRFunction func : ir.funcs.values()) {
            beFunc(func);
            inFunc(func);
            afFunc(func);
        }
    }
}