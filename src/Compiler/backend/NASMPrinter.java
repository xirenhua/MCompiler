package Compiler.backend;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRFunction;
import Compiler.IR.IRRoot;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.*;
import Compiler.IR.Instruction.*;
import Compiler.utils.CompileError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static Compiler.IR.Instruction.BinaryIR.IRBinaryOp.*;

public class NASMPrinter implements IRVisitor {

    public PrintStream out;
    public Map<String, Integer> idCounter;
    public Map<Object, String> idMap;
    public PhysicalReg preg0, preg1;
    public boolean isBssSection, isDataSection;

    public NASMPrinter(PrintStream out) {
        this.out = out;
        idCounter = new HashMap<>();
        idMap = new HashMap<>();
    }

    public String newId(String id) {
        int nowCnt = idCounter.getOrDefault(id, 0) + 1;
        idCounter.put(id, nowCnt);
        return id + "_" + nowCnt;
    }

    public String bbId(IRBasicBlock bb) {
        String id = idMap.get(bb);
        if (id == null) {
            id = "__block_" + newId(bb.name);
            idMap.put(bb, id);
        }
        return id;
    }

    public String dataId(StaticVar data) {
        String id = idMap.get(data);
        if (id == null) {
            id = "__static_data_" + newId(data.name);
            idMap.put(data, id);
        }
        return id;
    }

    public String strId(StaticString data) {
        String id = idMap.get(data);
        if (id == null) {
            id = "__static_str_" + newId("string");
            idMap.put(data, id);
        }
        return id;
    }

