package Compiler.entities;

import java.util.HashMap;
import java.util.Map;

public class GlobalScope extends Scope {
    public Map<String, ClassEntity> classMap;
    public Map<String, FuncEntity> funcMap;
    public Map<String, VarEntity> varMap;

    public GlobalScope() {
        parent = null;
        classMap = new HashMap<>();
        funcMap = new HashMap<>();
        varMap = new HashMap<>();
    }

    public void putFunc(String aname, FuncEntity afuncEntity) {
        funcMap.put(aname, afuncEntity);
    }

    public void putClass(String aname, ClassEntity aclassEntity) {
        classMap.put(aname, aclassEntity);
    }

    @Override
    public void putVar(String aname, VarEntity avarEntity) {
        varMap.put(aname, avarEntity);
    }


    @Override
    public boolean checkKey(String name) {
        return classMap.containsKey(name) || funcMap.containsKey(name) || varMap.containsKey(name);
    }

    @Override
    public Entity recurGetKey(String name) {
        if (varMap.containsKey(name)) {
            return varMap.get(name);
        } else if (funcMap.containsKey(name)) {
            return funcMap.get(name);
        } else {
            return null;
        }
    }
}