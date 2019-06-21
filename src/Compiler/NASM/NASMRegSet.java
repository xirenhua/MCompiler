package Compiler.NASM;

import Compiler.IR.Value.PhysicalReg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NASMRegSet {
    public static final Collection<PhysicalReg> allRegs, generalRegs, callerSaveRegs, calleeSaveRegs;
    public static final NASMReg rax, rcx, rdx, rbx, rsi, rdi, rsp, rbp, r8, r9, r10, r11, r12, r13, r14, r15;
    public static final List<PhysicalReg> posArgRegs;

    static {
        allRegs = new ArrayList<>();
        generalRegs = new ArrayList<>();
        callerSaveRegs = new ArrayList<>();
        calleeSaveRegs = new ArrayList<>();
        posArgRegs = new ArrayList<>();

        rax = new NASMReg("rax", false, true, false, -1);
        rcx = new NASMReg("rcx", false, true, false, 3);
        rdx = new NASMReg("rdx", false, true, false, 2);
        rbx = new NASMReg("rbx", false, false, true, -1);
        rsi = new NASMReg("rsi", false, true, false, 1);
        rdi = new NASMReg("rdi", false, true, false, 0);
        rsp = new NASMReg("rsp", false, true, false, -1);
        rbp = new NASMReg("rbp", false, false, true, -1);
        r8 = new NASMReg("r8", true, true, false, 4);
        r9 = new NASMReg("r9", true, true, false, 5);
        r10 = new NASMReg("r10", true, true, false, -1);
        r11 = new NASMReg("r11", true, true, false, -1);
        r12 = new NASMReg("r12", true, false, true, -1);
        r13 = new NASMReg("r13", true, false, true, -1);
        r14 = new NASMReg("r14", true, false, true, -1);
        r15 = new NASMReg("r15", true, false, true, -1);

        allRegs.add(rax);
        allRegs.add(rcx);
        allRegs.add(rdx);
        allRegs.add(rbx);
        allRegs.add(rsi);
        allRegs.add(rdi);
        allRegs.add(rsp);
        allRegs.add(rbp);
        allRegs.add(r8);
        allRegs.add(r9);
        allRegs.add(r10);
        allRegs.add(r11);
        allRegs.add(r12);
        allRegs.add(r13);
        allRegs.add(r14);
        allRegs.add(r15);

        posArgRegs.add(rdi);
        posArgRegs.add(rsi);
        posArgRegs.add(rdx);
        posArgRegs.add(rcx);
        posArgRegs.add(r8);
        posArgRegs.add(r9);

        for (PhysicalReg phyReg : allRegs) {
            if (phyReg.isGeneral) {
                generalRegs.add(phyReg);
            }
            if (phyReg.isCallerSave) {
                callerSaveRegs.add(phyReg);
            }
            if (phyReg.isCalleeSave) {
                calleeSaveRegs.add(phyReg);
            }
        }
    }
}
