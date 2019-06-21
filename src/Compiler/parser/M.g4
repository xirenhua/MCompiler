grammar M;

program: declaration* EOF;

declaration
    : varDecl
    | funcDecl
    | classDecl
    ;

varDecl: type Identifier (ASSIGN varInit)? SEMI;

classDecl: CLASS Identifier classBody;

funcDecl: type Identifier paramList funcBody;

constructorDecl: Identifier paramList funcBody;

varInit: expr;

paramList: LPAREN (paramDecl (COMMA paramDecl)*)? RPAREN;

paramDecl: type Identifier;

funcBody: LBRACE stat* RBRACE;

classBody:
    LBRACE (varDecl | funcDecl | constructorDecl)* RBRACE;

type: atomType (LBRACK empty RBRACK)*;

atomType
    : primitiveType
    | classType
    ;

primitiveType
    : token = BOOL
    | token = INT
    | token = VOID
    | token = STRING
    ;

classType
    : token = Identifier
    ;

stat
    : IF LPAREN expr RPAREN stat (ELSE stat)?               # IfStat
    | WHILE LPAREN expr RPAREN stat                         # WhileStat
    | FOR LPAREN forInit = expr? SEMI forCondition = expr? SEMI forUpdate = expr? RPAREN stat   # ForStat
    | BREAK SEMI                                            # BreakStat
    | CONTINUE SEMI                                         # ContinueStat
    | RETURN expr? SEMI                                     # ReturnStat
    | varDecl                                               # VarDeclStat
    | expr SEMI                                             # ExprStat
    | LBRACE stat* RBRACE                                   # BlockStat
    | SEMI                                                  # EmptyStat
    ;

expr
    : expr postfix = (INC | DEC)                            # PostfixExpr
    | NEW creator                                           # NewExpr
    | expr exprlist                                         # FuncCallExpr
    | expr LBRACK expr RBRACK                               # ArrayExpr
    | expr DOT Identifier                                   # MemberExpr

    | <assoc = right> prefix = (INC | DEC) expr             # PrefixExpr
    | <assoc = right> prefix = (ADD | SUB) expr             # UnaryExpr
    | <assoc = right> prefix = (NOT | BITNOT) expr          # UnaryExpr

    | left = expr op = (MUL | DIV | MOD) right = expr       # BinaryExpr
    | left = expr op = (ADD | SUB) right = expr             # BinaryExpr
    | left = expr op = (LSFT | RSFT) right = expr           # BinaryExpr
    | left = expr op = (LT | GT | LTE | GTE) right = expr   # BinaryExpr
    | left = expr op = (EQ | NEQ) right = expr              # BinaryExpr
    | left = expr op = BITAND right = expr                  # BinaryExpr
    | left = expr op = BITXOR right = expr                  # BinaryExpr
    | left = expr op = BITOR right = expr                   # BinaryExpr
    | left = expr op = AND right = expr                     # BinaryExpr
    | left = expr op = OR right = expr                      # BinaryExpr

    | <assoc = right> left = expr op = ASSIGN right = expr  # AssignExpr

    | LPAREN expr RPAREN                                    # PrimaryExpr
    | token = THIS                                          # PrimaryExpr
    | token = Identifier                                    # PrimaryExpr
    | token = BoolLiteral                                   # PrimaryExpr
    | token = IntLiteral                                    # PrimaryExpr
    | token = StringLiteral                                 # PrimaryExpr
    | token = NullLiteral                                   # PrimaryExpr
    ;

exprlist: LPAREN (expr (COMMA expr)*)? RPAREN;

creator:
    (classType (LPAREN RPAREN)?) | (atomType (LBRACK expr RBRACK)* (LBRACK empty RBRACK)* );

empty: ;


// Keywords
BOOL: 'bool';
INT: 'int';
STRING: 'string';
VOID: 'void';
IF: 'if';
ELSE: 'else';
FOR: 'for';
WHILE: 'while';
BREAK: 'break';
CONTINUE: 'continue';
RETURN: 'return';
NEW: 'new';
CLASS: 'class';
THIS: 'this';

// Separator
LPAREN: '(';
RPAREN: ')';
LBRACK: '[';
RBRACK: ']';
LBRACE: '{';
RBRACE: '}';
SEMI: ';';
COMMA: ',';

// Operator Arithmetic
ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
MOD: '%';
// Relation
GT: '>';
LT: '<';
EQ: '==';
NEQ: '!=';
LTE: '<=';
GTE: '>=';
// Logic
NOT: '!';
AND: '&&';
OR: '||';
// Bit
LSFT: '<<';
RSFT: '>>';
BITNOT: '~';
BITAND: '&';
BITOR: '|';
BITXOR: '^';
// Assign
ASSIGN: '=';
// Inc&dec
INC: '++';
DEC: '--';
// Member
DOT: '.';

// Literal
NullLiteral: 'null';
BoolLiteral: 'true' | 'false';
IntLiteral: ([1-9][0-9]* | '0');
StringLiteral: '"' ( EscapeChar | ~[\\"])*? '"';

fragment EscapeChar: ( '\\n' | '\\\\' | '\\"');

Identifier: [a-zA-Z][0-9a-zA-Z_]*;

LINE_COMMENT: '//' ~[\n]* -> channel(HIDDEN);
WS: [ \t\r\n]+ -> channel(HIDDEN);