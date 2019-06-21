package Compiler.entities;


import Compiler.type.Type;

abstract public class Entity {
    public String name;
    public Type type;

    public Entity(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
