package Compiler;

import java.io.*;

import Compiler.compiler.MCompiler;

public class Main {
    public static void main(String[] args) throws Exception {
        InputStream in;
        PrintStream ast, ir1, ir2, ir3, ir4, ir5, ir6, ir7, nasm;

        in = System.in;
        nasm = System.out;
        MCompiler mcompiler = new MCompiler(in, nasm);
//        in = new FileInputStream("i");
//        ast = new PrintStream(new FileOutputStream("ast"));
//        ir1 = new PrintStream(new FileOutputStream("r1"));
//        ir2 = new PrintStream(new FileOutputStream("r2"));
//        ir3 = new PrintStream(new FileOutputStream("r3"));
//        ir4 = new PrintStream(new FileOutputStream("r4"));
//        ir5 = new PrintStream(new FileOutputStream("r5"));
//        ir6 = new PrintStream(new FileOutputStream("r6"));
//        ir7 = new PrintStream(new FileOutputStream("r7"));
//        nasm = new PrintStream(new FileOutputStream("out.asm"));
//        MCompiler mcompiler = new MCompiler(in, ast, ir1, ir2, ir3, ir4, ir5, ir6, ir7, nasm);
        try {
            mcompiler.compile();
        } catch (Error e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
