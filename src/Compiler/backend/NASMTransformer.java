package Compiler.backend;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRFunction;
import Compiler.IR.IRRoot;
import Compiler.IR.Instruction.*;
import Compiler.IR.Value.*;
import Compiler.utils.Configuration;

import java.util.*;

import static Compiler.NASM.NASMRegSet.*;

public class NASMTransformer {
    public IRRoot ir;
    public Map<IRFunction, Info> infoMap;

    public NASMTransformer(IRRoot ir) {
        this.ir = ir;
        infoMap = new HashMap<>();
    }

    public class Info {
        List<PhysicalReg> useCallerSaves = new ArrayList<>();
        List<PhysicalReg> useCalleeSaves = new ArrayList<>();
        Set<PhysicalReg> recurRefRegs = new HashSet<>();
        Map<StackSlot, Integer> slotOffsetMap = new HashMap<>();
        int numExArgs, numSlots = 0;
    }

    public void gatherInfo(IRFunction func) {
        Info info = new Info();
        for (PhysicalReg preg : func.refPRegs) {
            if (preg.isCallerSave) info.useCallerSaves.add(preg);
            if (preg.isCalleeSave) info.useCalleeSaves.add(preg);
        }
        info.useCalleeSaves.add(rbx);
        info.useCalleeSaves.add(rbp);

        info.numSlots = func.slots.size();
        for (int i = 0; i < info.numSlots; ++i) {
            info.slotOffsetMap.put(func.slots.get(i), i * Configuration.REG_SIZE);
        }

        if ((info.useCalleeSaves.size() + info.numSlots) % 2 == 0) {
            ++info.numSlots;
        }

        List<VirtualReg> argVregList = func.argVRegList;
        info.numExArgs = Math.max(0, argVregList.size() - 6);

        // cause the return addr
        int extraArgoOffset = (info.useCalleeSaves.size() + info.numSlots + 1) * Configuration.REG_SIZE;
        for (int i = 6; i < argVregList.size(); ++i) {
            info.slotOffsetMap.put(func.argSlotMap.get(argVregList.get(i)), extraArgoOffset);
            extraArgoOffset += Configuration.REG_SIZE;
        }
        infoMap.put(func, info);
    }

    public void beFunc(IRFunction func) {
        Info info = infoMap.get(func);
        IRBasicBlock startBB = func.startBB;
        IRInstruction first = startBB.firstInst;
        for (PhysicalReg preg : info.useCalleeSaves) {
            first.addBefore(new PushIR(startBB, preg));
        }
        if (info.numSlots > 0) {
            first.addBefore(new BinaryIR(startBB, rsp, BinaryIR.IRBinaryOp.SUB, rsp, new Imm(info.numSlots * Configuration.REG_SIZE)));
        }
        first.addBefore(new MoveIR(startBB, rbp, rsp));
    }


