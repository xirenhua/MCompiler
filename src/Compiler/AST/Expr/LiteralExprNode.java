package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;
import org.antlr.v4.runtime.Token;

import static Compiler.parser.MParser.*;

public class LiteralExprNode extends ExprNode {
    public String typename;
    public String value;

    public LiteralExprNode(Token token) {
        location = new TokenLocation(token);
        switch (token.getType()) {
            case IntLiteral:
                typename = "int";
                value = token.getText();
                break;
            case NullLiteral:
                typename = "null";
                value = token.getText();
                break;
            case BoolLiteral:
                typename = "bool";
                value = token.getText();
                break;
            default:    //case STRING_LITERAL:
                typename = "string";
                value = escape(token.getText());
        }
    }

    private String escape(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = string.length();
        for (int i = 1; i < length - 1; i++) {
            char c = string.charAt(i);
            if (c == '\\') {
                char nc = string.charAt(i + 1);
                switch (nc) {
                    case 'n':
                        stringBuilder.append('\n');
                        break;
                    case '\\':
                        stringBuilder.append('\\');
                        break;
                    case '"':
                        stringBuilder.append('"');
                        break;
                    default:
                        stringBuilder.append(nc);
                }
                i++;
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
