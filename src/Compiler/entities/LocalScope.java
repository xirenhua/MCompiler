package Compiler.entities;

import java.util.HashMap;
import java.util.Map;

public class LocalScope extends Scope {
    public Map<String, VarEntity> varMap;

    public LocalScope(Scope aparentScope) {
        parent = aparentScope;
        varMap = new HashMap<>();
    }

    @Override
    public void putVar(String aname, VarEntity avarEntity) {
        varMap.put(aname, avarEntity);
    }

    @Override
    public void putFunc(String aname, FuncEntity afuncEntity) {
    }

    @Override
    public void putClass(String aname, ClassEntity afuncEntity) {
    }

    @Override
    public boolean checkKey(String name) {
        return varMap.containsKey(name);
    }

    @Override
    public Entity recurGetKey(String name) {
        if (varMap.containsKey(name)) {
            return varMap.get(name);
        } else {
            return parent.recurGetKey(name);
        }
    }
}