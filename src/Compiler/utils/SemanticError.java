package Compiler.utils;


import Compiler.AST.TokenLocation;

public class SemanticError extends Error {
    public TokenLocation location;
    public String message;

    public SemanticError(TokenLocation alocation, String amessage) {
        super(String.format("[Semantic Error] at %s: %s", alocation.toString(), amessage));
        location = alocation;
        message = amessage;
    }

    public SemanticError(String amessage) {
        super(String.format("[Semantic Error] : %s", amessage));
        location = null;
        message = amessage;
    }


    @Override
    public String toString() {
        return String.format("[Semantic Error] at %s: %s", location.toString(), message);
    }
}
