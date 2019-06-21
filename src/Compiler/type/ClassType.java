package Compiler.type;

import Compiler.utils.Configuration;

public class ClassType extends Type {
    public String name;

    public ClassType(String name) {
        hyperType = HyperTypes.CLASS;
        size = Configuration.REG_SIZE;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClassType)) return false;
        return name.equals(((ClassType) obj).name);
    }

    @Override
    public String toString() {
        return String.format("Class: %s", name);
    }
}