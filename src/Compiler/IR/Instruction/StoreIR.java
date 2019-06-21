package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;
import Compiler.IR.Value.StackSlot;
import Compiler.IR.Value.StaticVar;

import java.util.Map;

public class StoreIR extends IRInstruction {

    public IRValue value;
    public int size;
    public IRValue addr;
    public int addrOffset;

    public StoreIR(IRBasicBlock parentBB, IRValue value, int size, Reg addr, int addrOffset) {
        super(parentBB);
        this.value = value;
        this.size = size;
        this.addr = addr;
        this.addrOffset = addrOffset;
        renewUseRegs();
        renewDefReg();
    }

    public StoreIR(IRBasicBlock parentBB, IRValue value, int size, StaticVar address) {
        super(parentBB);
        this.value = value;
        this.size = size;
        this.addr = address;
        this.addrOffset = 0;
        renewUseRegs();
        renewDefReg();
    }

    public StoreIR(IRBasicBlock parentBB, IRValue value, int size, StackSlot address, int addrOffset) {
        super(parentBB);
        this.value = value;
        this.size = size;
        this.addr = address;
        this.addrOffset = addrOffset;
        renewUseRegs();
        renewDefReg();
    }

    @Override
    public void renewDefReg() {
    }

    @Override
    public void mapDefReg(Reg vreg) {
    }

    @Override
    public void renewUseRegs() {
        useRegs.clear();
        if (addr instanceof Reg)
            useRegs.add((Reg) addr);
        if (value instanceof Reg)
            useRegs.add((Reg) value);
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
        if (addr instanceof Reg)
            addr = fullMap.get(addr);
        if (value instanceof Reg)
            value = fullMap.get(value);
        renewUseRegs();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}