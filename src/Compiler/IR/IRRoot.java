package Compiler.IR;

import Compiler.AST.Stat.StatNode;
import Compiler.IR.Value.*;
import Compiler.NASM.NASMReg;
import Compiler.NASM.NASMRegSet;

import java.util.*;

public class IRRoot {
    public List<StaticVar> staticVarList;
    public Map<String, StaticString> staticStrList;
    public Map<String, IRFunction> funcs;
    public Map<String, IRFunction> biFuncs;
    public boolean hasDivShift;
    public PhysicalReg preg0, preg1;
    public Map<StatNode, ForRecord> forRecMap;

    public static class ForRecord {
        public IRBasicBlock condBB, stepBB, bodyBB, afterBB;
        public boolean processed = false;

        public ForRecord(IRBasicBlock condBB, IRBasicBlock stepBB, IRBasicBlock bodyBB, IRBasicBlock afterBB) {
            this.condBB = condBB;
            this.stepBB = stepBB;
            this.bodyBB = bodyBB;
            this.afterBB = afterBB;
        }
    }

    public IRRoot() {
        staticVarList = new ArrayList<>();
        staticStrList = new HashMap<>();
        funcs = new HashMap<>();
        biFuncs = new HashMap<>();
        hasDivShift = false;
        preg0 = preg1 = null;
        forRecMap = new HashMap<>();
        addBuiltInFunc();
    }

    public void addFunc(IRFunction func) {
        funcs.put(func.name, func);
    }

    public IRFunction getFunc(String name) {
        return funcs.get(name);
    }

    public void addBiFunc(IRFunction func) {
        biFuncs.put(func.name, func);
    }

    public IRFunction getBiFunc(String name) {
        return biFuncs.get(name);
    }

    public void addStaticVar(StaticVar data) {
        staticVarList.add(data);
    }

    public void addStaticStr(StaticString statStr) {
        staticStrList.put(statStr.value, statStr);
    }

    public StaticString getStaticStr(String str) {
        return staticStrList.get(str);
    }

    public void updateRecursiveCalleeSet() {
        Set<IRFunction> tmpRecursiveCalleeSet = new HashSet<>();
        for (IRFunction irFunction : funcs.values()) {
            irFunction.recurCalleeSet.clear();
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (IRFunction irFunction : funcs.values()) {
                tmpRecursiveCalleeSet.clear();
                tmpRecursiveCalleeSet.addAll(irFunction.calleeSet);
                for (IRFunction calleeFunction : irFunction.calleeSet) {
                    tmpRecursiveCalleeSet.addAll(calleeFunction.recurCalleeSet);
                }
                if (!tmpRecursiveCalleeSet.equals(irFunction.recurCalleeSet)) {
                    irFunction.recurCalleeSet.clear();
                    irFunction.recurCalleeSet.addAll(tmpRecursiveCalleeSet);
                    changed = true;
                }
            }
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public void addBuiltInFunc() {
        IRFunction func;

        func = new IRFunction(BUILTIN_STRING_CONCAT, "__builtin_string_concat");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_STRING_EQUAL, "__builtin_string_equal");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_STRING_INEQUAL, "__builtin_string_inequal");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_STRING_LESS, "__builtin_string_less");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_STRING_LESS_EQUAL, "__builtin_string_less_equal");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_PRINT, "_Z5printPc");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_PRINTLN, "_Z7printlnPc");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_PRINT_INT, "_Z8printInti");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_PRINTLN_INT, "_Z10printlnInti");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_GET_STRING, "_Z9getStringv");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_GET_INT, "_Z6getIntv");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_TO_STRING, "_Z8toStringi");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_STRING_SUBSTRING, "_Z27__member___string_substringPcii");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_STRING_PARSEINT, "_Z26__member___string_parseIntPc");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);

        func = new IRFunction(BUILTIN_STRING_ORD, "_Z21__member___string_ordPci");
        func.refPRegs.addAll(NASMRegSet.generalRegs);
        addBiFunc(func);
    }

    static public final String BUILTIN_STRING_CONCAT = "@@str@concat";
    static public final String BUILTIN_STRING_EQUAL = "@@str@equal";
    static public final String BUILTIN_STRING_INEQUAL = "@@str@inequal";
    static public final String BUILTIN_STRING_LESS = "@@str@less";
    static public final String BUILTIN_STRING_LESS_EQUAL = "@@str@lessequal";

    static public final String BUILTIN_PRINT = "print";
    static public final String BUILTIN_PRINTLN = "println";
    static public final String BUILTIN_PRINT_INT = "printInt";
    static public final String BUILTIN_PRINTLN_INT = "printlnInt";
    static public final String BUILTIN_GET_STRING = "getString";
    static public final String BUILTIN_GET_INT = "getInt";
    static public final String BUILTIN_TO_STRING = "toString";
    static public final String BUILTIN_STRING_LENGTH = "@@str@length";
    static public final String BUILTIN_STRING_SUBSTRING = "@@str@substring";
    static public final String BUILTIN_STRING_PARSEINT = "@@str@parseInt";
    static public final String BUILTIN_STRING_ORD = "@@str@ord";
    static public final String BUILTIN_ARRAY_SIZE = "@@arr@size";
}
