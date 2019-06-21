package Compiler.entities;

import java.util.HashMap;
import java.util.Map;

public class ClassScope extends Scope {
    public Map<String, FuncEntity> funcMap;
    public Map<String, VarEntity> varMap;

    public ClassScope(GlobalScope globalScope) {
        varMap = new HashMap<>();
        funcMap = new HashMap<>();
        parent = globalScope;
    }

    @Override
    public void putVar(String aname, VarEntity avarEntity) {
        varMap.put(aname, avarEntity);
    }

    @Override
    public void putFunc(String aname, FuncEntity afuncEntity) {
        funcMap.put(aname, afuncEntity);
    }

    @Override
    public void putClass(String aname, ClassEntity aclassEntity) {
    }


    @Override
    public boolean checkKey(String name) {
        return varMap.containsKey(name) || funcMap.containsKey(name);
    }

    @Override
    public Entity recurGetKey(String name) {
        if (varMap.containsKey(name)) {
            return varMap.get(name);
        } else if (funcMap.containsKey(name)) {
            return funcMap.get(name);
        } else {
            return parent.recurGetKey(name);
        }
    }
}