package Compiler.type;

import Compiler.utils.Configuration;

public class ArrayType extends Type {
    public Type baseType;
    public int dimension = -1;

    public ArrayType(Type abaseType, int adimension) {
        hyperType = HyperTypes.ARRAY;
        baseType = abaseType;
        dimension = adimension;
        size = Configuration.REG_SIZE;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayType)) return false;
        return (baseType.equals(((ArrayType) obj).baseType) && (dimension == ((ArrayType) obj).dimension));
    }

    @Override
    public String toString() {
        return baseType.toString() + String.format(", Dimension %d", dimension);
    }
}