package Compiler.entities;

abstract public class Scope {
    public Scope parent;

    public abstract void putVar(String name, VarEntity varEntity);

    public abstract void putFunc(String name, FuncEntity funcEntity);

    public abstract void putClass(String name, ClassEntity classEntity);

    public abstract boolean checkKey(String name);

    public abstract Entity recurGetKey(String name);

}