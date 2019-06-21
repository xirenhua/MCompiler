package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;

public abstract class TransferIR extends IRInstruction {
    public TransferIR(IRBasicBlock parentBB) {
        super(parentBB);
    }
}
