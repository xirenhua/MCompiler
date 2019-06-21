package Compiler.type;

public class NullType extends Type {
    public NullType() {
        hyperType = HyperTypes.NULL;
    }

    @Override
    public String toString() {
        return "NULL";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NullType);
    }
}