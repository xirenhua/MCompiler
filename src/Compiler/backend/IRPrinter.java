package Compiler.backend;

import Compiler.IR.IRVisitor;
import Compiler.IR.IRRoot;
import Compiler.IR.IRFunction;
import Compiler.IR.IRBasicBlock;
import Compiler.IR.Value.*;
import Compiler.IR.Instruction.*;

import java.io.PrintStream;
import java.util.*;

public class IRPrinter implements IRVisitor {
    private PrintStream out;

    public IRPrinter(PrintStream out) {
        this.out = out;
    }

    private Map<IRBasicBlock, String> bbMap = new HashMap<>();
    private Map<VirtualReg, String> vregMap = new HashMap<>();
    private Map<StaticVar, String> staticVarMap = new HashMap<>();
    private Map<StaticString, String> staticStrMap = new HashMap<>();

    private Map<String, Integer> bbCnt = new HashMap<>();
    private Map<String, Integer> vregCnt = new HashMap<>();
    private Map<String, Integer> staticDataCnt = new HashMap<>();
    private Map<String, Integer> staticStringCnt = new HashMap<>();


    private Set<IRBasicBlock> bbVisited = new HashSet<>();

    private boolean isStaticDef;

    private String genID(String name, Map<String, Integer> cnt) {
        int cntName = cnt.getOrDefault(name, 0) + 1;
        cnt.put(name, cntName);
        return name + "_" + cntName;
    }

    private String getBBID(IRBasicBlock bb) {
        String id = bbMap.get(bb);
        if (id == null) {
            if (bb.name == null) {
                id = genID("bb", bbCnt);
            } else {
                id = genID("bb_" + bb.name, bbCnt);
            }
            bbMap.put(bb, id);
        }
        return id;
    }

    private String getVRegID(VirtualReg vreg) {
        String id = vregMap.get(vreg);
        if (id == null) {
            if (vreg.name == null) {
                id = genID("vg", vregCnt);
            } else {
                id = genID("vg_" + vreg.name, vregCnt);
            }
            vregMap.put(vreg, id);
        }
        return id;
    }

    private String getStaticDataID(StaticVar data) {
        String id = staticVarMap.get(data);
        if (id == null) {
            if (data.name == null) {
                id = genID("sd", staticDataCnt);
            } else {
                id = genID("sd_" + data.name, staticDataCnt);
            }
            staticVarMap.put(data, id);
        }
        return id;
    }

    private String getStaticStrID(StaticString data) {
        String id = staticStrMap.get(data);
        if (id == null) {
            if (data.value == null) {
                id = genID("sd", staticStringCnt);
            } else {
                id = genID("sd_" + data.value, staticStringCnt);
            }
            staticStrMap.put(data, id);
        }
        return id;
    }

    @Override
    public void visit(IRRoot node) {
        isStaticDef = true;
        for (StaticVar staticData : node.staticVarList) {
            staticData.accept(this);
        }
        for (StaticString staticStr : node.staticStrList.values()) {
            staticStr.accept(this);
        }
        isStaticDef = false;
        out.println();
        for (IRFunction func : node.funcs.values()) {
            func.accept(this);
        }
    }

    @Override
    public void visit(IRFunction node) {
        vregMap = new IdentityHashMap<>();
        vregCnt = new HashMap<>();
        out.printf("func %s ", node.name);
        for (VirtualReg paraVReg : node.argVRegList) {
            out.printf("$%s ", getVRegID(paraVReg));
        }
        out.printf("{\n");
        for (IRBasicBlock bb : node.revPostOrder) {
            bb.accept(this);
        }
        out.printf("}\n\n");
    }

    @Override
    public void visit(IRBasicBlock node) {
        if (bbVisited.contains(node)) return;
        bbVisited.add(node);
        out.println("%" + getBBID(node) + ":");
        for (IRInstruction inst = node.firstInst; inst != null; inst = inst.nextInst) {
            inst.accept(this);
        }
    }

    @Override
    public void visit(BranchIR node) {
        out.print("    br ");
        node.cond.accept(this);
        out.println(" %" + getBBID(node.thenBB) + " %" + getBBID(node.elseBB));
        out.println();
    }

