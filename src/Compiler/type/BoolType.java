package Compiler.type;

import Compiler.utils.Configuration;

public class BoolType extends PrimitiveType {
    public BoolType() {
        hyperType = HyperTypes.BOOL;
        size = Configuration.REG_SIZE;
    }

    @Override
    public String toString() {
        return "Bool";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BoolType);
    }
}