package Compiler.type;

import Compiler.utils.Configuration;

public class IntType extends PrimitiveType {
    public IntType() {
        hyperType = HyperTypes.INT;
        size = Configuration.REG_SIZE;
    }

    @Override
    public String toString() {
        return "Int";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IntType);
    }
}