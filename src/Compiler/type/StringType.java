package Compiler.type;

import Compiler.utils.Configuration;

public class StringType extends PrimitiveType {
    public StringType() {
        hyperType = HyperTypes.STRING;
        size = Configuration.REG_SIZE;
    }

    @Override
    public String toString() {
        return "String";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof StringType);
    }
}
