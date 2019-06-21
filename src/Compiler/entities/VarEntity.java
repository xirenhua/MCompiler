package Compiler.entities;

import Compiler.AST.Decl.VarDeclNode;
import Compiler.IR.Value.Reg;
import Compiler.type.Type;

public class VarEntity extends Entity {
    public Reg reg;
    public int addrOffset;

    public VarEntity(String name, Type type) {
        super(name, type);
    }
}
