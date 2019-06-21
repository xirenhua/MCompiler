package Compiler.entities;


import Compiler.type.Type;

import java.util.List;

public class FuncEntity extends Entity {
    public List<VarEntity> parameters;
    public Type retType;
    public LocalScope funcScope;
    public boolean isMember;
    public boolean isBuiltin;

    public FuncEntity(String name, Type type, Scope parentScope) {
        super(name, type);
        funcScope = new LocalScope(parentScope);
        isBuiltin = false;
    }
}
