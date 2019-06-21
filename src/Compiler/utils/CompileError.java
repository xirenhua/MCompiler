package Compiler.utils;


import Compiler.AST.TokenLocation;

public class CompileError extends Error {
    public TokenLocation location;
    public String message;

    public CompileError(TokenLocation alocation, String amessage) {
        super(String.format("[Compile Error] at %s: %s", alocation.toString(), amessage));
        location = alocation;
        message = amessage;
    }

    public CompileError(String amessage) {
        super(String.format("[Compile Error] : %s", amessage));
        location = null;
        message = amessage;
    }

    @Override
    public String toString() {
        return String.format("[Compile Error] at %s: %s", location.toString(), message);
    }
}
