package Compiler.backend;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRFunction;
import Compiler.IR.IRRoot;
import Compiler.IR.Instruction.*;
import Compiler.IR.Value.Reg;
import Compiler.IR.Value.VirtualReg;

import java.util.*;

public class LiveAnalysis {
    public IRRoot ir;
    public boolean eliminationChanged;

    public LiveAnalysis(IRRoot ir) {
        this.ir = ir;
        this.eliminationChanged = false;
    }

    public void funcLiveAnalysis(IRFunction func) {
        Set<VirtualReg> tmpLiveIn = new HashSet<>();
        Set<VirtualReg> tmpLiveOut = new HashSet<>();

        boolean changed = true;
        while (changed) {
            changed = false;
            for (IRBasicBlock bb : func.revPreOrder) {
                for (IRInstruction inst = bb.lastInst; inst != null; inst = inst.prevInst) {
                    tmpLiveIn.clear();
                    tmpLiveOut.clear();
                    if (inst instanceof TransferIR) {
                        if (inst instanceof JumpIR) {
                            tmpLiveOut.addAll(((JumpIR) inst).targetBB.firstInst.liveIn);

                        } else if (inst instanceof BranchIR) {
                            tmpLiveOut.addAll(((BranchIR) inst).thenBB.firstInst.liveIn);
                            tmpLiveOut.addAll(((BranchIR) inst).elseBB.firstInst.liveIn);
                        }
                    } else {
                        if (inst.nextInst != null) {
                            tmpLiveOut.addAll(inst.nextInst.liveIn);
                        }
                    }

                    tmpLiveIn.addAll(tmpLiveOut);
                    Reg defReg = inst.defReg;
                    List<Reg> useRegs = inst.useRegs;
                    if (defReg instanceof VirtualReg) {
                        tmpLiveIn.remove(defReg);
                    }
                    for (Reg useReg : useRegs) {
                        if (useReg instanceof VirtualReg) {
                            tmpLiveIn.add((VirtualReg) useReg);
                        }
                    }

                    if (!inst.liveIn.equals(tmpLiveIn)) {
                        changed = true;
                        inst.liveIn.clear();
                        inst.liveIn.addAll(tmpLiveIn);
                    }
                    if (!inst.liveOut.equals(tmpLiveOut)) {
                        changed = true;
                        inst.liveOut.clear();
                        inst.liveOut.addAll(tmpLiveOut);
                    }
                }
            }
        }
    }

    public void tryEliminate(IRFunction func) {
        func.calcReversePreOrder();
        for (IRBasicBlock bb : func.revPreOrder) {
            for (IRInstruction inst = bb.lastInst, prevInst; inst != null; inst = prevInst) {
                prevInst = inst.prevInst;
                if (inst instanceof BinaryIR || inst instanceof CompIR ||
                        inst instanceof LoadIR || inst instanceof MoveIR || inst instanceof UnaryIR ||
                        inst instanceof HeapAllocIR) {
                    Reg dest = inst.defReg;
                    if (dest == null || !inst.liveOut.contains(dest)) {
                        eliminationChanged = true;
                        inst.remove();
                    }
                }

            }
        }


        for (IRRoot.ForRecord forRec : ir.forRecMap.values()) {
            if (forRec.processed) continue;
            boolean isFieldOutside = false;
            if (forRec.condBB == null || forRec.stepBB == null || forRec.bodyBB == null || forRec.afterBB == null)
                continue;
            List<IRBasicBlock> bbList = new ArrayList<>();
            bbList.add(forRec.condBB);
            bbList.add(forRec.stepBB);
            bbList.add(forRec.bodyBB);
            bbList.add(forRec.afterBB);
            IRInstruction afterFirstInst = forRec.afterBB.firstInst;
            for (int i = 0; i < 3; ++i) {
                for (IRInstruction inst = bbList.get(i).firstInst; inst != null; inst = inst.nextInst) {
                    if (inst instanceof FuncCallIR) {
                        isFieldOutside = true;
                        continue;
                    }
                    if (inst.defReg != null) {
                        if (afterFirstInst.liveIn.contains(inst.defReg)) {
                            isFieldOutside = true;
                        }
                        continue;
                    }
                    if (inst instanceof StoreIR) {
                        isFieldOutside = true;
                        continue;
                    }
                    if (inst instanceof JumpIR) {
                        if (!bbList.contains(((JumpIR) inst).targetBB))
                            isFieldOutside = true;
                        continue;
                    }
                    if (inst instanceof BranchIR) {
                        if (!bbList.contains(((BranchIR) inst).thenBB) || !bbList.contains(((BranchIR) inst).elseBB))
                            isFieldOutside = true;
                        continue;
                    }
                    if (inst instanceof ReturnIR || inst instanceof PushIR) {
                        isFieldOutside = true;
                        continue;
                    }
                }
            }
            if (!isFieldOutside) {
                forRec.condBB.reInit();
                forRec.condBB.setTransfer(new JumpIR(forRec.condBB, forRec.afterBB));
                forRec.processed = true;
            }
        }
    }

    private Map<IRBasicBlock, IRBasicBlock> jumpTargetBBMap = new HashMap<>();

    IRBasicBlock replaceJumpTarget(IRBasicBlock bb) {
        IRBasicBlock ret = bb, query = jumpTargetBBMap.get(bb);
        while (query != null) {
            ret = query;
            query = jumpTargetBBMap.get(query);
        }
        return ret;
    }

    void removeBlankBB(IRFunction func) {
        jumpTargetBBMap.clear();
        func.calcReversePostOrder();
        for (IRBasicBlock bb : func.revPostOrder) {
            if (bb.firstInst == bb.lastInst) {
                IRInstruction inst = bb.firstInst;
                if (inst instanceof JumpIR) {
                    jumpTargetBBMap.put(bb, ((JumpIR) inst).targetBB);
                }
            }
        }

        func.calcReversePostOrder();
        for (IRBasicBlock bb : func.revPostOrder) {
            if (bb.lastInst instanceof JumpIR) {
                JumpIR jumpInst = (JumpIR) bb.lastInst;
                jumpInst.targetBB = replaceJumpTarget(jumpInst.targetBB);
            } else if (bb.lastInst instanceof BranchIR) {
                BranchIR branchInst = (BranchIR) bb.lastInst;
                branchInst.thenBB = replaceJumpTarget(branchInst.thenBB);
                branchInst.elseBB = replaceJumpTarget(branchInst.elseBB);
                if (branchInst.thenBB == branchInst.elseBB) {
                    branchInst.replace(new JumpIR(bb, branchInst.thenBB));
                }
            }
        }
    }


    public void run() {
        for (IRFunction func : ir.funcs.values()) {
            funcLiveAnalysis(func);
        }
        eliminationChanged = true;

        while (eliminationChanged) {
            eliminationChanged = false;
            for (IRFunction func : ir.funcs.values()) {
                if (func.isBuitIn) continue;
                tryEliminate(func);
                removeBlankBB(func);
                funcLiveAnalysis(func);
            }
        }
    }
}
