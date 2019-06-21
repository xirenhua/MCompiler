package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRFunction;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;

import java.util.List;
import java.util.Map;

public class FuncCallIR extends IRInstruction {

    public IRFunction func;
    public List<IRValue> args;
    public Reg dest;

    public FuncCallIR(IRBasicBlock parentBB, IRFunction func, List<IRValue> args, Reg dest) {
        super(parentBB);
        this.func = func;
        this.args = args;
        this.dest = dest;
        renewUseRegs();
        renewDefReg();
    }

    @Override
    public void renewDefReg() {
        defReg = dest;
    }

    @Override
    public void mapDefReg(Reg vreg) {
        dest = vreg;
        renewDefReg();
    }

    @Override
    public void renewUseRegs() {
        useRegs.clear();
        for (IRValue arg : args) {
            if (arg instanceof Reg)
                useRegs.add((Reg) arg);
        }
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
        for (int i = 0; i < args.size(); ++i) {
            if (args.get(i) instanceof Reg) {
                args.set(i, fullMap.get(args.get(i)));
            }
        }
        renewUseRegs();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}