    public void inFunc(IRFunction func) {
        Info info = infoMap.get(func);
        for (IRBasicBlock bb : func.revPostOrder) {
            for (IRInstruction inst = bb.firstInst; inst != null; inst = inst.nextInst) {


                if (inst instanceof FuncCallIR) {
                    IRFunction callee = ((FuncCallIR) inst).func;
                    Info calleeInfo = infoMap.get(callee);
                    int numPushCallerSave = 0;
                    int numPosArg = Math.min(func.argVRegList.size(), 6);

                    // push callerSave(except for callerposArg)
                    for (PhysicalReg preg : info.useCallerSaves) {
                        if (preg.isPosArg && preg.posArgId < numPosArg) continue;
                        if (calleeInfo.recurRefRegs.contains(preg)) {
                            ++numPushCallerSave;
                            inst.addBefore(new PushIR(inst.parentBB, preg));
                        }
                    }

                    // push callersave(just callerposArg)
                    for (int i = 0; i < numPosArg; ++i) {
                        inst.addBefore(new PushIR(inst.parentBB, posArgRegs.get(i)));
                    }
                    numPushCallerSave += numPosArg;


                    boolean alignPush = false;
                    List<IRValue> args = ((FuncCallIR) inst).args;
                    int numCalleePosArg = Math.min(args.size(), 6);

                    // rsp allignment
                    if ((numPushCallerSave + calleeInfo.numExArgs) % 2 == 1) {
                        alignPush = true;
                        inst.addBefore(new PushIR(inst.parentBB, new Imm(0)));
                    }

                    // push extraArg
                    for (int i = args.size() - 1; i > 5; --i) {
                        if (args.get(i) instanceof StackSlot) {
                            inst.addBefore(new LoadIR(inst.parentBB, rax, Configuration.REG_SIZE, rbp, info.slotOffsetMap.get(args.get(i))));
                            inst.addBefore(new PushIR(inst.parentBB, rax));
                        } else {
                            inst.addBefore(new PushIR(inst.parentBB, args.get(i)));
                        }
                    }

                    // judge whether arg itself take the place of posArg
                    int index = 0;
                    List<Integer> indexList = new ArrayList<>();
                    Map<PhysicalReg, Integer> indexMap = new HashMap<>();
                    for (int i = 0; i < numCalleePosArg; ++i) {
                        IRValue arg = args.get(i);
                        if (arg instanceof PhysicalReg && ((PhysicalReg) arg).isPosArg && ((PhysicalReg) arg).posArgId < numCalleePosArg) {
                            PhysicalReg preg = (PhysicalReg) arg;
                            if (indexMap.containsKey(preg)) {
                                indexList.add(indexMap.get(preg));
                            } else {
                                indexList.add(index);
                                indexMap.put(preg, index);
                                inst.addBefore(new PushIR(inst.parentBB, preg));
                                index += 1;
                            }
                        } else {
                            indexList.add(-1);
                        }
                    }

                    // assign arg to posArg
                    for (int i = 0; i < numCalleePosArg; ++i) {
                        if (indexList.get(i) == -1) {
                            if (args.get(i) instanceof StackSlot) {
                                inst.addBefore(new LoadIR(inst.parentBB, rax, Configuration.REG_SIZE, rbp, info.slotOffsetMap.get(args.get(i))));
                                inst.addBefore(new MoveIR(inst.parentBB, posArgRegs.get(i), rax));
                            } else {
                                inst.addBefore(new MoveIR(inst.parentBB, posArgRegs.get(i), args.get(i)));
                            }
                        } else {
                            inst.addBefore(new LoadIR(inst.parentBB, posArgRegs.get(i), Configuration.REG_SIZE, rsp, Configuration.REG_SIZE * (index - 1 - indexList.get(i))));
                        }
                    }

                    // pop those arg itself take the place of posArg
                    if (index > 0) {
                        inst.addBefore(new BinaryIR(inst.parentBB, rsp, BinaryIR.IRBinaryOp.ADD, rsp, new Imm(index * Configuration.REG_SIZE)));
                    }

                    // move rax to dest;
                    if (((FuncCallIR) inst).dest != null) {
                        inst.addNext(new MoveIR(inst.parentBB, ((FuncCallIR) inst).dest, rax));
                    }


                    // pop callersave
                    for (PhysicalReg preg : info.useCallerSaves) {
                        if (preg.isPosArg && preg.posArgId < numPosArg) continue;
                        if (calleeInfo.recurRefRegs.contains(preg)) {
                            inst.addNext(new PopIR(inst.parentBB, preg));
                        }
                    }

                    // pop callerposarg
                    for (int i = 0; i < numPosArg; ++i) {
                        inst.addNext(new PopIR(inst.parentBB, posArgRegs.get(i)));
                    }

                    // remove extra arguments
                    if (calleeInfo.numExArgs > 0 || alignPush) {
                        int wtf = alignPush ? (calleeInfo.numExArgs + 1) : calleeInfo.numExArgs;
                        inst.addNext(new BinaryIR(inst.parentBB, rsp, BinaryIR.IRBinaryOp.ADD, rsp, new Imm(Configuration.REG_SIZE * wtf)));
                    }

                } else if (inst instanceof HeapAllocIR) {
                    int numPushCallerSave = 0;
                    for (PhysicalReg preg : info.useCallerSaves) {
                        ++numPushCallerSave;
                        inst.addBefore(new PushIR(inst.parentBB, preg));
                    }
                    inst.addBefore(new MoveIR(inst.parentBB, rdi, ((HeapAllocIR) inst).allocSize));
                    // for align??
                    if (numPushCallerSave % 2 == 1) {
                        inst.addBefore(new PushIR(inst.parentBB, new Imm(0)));
                    }

                    inst.addNext(new MoveIR(inst.parentBB, ((HeapAllocIR) inst).dest, rax));

                    for (PhysicalReg preg : info.useCallerSaves) {
                        inst.addNext(new PopIR(inst.parentBB, preg));
                    }

                    if (numPushCallerSave % 2 == 1) {
                        inst.addNext(new BinaryIR(inst.parentBB, rsp, BinaryIR.IRBinaryOp.ADD, rsp, new Imm(Configuration.REG_SIZE)));
                    }

                } else if (inst instanceof LoadIR) {
                    if (((LoadIR) inst).addr instanceof StackSlot) {
                        ((LoadIR) inst).addrOffset = info.slotOffsetMap.get(((LoadIR) inst).addr);
                        ((LoadIR) inst).addr = rbp;
                    }
                } else if (inst instanceof StoreIR) {
                    if (((StoreIR) inst).addr instanceof StackSlot) {
                        ((StoreIR) inst).addrOffset = info.slotOffsetMap.get(((StoreIR) inst).addr);
                        ((StoreIR) inst).addr = rbp;
                    }
                } else if (inst instanceof MoveIR) {
                    if (((MoveIR) inst).lhs == ((MoveIR) inst).rhs) {
                        inst.remove();
                    }
                }
            }
        }
    }

    public void afFunc(IRFunction func) {
        Info info = infoMap.get(func);
        ReturnIR retInst = func.retInstList.get(0);
        if (retInst.retValue != null) {
            retInst.addBefore(new MoveIR(retInst.parentBB, rax, retInst.retValue));
        }
        IRBasicBlock endBB = func.endBB;
        IRInstruction inst = endBB.lastInst;
        if (info.numSlots > 0) {
            inst.addBefore(new BinaryIR(endBB, rsp, BinaryIR.IRBinaryOp.ADD, rsp, new Imm(info.numSlots * Configuration.REG_SIZE)));
        }
        for (int i = info.useCalleeSaves.size() - 1; i >= 0; --i) {
            inst.addBefore(new PopIR(endBB, info.useCalleeSaves.get(i)));
        }
    }

    public void calcRecur() {
        for (IRFunction func : infoMap.keySet()) {
            Info info = infoMap.get(func);
            info.recurRefRegs.addAll(func.refPRegs);
            for (IRFunction callee : func.recurCalleeSet) {
                info.recurRefRegs.addAll(callee.refPRegs);
            }
        }
    }

    public void run() {
        for (IRFunction func : ir.funcs.values()) {
            gatherInfo(func);
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
