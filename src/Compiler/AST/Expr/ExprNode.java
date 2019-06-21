package Compiler.AST.Expr;

import Compiler.AST.AstNode;
import Compiler.IR.IRBasicBlock;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;
import Compiler.type.Type;

public abstract class ExprNode extends AstNode {
    public Type type;
    public boolean isLeftValue;
    public IRValue reg;
    public Reg addr;
    public int addrOffset;
    public IRBasicBlock trueBB, falseBB;
}