    @Override
    public void visit(JumpIR node) {
        out.printf("    jump %%%s\n\n", getBBID(node.targetBB));
    }

    @Override
    public void visit(ReturnIR node) {
        out.print("    ret ");
        if (node.retValue != null) {
            node.retValue.accept(this);
        } else {
            out.print("0");
        }
        out.println();
        out.println();
    }

    @Override
    public void visit(UnaryIR node) {
        out.print("    ");
        String op = null;
        switch (node.op) {
            case NEG:
                op = "neg";
                break;
            case BITWISE_NOT:
                op = "not";
                break;
        }
        node.dest.accept(this);
        out.printf(" = %s ", op);
        node.rhs.accept(this);
        out.println();
    }

    @Override
    public void visit(BinaryIR node) {
        out.print("    ");
        String op = null;
        switch (node.op) {
            case ADD:
                op = "add";
                break;
            case SUB:
                op = "sub";
                break;
            case MUL:
                op = "mul";
                break;
            case DIV:
                op = "div";
                break;
            case MOD:
                op = "rem";
                break;
            case SHL:
                op = "shl";
                break;
            case SHR:
                op = "shr";
                break;
            case BITWISE_AND:
                op = "and";
                break;
            case BITWISE_OR:
                op = "or";
                break;
            case BITWISE_XOR:
                op = "xor";
                break;
        }
        node.dest.accept(this);
        out.printf(" = %s ", op);
        node.lhs.accept(this);
        out.printf(" ");
        node.rhs.accept(this);
        out.println();
    }

    @Override
    public void visit(CompIR node) {
        out.print("    ");
        String op = null;
        switch (node.op) {
            case EQUAL:
                op = "seq";
                break;
            case INEQUAL:
                op = "sne";
                break;
            case GREATER:
                op = "sgt";
                break;
            case GREATER_EQUAL:
                op = "sge";
                break;
            case LESS:
                op = "slt";
                break;
            case LESS_EQUAL:
                op = "sle";
                break;
        }
        node.dest.accept(this);
        out.printf(" = %s ", op);
        node.lhs.accept(this);
        out.printf(" ");
        node.rhs.accept(this);
        out.println();
    }

    @Override
    public void visit(MoveIR node) {
        out.print("    ");
        node.lhs.accept(this);
        out.print(" = move ");
        node.rhs.accept(this);
        out.println();
    }

    @Override
    public void visit(LoadIR node) {
        out.print("    ");
        node.dest.accept(this);
        out.printf(" = load %d ", node.size);
        node.addr.accept(this);
        out.println(" " + node.addrOffset);
    }

    @Override
    public void visit(StoreIR node) {
        out.printf("    store %d ", node.size);
        node.addr.accept(this);
        out.print(" ");
        node.value.accept(this);
        out.println(" " + node.addrOffset);
    }

    @Override
    public void visit(FuncCallIR node) {
        out.print("    ");
        if (node.dest != null) {
            node.dest.accept(this);
            out.print(" = ");
        }
        out.printf("call %s ", node.func.name);
        for (IRValue arg : node.args) {
            arg.accept(this);
            out.print(" ");
        }
        out.println();
    }

    @Override
    public void visit(HeapAllocIR node) {
        out.print("    ");
        node.dest.accept(this);
        out.print(" = alloc ");
        node.allocSize.accept(this);
        out.println();
    }

    @Override
    public void visit(VirtualReg node) {
        out.print("$" + getVRegID(node));
    }

    @Override
    public void visit(Imm node) {
        out.print(node.value);
    }

    @Override
    public void visit(StaticVar node) {
        if (isStaticDef) out.printf("space @%s %d\n", getStaticDataID(node), node.size);
        else out.print("@" + getStaticDataID(node));
    }

    @Override
    public void visit(StaticString node) {
        if (isStaticDef) out.printf("asciiz @%s %s\n", getStaticStrID(node), node.value);
        else out.print("@" + getStaticStrID(node));
    }

    @Override
    public void visit(PhysicalReg node) {
        out.print("$" + node.name);
    }

    @Override
    public void visit(PushIR node) {
        out.print("    push ");
        node.value.accept(this);
        out.println();
    }

    @Override
    public void visit(PopIR node) {
        out.print("    pop ");
        node.preg.accept(this);
        out.println();
    }
}