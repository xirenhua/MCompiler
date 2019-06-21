package Compiler.compiler;


import Compiler.AST.ProgramNode;
import Compiler.IR.IRRoot;
import Compiler.backend.*;
import Compiler.entities.GlobalScope;
import Compiler.frontend.*;
import Compiler.parser.MLexer;
import Compiler.parser.MParser;
import Compiler.parser.SyntaxErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.InputStream;
import java.io.PrintStream;

public class MCompiler {
    public InputStream inS;
    public PrintStream astOut;
    public PrintStream irOut1;
    public PrintStream irOut2;
    public PrintStream irOut3;
    public PrintStream irOut4;
    public PrintStream irOut5;
    public PrintStream irOut6;
    public PrintStream irOut7;
    public PrintStream nasmOut;
    public boolean hand;


    public MCompiler(InputStream inS, PrintStream astOut, PrintStream irOut1, PrintStream irOut2, PrintStream irOut3, PrintStream irOut4, PrintStream irOut5, PrintStream irOut6, PrintStream irOut7, PrintStream nasmOut) {
        this.inS = inS;
        this.astOut = astOut;
        this.irOut1 = irOut1;
        this.irOut2 = irOut2;
        this.irOut3 = irOut3;
        this.irOut4 = irOut4;
        this.irOut5 = irOut5;
        this.irOut6 = irOut6;
        this.irOut7 = irOut7;
        this.nasmOut = nasmOut;
        hand = false;
    }

    public MCompiler(InputStream inS, PrintStream nasmOut) {
        this.inS = inS;
        this.nasmOut = nasmOut;
        hand = true;
    }

    private ProgramNode buildAST() throws Exception {
        CharStream input = CharStreams.fromStream(inS);
        MLexer lexer = new MLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MParser parser = new MParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new SyntaxErrorListener());
        MParser.ProgramContext tree = parser.program();

        return (ProgramNode) (new AstBuilder().visit(tree));
    }

    private IRRoot buildIR() throws Exception {
        ProgramNode ast = buildAST();

//        new AstPrinter(astOut).visit(ast);

        GlobalClassScanner globalClassScanner = new GlobalClassScanner();
        GlobalScope globalScope = globalClassScanner.globalScope;
        FuncMemberMethodScanner funcMemberMethodScanner = new FuncMemberMethodScanner(globalScope);
        SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder(globalScope);
        IRbuilder iRbuilder = new IRbuilder(globalScope);

        globalClassScanner.visit(ast);
        funcMemberMethodScanner.visit(ast);
        funcMemberMethodScanner.checkMain();
        symbolTableBuilder.visit(ast);
        iRbuilder.visit(ast);

        return iRbuilder.ir;
    }

    public void compile() throws Exception {
        IRRoot ir = buildIR();

        if (hand) {
            new TwoRegOpTransformer(ir).run();

            new GlobalVarResolver(ir).run();

            new FuncArgAllocator(ir).run();

            new LiveAnalysis(ir).run();
            new RegAllocator(ir).run();

            new NASMTransformer(ir).run();

            new ExtraInstructionOptimizer(ir).run();

            new NASMPrinter(nasmOut).visit(ir);
        } else {
            System.out.println("1111111111111111111111111");
            new IRPrinter(irOut1).visit(ir);

            new TwoRegOpTransformer(ir).run();
            System.out.println("2222222222222222222222222");
            new IRPrinter(irOut2).visit(ir);

            new GlobalVarResolver(ir).run();
            System.out.println("3333333333333333333333333");
            new IRPrinter(irOut3).visit(ir);

            new FuncArgAllocator(ir).run();
            System.out.println("4444444444444444444444444");
            new IRPrinter(irOut4).visit(ir);

            new LiveAnalysis(ir).run();
            new RegAllocator(ir).run();
            System.out.println("5555555555555555555555555");
            new IRPrinter(irOut5).visit(ir);

            new NASMTransformer(ir).run();
            System.out.println("6666666666666666666666666");
            new IRPrinter(irOut6).visit(ir);

            new ExtraInstructionOptimizer(ir).run();
            System.out.println("7777777777777777777777777");
            new IRPrinter(irOut7).visit(ir);

            System.out.println("fuckfuckfuckfuckfuckfuck");
            new NASMPrinter(nasmOut).visit(ir);
        }
    }
}


// r1: nothing
// r2: tworeg
// r3: static
// r4: funcarg
// r5: regalloc
// r6: nasmtran