    @Override
    public void visit(IRRoot node) {
        preg0 = node.preg0;
        preg1 = node.preg1;

        idMap.put(node.funcs.get("main").startBB, "main");

        out.println("\t\tglobal\tmain");
        out.println();

        out.println("\t\textern\tmalloc");
        out.println();

        if (node.staticVarList.size() > 0) {
            isBssSection = true;
            out.println("\t\tsection\t.bss");
            for (StaticVar staticData : node.staticVarList) {
                staticData.accept(this);
            }
            out.println();
            isBssSection = false;
        }

        if (node.staticStrList.size() > 0) {
            isDataSection = true;
            out.println("\t\tsection\t.data");
            for (StaticString staticString : node.staticStrList.values()) {
                staticString.accept(this);
            }
            out.println();
            isDataSection = false;
        }

        out.println("\t\tsection\t.text\n");
        for (IRFunction irFunction : node.funcs.values()) {
            irFunction.accept(this);
        }
        out.println();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("lib/builtin_functions.asm"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                out.println(line);
            }
        } catch (IOException e) {
            throw new CompileError("IO exception when reading builtin functions from file");
        }
    }

    @Override
    public void visit(IRFunction node) {
        out.printf("; function %s\n\n", node.name);
        for (IRBasicBlock bb : node.revPostOrder) {
            bb.accept(this);
        }
    }

    @Override
    public void visit(IRBasicBlock node) {
        out.printf("%s:\n", bbId(node));
        for (IRInstruction inst = node.firstInst; inst != null; inst = inst.nextInst) {
            inst.accept(this);
        }
        out.println();
    }


    @Override
    public void visit(BranchIR node) {
        if (node.cond instanceof Imm) {
            int boolValue = ((Imm) node.cond).value;
            out.printf("\t\tjmp\t\t%s\n", boolValue == 1 ? bbId(node.thenBB) : bbId(node.elseBB));
            return;
        }
        out.print("\t\tcmp\t\t");
        node.cond.accept(this);
        out.println(", 1");
        out.printf("\t\tje\t\t%s\n", bbId(node.thenBB));
        if (node.elseBB.postIndex + 1 == node.parentBB.postIndex) return;
        out.printf("\t\tjmp\t\t%s\n", bbId(node.elseBB));
    }

    @Override
    public void visit(JumpIR node) {
        if (node.targetBB.postIndex + 1 == node.parentBB.postIndex) return;
        out.printf("\t\tjmp\t\t%s\n", bbId(node.targetBB));
    }

    @Override
    public void visit(ReturnIR node) {
        out.println("\t\tret");
    }

    @Override
    public void visit(UnaryIR node) {
        String op;
        switch (node.op) {
            case BITWISE_NOT:
                op = "not";
                break;
            case NEG:
                op = "neg";
                break;
            default:
                throw new CompileError("invalid unary operation");
        }
        out.print("\t\tmov\t\t");
        node.dest.accept(this);
        out.print(", ");
        node.rhs.accept(this);
        out.print("\n\t\t" + op + "\t\t");
        node.dest.accept(this);
        out.println();
    }

    @Override
    public void visit(BinaryIR node) {
        if (node.op == DIV || node.op == MOD) {
            // to be optimized: not pushing rdx
            out.print("\t\tmov\t\trbx, ");
            node.rhs.accept(this);
            out.println();
            out.print("\t\tmov\t\trax, ");
            node.lhs.accept(this);
            out.println();
            out.println("\t\tmov\t\t" + preg0.name + ", rdx");
            out.println("\t\tcdq");
            out.println("\t\tdiv\trbx");
            out.print("\t\tmov\t\t");
            node.dest.accept(this);
            if (node.op == DIV) {
                out.println(", rax");
            } else {
                out.println(", rdx");
            }
            out.println("\t\tmov\t\trdx, " + preg0.name);
        } else if (node.op == SHL ||
                node.op == SHR) {
            out.println("\t\tmov\t\trbx, rcx");
            out.print("\t\tmov\t\trcx, ");
            node.rhs.accept(this);
            if (node.op == SHL) {
                out.print("\n\t\tsal\t\t");
            } else {
                out.print("\n\t\tsar\t\t");
            }
            node.lhs.accept(this);
            out.println(", cl");
            out.println("\t\tmov\t\trcx, rbx");
            out.print("\t\tand\t\t");
            node.lhs.accept(this);
            out.println(", -1");

        } else {
            if (node.dest != node.lhs)
                throw new CompileError("binary operation should have same dest and lhs");
            String op;
            switch (node.op) {
                case ADD:
                    if (node.rhs instanceof Imm && ((Imm) node.rhs).value == 1) {
                        out.print("\t\tinc\t\t");
                        node.lhs.accept(this);
                        out.println();
                        return;
                    }
                    op = "add\t";
                    break;
                case SUB:
                    if (node.rhs instanceof Imm && ((Imm) node.rhs).value == 1) {
                        out.print("\t\tdec\t\t");
                        node.lhs.accept(this);
                        out.println();
                        return;
                    }
                    op = "sub\t";
                    break;
                case MUL:
                    if (node.rhs instanceof Imm && ((Imm) node.rhs).value == 1) {
                        return;
                    }
                    op = "imul";
                    break;
                case BITWISE_OR:
                    op = "or\t";
                    break;
                case BITWISE_XOR:
                    op = "xor\t";
                    break;
                case BITWISE_AND:
                    op = "and\t";
                    break;
                default:
                    throw new CompileError("invalid binary operation");
            }
            out.print("\t\t" + op + "\t");
            node.lhs.accept(this);
            out.print(", ");
            node.rhs.accept(this);
            out.println();
        }
    }

    @Override
    public void visit(CompIR node) {
        if (node.lhs instanceof PhysicalReg) {
            out.print("\t\tand\t\t");
            node.lhs.accept(this);
            out.println(", -1");
        }
        if (node.rhs instanceof PhysicalReg) {
            out.print("\t\tand\t\t");
            node.rhs.accept(this);
            out.println(", -1");
        }
        out.println("\t\txor\t\trax, rax");
        out.print("\t\tcmp\t\t");
        node.lhs.accept(this);
        out.print(", ");
        node.rhs.accept(this);
        out.println();
        String op;
        switch (node.op) {
            case EQUAL:
                op = "sete";
                break;
            case INEQUAL:
                op = "setne";
                break;
            case LESS:
                op = "setl";
                break;
            case LESS_EQUAL:
                op = "setle";
                break;
            case GREATER:
                op = "setg";
                break;
            case GREATER_EQUAL:
                op = "setge";
                break;
            default:
                throw new CompileError("invalid comparison operation");
        }
        out.println("\t\t" + op + "\tal");
        out.print("\t\tmov\t\t");
        node.dest.accept(this);
        out.println(", rax");
    }

    public String sizeStr(int memSize) {
        String sizeStr;
        switch (memSize) {
            case 1:
                sizeStr = "byte";
                break;
            case 2:
                sizeStr = "word";
                break;
            case 4:
                sizeStr = "dword";
                break;
            case 8:
                sizeStr = "qword";
                break;
            default:
                throw new CompileError("invalid load size: " + memSize);
        }
        return sizeStr;
    }

    @Override
    public void visit(MoveIR node) {
        out.print("\t\tmov\t\t");
        node.lhs.accept(this);
        out.print(", ");
        node.rhs.accept(this);
        out.println();
    }

    @Override
    public void visit(LoadIR node) {
        if (node.addr instanceof StaticString) {
            out.print("\t\tmov\t\t");
            node.dest.accept(this);
            out.print(", " + sizeStr(node.size) + " ");
            node.addr.accept(this);
            out.println();
            return;
        }
        out.print("\t\tmov\t\t");
        node.dest.accept(this);
        out.print(", " + sizeStr(node.size) + " [");
        node.addr.accept(this);
        if (node.addrOffset < 0) {
            out.print(node.addrOffset);
        } else if (node.addrOffset > 0) {
            out.print("+" + node.addrOffset);
        }
        out.println("]");
    }

    @Override
    public void visit(StoreIR node) {
        if (node.addr instanceof StaticString) {
            out.print("\t\tmov\t\t" + sizeStr(node.size) + " ");
            node.addr.accept(this);
            out.print(" ");
            node.value.accept(this);
            out.println();
            return;
        }
        out.print("\t\tmov\t\t" + sizeStr(node.size) + " [");
        node.addr.accept(this);
        if (node.addrOffset < 0) {
            out.print(node.addrOffset);
        } else if (node.addrOffset > 0) {
            out.print("+" + node.addrOffset);
        }
        out.print("], ");
        node.value.accept(this);
        out.println();
    }

    @Override
    public void visit(FuncCallIR node) {
        if (node.func.isBuitIn) out.println("\t\tcall\t" + node.func.builinLabel);
        else out.println("\t\tcall\t" + bbId(node.func.startBB));
    }

    @Override
    public void visit(PushIR node) {
        out.print("\t\tpush\t");
        node.value.accept(this);
        out.println();
    }

    @Override
    public void visit(PopIR node) {
        out.print("\t\tpop\t\t");
        node.preg.accept(this);
        out.println();
    }

    @Override
    public void visit(HeapAllocIR node) {
        out.println("\t\tcall\tmalloc");
    }


    @Override
    public void visit(Imm node) {
        out.print(node.value);
    }


    @Override
    public void visit(VirtualReg node) {
        throw new CompileError("should not visit virtual register node in NASMPrinter");
    }

    @Override
    public void visit(PhysicalReg node) {
        out.print(node.name);
    }


    private String staticStrDataSection(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = str.length(); i < n; ++i) {
            char c = str.charAt(i);
            sb.append((int) c);
            sb.append(", ");
        }
        sb.append(0);
        return sb.toString();
    }

    @Override
    public void visit(StaticVar node) {
        if (isBssSection) {
            String op;
            switch (node.size) {
                case 1:
                    op = "resb";
                    break;
                case 2:
                    op = "resw";
                    break;
                case 4:
                    op = "resd";
                    break;
                case 8:
                    op = "resq";
                    break;
                default:
                    throw new CompileError("invalid static data size");
            }
            out.printf("%s:\t%s\t1\n", dataId(node), op);
        } else out.print(dataId(node));
    }

    @Override
    public void visit(StaticString node) {
        if (isDataSection) {
            out.printf("%s:\n", strId(node));
            out.printf("\t\tdq\t\t%d\n", node.value.length());
            out.printf("\t\tdb\t\t%s\n", staticStrDataSection(node.value));
        } else {
            out.print(strId(node));
        }
    }
}