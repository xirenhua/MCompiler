package Compiler.entities;

import Compiler.type.Type;

public class ClassEntity extends Entity {
    public ClassScope classScope;
    public int memorySize;

    public ClassEntity(String name, Type type, GlobalScope globalScope) {
        super(name, type);
        classScope = new ClassScope(globalScope);
    }
}
