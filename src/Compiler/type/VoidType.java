package Compiler.type;

public class VoidType extends PrimitiveType {

    public VoidType() {
        hyperType = HyperTypes.VOID;
    }

    @Override
    public String toString() {
        return "Void";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof VoidType);
    }
}
