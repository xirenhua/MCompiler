package Compiler.NASM;

import Compiler.IR.Value.PhysicalReg;

public class NASMReg extends PhysicalReg {

    public NASMReg(String name, boolean isGeneral, boolean isCallerSave, boolean isCalleeSave, int posArgId) {
        super(name, isGeneral, isCallerSave, isCalleeSave, posArgId);
    }
}
