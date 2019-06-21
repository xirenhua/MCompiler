package Compiler.backend;

import Compiler.IR.*;
import Compiler.IR.Instruction.BinaryIR;
import Compiler.IR.Instruction.IRInstruction;
import Compiler.IR.Instruction.MoveIR;
import Compiler.IR.Value.Reg;
import Compiler.IR.Value.VirtualReg;

public class TwoRegOpTransformer {
    public IRRoot ir;

    public TwoRegOpTransformer(IRRoot ir) {
        this.ir = ir;
    }

    public void run() {
        for (IRFunction irFunction : ir.funcs.values()) {
            for (IRBasicBlock bb : irFunction.revPostOrder) {
                for (IRInstruction inst = bb.firstInst; inst != null; inst = inst.nextInst) {
                    if (!(inst instanceof BinaryIR)) continue;
                    BinaryIR binaryInst = (BinaryIR) inst;
                    if (binaryInst.dest == binaryInst.lhs) continue;
                    if (binaryInst.dest == binaryInst.rhs) {
                        VirtualReg vreg = new VirtualReg("rhsBak");
                        binaryInst.addBefore(new MoveIR(binaryInst.parentBB, vreg, binaryInst.rhs));
                        binaryInst.addBefore(new MoveIR(binaryInst.parentBB, binaryInst.dest, binaryInst.lhs));
                        binaryInst.lhs = binaryInst.dest;
                        binaryInst.rhs = vreg;
                        binaryInst.renewDefReg();
                        binaryInst.renewUseRegs();
                    } else if (binaryInst.op != BinaryIR.IRBinaryOp.DIV &&
                            binaryInst.op != BinaryIR.IRBinaryOp.MOD) {
                        binaryInst.addBefore(new MoveIR(binaryInst.parentBB, binaryInst.dest, binaryInst.lhs));
                        binaryInst.lhs = binaryInst.dest;
                        binaryInst.renewDefReg();
                        binaryInst.renewUseRegs();
                    }
                }
            }
        }
    }
}
