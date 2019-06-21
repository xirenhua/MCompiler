package Compiler.IR;

import Compiler.IR.Instruction.*;
import Compiler.IR.Value.*;

public interface IRVisitor {
    void visit(IRRoot node);

    void visit(IRFunction node);

    void visit(IRBasicBlock node);


    void visit(BranchIR node);

    void visit(JumpIR node);

    void visit(ReturnIR node);


    void visit(UnaryIR node);

    void visit(BinaryIR node);

    void visit(CompIR node);

    void visit(MoveIR node);

    void visit(LoadIR node);

    void visit(StoreIR node);

    void visit(FuncCallIR node);

    void visit(PushIR node);

    void visit(PopIR node);

    void visit(HeapAllocIR node);

    void visit(Imm node);

    void visit(VirtualReg node);

    void visit(PhysicalReg node);

    void visit(StaticVar node);

    void visit(StaticString node);
}