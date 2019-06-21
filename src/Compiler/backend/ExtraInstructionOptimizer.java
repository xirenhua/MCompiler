package Compiler.backend;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRFunction;
import Compiler.IR.IRRoot;
import Compiler.IR.Instruction.IRInstruction;
import Compiler.IR.Instruction.LoadIR;
import Compiler.IR.Instruction.MoveIR;
import Compiler.IR.Instruction.StoreIR;

public class ExtraInstructionOptimizer {
    private IRRoot ir;

    public ExtraInstructionOptimizer(IRRoot ir) {
        this.ir = ir;
    }

    public void run() {
        for (IRFunction func : ir.funcs.values()) {
            for (IRBasicBlock bb : func.revPostOrder) {
                for (IRInstruction inst = bb.firstInst, lastInst = null; inst != null; inst = inst.nextInst) {
                    boolean remove = false;
                    if (inst instanceof MoveIR) {
                        if (((MoveIR) inst).lhs == ((MoveIR) inst).rhs) remove = true;
                        else if (lastInst instanceof MoveIR &&
                                ((MoveIR) inst).lhs == ((MoveIR) lastInst).rhs &&
                                ((MoveIR) inst).rhs == ((MoveIR) lastInst).lhs) remove = true;
                    } else if (inst instanceof LoadIR) {
                        if (lastInst instanceof StoreIR &&
                                ((StoreIR) lastInst).value == ((LoadIR) inst).dest &&
                                ((StoreIR) lastInst).addr == ((LoadIR) inst).addr &&
                                ((StoreIR) lastInst).addrOffset == ((LoadIR) inst).addrOffset &&
                                ((StoreIR) lastInst).size == ((LoadIR) inst).size) remove = true;
                    } else if (inst instanceof StoreIR) {
                        if (lastInst instanceof LoadIR &&
                                ((LoadIR) lastInst).dest == ((StoreIR) inst).value &&
                                ((LoadIR) lastInst).addr == ((StoreIR) inst).addr &&
                                ((LoadIR) lastInst).addrOffset == ((StoreIR) inst).addrOffset &&
                                ((LoadIR) lastInst).size == ((StoreIR) inst).size) remove = true;
                    }
                    if (remove) inst.remove();
                    else lastInst = inst;
                }
            }
        }
    }
}