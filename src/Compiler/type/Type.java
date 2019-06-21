package Compiler.type;

abstract public class Type {
    public enum HyperTypes {
        VOID, INT, BOOL, STRING, CLASS, ARRAY, FUNCTION, NULL
    }

    public HyperTypes hyperType;

    public int size;

    abstract public String toString();

    abstract public boolean equals(Object obj);
}