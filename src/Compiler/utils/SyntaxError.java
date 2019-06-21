package Compiler.utils;


import Compiler.AST.TokenLocation;

public class SyntaxError extends Error {
    public TokenLocation location;
    public String message;

    public SyntaxError(TokenLocation alocation, String amessage) {
        super(String.format("[Syntax Error] at %s: %s", alocation.toString(), amessage));
        location = alocation;
        message = amessage;
    }

    public SyntaxError(String amessage) {
        super(String.format("[Syntax Error] : %s", amessage));
        location = null;
        message = amessage;
    }

    @Override
    public String toString() {
        return String.format("[Syntax Error] at %s: %s", location.toString(), message);
    }
}
