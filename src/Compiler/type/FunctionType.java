package Compiler.type;

public class FunctionType extends Type {
    public String name;

    public FunctionType(String name) {
        hyperType = HyperTypes.FUNCTION;
        this.name = name;
    }

    // actually useless
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FunctionType)) return false;
        return name.equals(((FunctionType) obj).name);
    }

    @Override
    public String toString() {
        return String.format("Func: %s", name);
    }
}