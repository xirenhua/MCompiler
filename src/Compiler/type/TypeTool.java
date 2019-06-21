package Compiler.type;

import Compiler.entities.ClassEntity;
import Compiler.utils.SemanticError;

import java.lang.reflect.Array;
import java.util.Map;

public class TypeTool {

    public Map<String, ClassEntity> classMap;

    public TypeTool(Map<String, ClassEntity> amap) {
        classMap = amap;
    }

    public boolean checkDeclType(Type type) {
        // type could only be primitive class array
        if (type instanceof ArrayType) {
            if (((ArrayType) type).baseType instanceof VoidType) {
                return false;
            }
            checkDeclType(((ArrayType) type).baseType);
        }
        if (type instanceof ClassType) {
            if (!classMap.containsKey(((ClassType) type).name)) {
                return false;
            }
        }
        if (type instanceof VoidType) {
            return false;
        }
        return true;
    }

    public boolean checkReturnType(Type type) {
        // type could only be primitive class array
        if (type instanceof ArrayType) {
            if (((ArrayType) type).baseType instanceof VoidType) {
                return false;
            }
            checkReturnType(((ArrayType) type).baseType);
        }
        if (type instanceof ClassType) {
            if (!classMap.containsKey(((ClassType) type).name)) {
                return false;
            }
        }
        return true;
    }

    // varDecl
    // type in ast node: primitive(void) array class
    public boolean checkAssignType(Type typea, Type typeb) {
        if (typea instanceof PrimitiveType) {
            return typea.equals(typeb);
        }
        if (typea instanceof ClassType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        }
        if (typea instanceof ArrayType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        }
        return false;
    }

    // "="
    public boolean checkEQ(Type typea, Type typeb) {
        // here assuming lefttype is valid decltype already
        if (typea instanceof PrimitiveType) {
            return typea.equals(typeb);
        }
        if (typea instanceof ClassType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        }
        if (typea instanceof ArrayType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        } else {
            return false;
        }
    }

    // "== != "
    public boolean checkEQEQ(Type typea, Type typeb) {
        if (typea instanceof NullType) {
            return (typeb instanceof NullType) || (typeb instanceof ClassType) || (typeb instanceof ArrayType);
        }
        if (typea instanceof PrimitiveType) {
            return typea.equals(typeb);
        }
        if (typea instanceof ClassType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        }
        if (typea instanceof ArrayType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        }
        return false;
    }

    // funcCall
    public boolean checkArguments(Type typea, Type typeb) {
        // primitive(no void) class array
        if (typea instanceof PrimitiveType) {
            return typea.equals(typeb);
        }
        if (typea instanceof ClassType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        }
        if (typea instanceof ArrayType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        } else {
            return false;
        }
    }

    // returnexpr
    public boolean checkReturnMatch(Type typea, Type typeb) {
        // typea can be primitive class array null(not NULLTYPE)
        // typeb is guaranteed not null(not NULLTYPE) which means it must return something
        if (typea == null) {
            return false;
        }
        if (typea instanceof VoidType) {
            return false;
        }
        if (typea instanceof PrimitiveType) {
            return typea.equals(typeb);
        }
        if (typea instanceof ClassType || typea instanceof ArrayType) {
            return typea.equals(typeb) || (typeb instanceof NullType);
        }
        return false;
    }
}