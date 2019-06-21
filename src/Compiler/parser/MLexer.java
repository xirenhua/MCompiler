// Generated from M.g4 by ANTLR 4.7.2
package Compiler.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BOOL=1, INT=2, STRING=3, VOID=4, IF=5, ELSE=6, FOR=7, WHILE=8, BREAK=9, 
		CONTINUE=10, RETURN=11, NEW=12, CLASS=13, THIS=14, LPAREN=15, RPAREN=16, 
		LBRACK=17, RBRACK=18, LBRACE=19, RBRACE=20, SEMI=21, COMMA=22, ADD=23, 
		SUB=24, MUL=25, DIV=26, MOD=27, GT=28, LT=29, EQ=30, NEQ=31, LTE=32, GTE=33, 
		NOT=34, AND=35, OR=36, LSFT=37, RSFT=38, BITNOT=39, BITAND=40, BITOR=41, 
		BITXOR=42, ASSIGN=43, INC=44, DEC=45, DOT=46, NullLiteral=47, BoolLiteral=48, 
		IntLiteral=49, StringLiteral=50, Identifier=51, LINE_COMMENT=52, WS=53;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"BOOL", "INT", "STRING", "VOID", "IF", "ELSE", "FOR", "WHILE", "BREAK", 
			"CONTINUE", "RETURN", "NEW", "CLASS", "THIS", "LPAREN", "RPAREN", "LBRACK", 
			"RBRACK", "LBRACE", "RBRACE", "SEMI", "COMMA", "ADD", "SUB", "MUL", "DIV", 
			"MOD", "GT", "LT", "EQ", "NEQ", "LTE", "GTE", "NOT", "AND", "OR", "LSFT", 
			"RSFT", "BITNOT", "BITAND", "BITOR", "BITXOR", "ASSIGN", "INC", "DEC", 
			"DOT", "NullLiteral", "BoolLiteral", "IntLiteral", "StringLiteral", "EscapeChar", 
			"Identifier", "LINE_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'bool'", "'int'", "'string'", "'void'", "'if'", "'else'", "'for'", 
			"'while'", "'break'", "'continue'", "'return'", "'new'", "'class'", "'this'", 
			"'('", "')'", "'['", "']'", "'{'", "'}'", "';'", "','", "'+'", "'-'", 
			"'*'", "'/'", "'%'", "'>'", "'<'", "'=='", "'!='", "'<='", "'>='", "'!'", 
			"'&&'", "'||'", "'<<'", "'>>'", "'~'", "'&'", "'|'", "'^'", "'='", "'++'", 
			"'--'", "'.'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BOOL", "INT", "STRING", "VOID", "IF", "ELSE", "FOR", "WHILE", 
			"BREAK", "CONTINUE", "RETURN", "NEW", "CLASS", "THIS", "LPAREN", "RPAREN", 
			"LBRACK", "RBRACK", "LBRACE", "RBRACE", "SEMI", "COMMA", "ADD", "SUB", 
			"MUL", "DIV", "MOD", "GT", "LT", "EQ", "NEQ", "LTE", "GTE", "NOT", "AND", 
			"OR", "LSFT", "RSFT", "BITNOT", "BITAND", "BITOR", "BITXOR", "ASSIGN", 
			"INC", "DEC", "DOT", "NullLiteral", "BoolLiteral", "IntLiteral", "StringLiteral", 
			"Identifier", "LINE_COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "M.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\67\u014a\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3"+
		"\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25"+
		"\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34"+
		"\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3!\3\"\3\"\3\"\3#\3"+
		"#\3$\3$\3$\3%\3%\3%\3&\3&\3&\3\'\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3"+
		",\3-\3-\3-\3.\3.\3.\3/\3/\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61"+
		"\3\61\3\61\3\61\3\61\3\61\5\61\u0114\n\61\3\62\3\62\7\62\u0118\n\62\f"+
		"\62\16\62\u011b\13\62\3\62\5\62\u011e\n\62\3\63\3\63\3\63\7\63\u0123\n"+
		"\63\f\63\16\63\u0126\13\63\3\63\3\63\3\64\3\64\3\64\3\64\3\64\3\64\5\64"+
		"\u0130\n\64\3\65\3\65\7\65\u0134\n\65\f\65\16\65\u0137\13\65\3\66\3\66"+
		"\3\66\3\66\7\66\u013d\n\66\f\66\16\66\u0140\13\66\3\66\3\66\3\67\6\67"+
		"\u0145\n\67\r\67\16\67\u0146\3\67\3\67\3\u0124\28\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O"+
		")Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\2i\65k\66m\67\3\2\t\3\2\63;\3\2\62"+
		";\4\2$$^^\4\2C\\c|\6\2\62;C\\aac|\3\2\f\f\5\2\13\f\17\17\"\"\2\u0152\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2"+
		"\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2"+
		"\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2"+
		"\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U"+
		"\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2"+
		"\2\2\2c\3\2\2\2\2e\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\3o\3\2\2\2"+
		"\5t\3\2\2\2\7x\3\2\2\2\t\177\3\2\2\2\13\u0084\3\2\2\2\r\u0087\3\2\2\2"+
		"\17\u008c\3\2\2\2\21\u0090\3\2\2\2\23\u0096\3\2\2\2\25\u009c\3\2\2\2\27"+
		"\u00a5\3\2\2\2\31\u00ac\3\2\2\2\33\u00b0\3\2\2\2\35\u00b6\3\2\2\2\37\u00bb"+
		"\3\2\2\2!\u00bd\3\2\2\2#\u00bf\3\2\2\2%\u00c1\3\2\2\2\'\u00c3\3\2\2\2"+
		")\u00c5\3\2\2\2+\u00c7\3\2\2\2-\u00c9\3\2\2\2/\u00cb\3\2\2\2\61\u00cd"+
		"\3\2\2\2\63\u00cf\3\2\2\2\65\u00d1\3\2\2\2\67\u00d3\3\2\2\29\u00d5\3\2"+
		"\2\2;\u00d7\3\2\2\2=\u00d9\3\2\2\2?\u00dc\3\2\2\2A\u00df\3\2\2\2C\u00e2"+
		"\3\2\2\2E\u00e5\3\2\2\2G\u00e7\3\2\2\2I\u00ea\3\2\2\2K\u00ed\3\2\2\2M"+
		"\u00f0\3\2\2\2O\u00f3\3\2\2\2Q\u00f5\3\2\2\2S\u00f7\3\2\2\2U\u00f9\3\2"+
		"\2\2W\u00fb\3\2\2\2Y\u00fd\3\2\2\2[\u0100\3\2\2\2]\u0103\3\2\2\2_\u0105"+
		"\3\2\2\2a\u0113\3\2\2\2c\u011d\3\2\2\2e\u011f\3\2\2\2g\u012f\3\2\2\2i"+
		"\u0131\3\2\2\2k\u0138\3\2\2\2m\u0144\3\2\2\2op\7d\2\2pq\7q\2\2qr\7q\2"+
		"\2rs\7n\2\2s\4\3\2\2\2tu\7k\2\2uv\7p\2\2vw\7v\2\2w\6\3\2\2\2xy\7u\2\2"+
		"yz\7v\2\2z{\7t\2\2{|\7k\2\2|}\7p\2\2}~\7i\2\2~\b\3\2\2\2\177\u0080\7x"+
		"\2\2\u0080\u0081\7q\2\2\u0081\u0082\7k\2\2\u0082\u0083\7f\2\2\u0083\n"+
		"\3\2\2\2\u0084\u0085\7k\2\2\u0085\u0086\7h\2\2\u0086\f\3\2\2\2\u0087\u0088"+
		"\7g\2\2\u0088\u0089\7n\2\2\u0089\u008a\7u\2\2\u008a\u008b\7g\2\2\u008b"+
		"\16\3\2\2\2\u008c\u008d\7h\2\2\u008d\u008e\7q\2\2\u008e\u008f\7t\2\2\u008f"+
		"\20\3\2\2\2\u0090\u0091\7y\2\2\u0091\u0092\7j\2\2\u0092\u0093\7k\2\2\u0093"+
		"\u0094\7n\2\2\u0094\u0095\7g\2\2\u0095\22\3\2\2\2\u0096\u0097\7d\2\2\u0097"+
		"\u0098\7t\2\2\u0098\u0099\7g\2\2\u0099\u009a\7c\2\2\u009a\u009b\7m\2\2"+
		"\u009b\24\3\2\2\2\u009c\u009d\7e\2\2\u009d\u009e\7q\2\2\u009e\u009f\7"+
		"p\2\2\u009f\u00a0\7v\2\2\u00a0\u00a1\7k\2\2\u00a1\u00a2\7p\2\2\u00a2\u00a3"+
		"\7w\2\2\u00a3\u00a4\7g\2\2\u00a4\26\3\2\2\2\u00a5\u00a6\7t\2\2\u00a6\u00a7"+
		"\7g\2\2\u00a7\u00a8\7v\2\2\u00a8\u00a9\7w\2\2\u00a9\u00aa\7t\2\2\u00aa"+
		"\u00ab\7p\2\2\u00ab\30\3\2\2\2\u00ac\u00ad\7p\2\2\u00ad\u00ae\7g\2\2\u00ae"+
		"\u00af\7y\2\2\u00af\32\3\2\2\2\u00b0\u00b1\7e\2\2\u00b1\u00b2\7n\2\2\u00b2"+
		"\u00b3\7c\2\2\u00b3\u00b4\7u\2\2\u00b4\u00b5\7u\2\2\u00b5\34\3\2\2\2\u00b6"+
		"\u00b7\7v\2\2\u00b7\u00b8\7j\2\2\u00b8\u00b9\7k\2\2\u00b9\u00ba\7u\2\2"+
		"\u00ba\36\3\2\2\2\u00bb\u00bc\7*\2\2\u00bc \3\2\2\2\u00bd\u00be\7+\2\2"+
		"\u00be\"\3\2\2\2\u00bf\u00c0\7]\2\2\u00c0$\3\2\2\2\u00c1\u00c2\7_\2\2"+
		"\u00c2&\3\2\2\2\u00c3\u00c4\7}\2\2\u00c4(\3\2\2\2\u00c5\u00c6\7\177\2"+
		"\2\u00c6*\3\2\2\2\u00c7\u00c8\7=\2\2\u00c8,\3\2\2\2\u00c9\u00ca\7.\2\2"+
		"\u00ca.\3\2\2\2\u00cb\u00cc\7-\2\2\u00cc\60\3\2\2\2\u00cd\u00ce\7/\2\2"+
		"\u00ce\62\3\2\2\2\u00cf\u00d0\7,\2\2\u00d0\64\3\2\2\2\u00d1\u00d2\7\61"+
		"\2\2\u00d2\66\3\2\2\2\u00d3\u00d4\7\'\2\2\u00d48\3\2\2\2\u00d5\u00d6\7"+
		"@\2\2\u00d6:\3\2\2\2\u00d7\u00d8\7>\2\2\u00d8<\3\2\2\2\u00d9\u00da\7?"+
		"\2\2\u00da\u00db\7?\2\2\u00db>\3\2\2\2\u00dc\u00dd\7#\2\2\u00dd\u00de"+
		"\7?\2\2\u00de@\3\2\2\2\u00df\u00e0\7>\2\2\u00e0\u00e1\7?\2\2\u00e1B\3"+
		"\2\2\2\u00e2\u00e3\7@\2\2\u00e3\u00e4\7?\2\2\u00e4D\3\2\2\2\u00e5\u00e6"+
		"\7#\2\2\u00e6F\3\2\2\2\u00e7\u00e8\7(\2\2\u00e8\u00e9\7(\2\2\u00e9H\3"+
		"\2\2\2\u00ea\u00eb\7~\2\2\u00eb\u00ec\7~\2\2\u00ecJ\3\2\2\2\u00ed\u00ee"+
		"\7>\2\2\u00ee\u00ef\7>\2\2\u00efL\3\2\2\2\u00f0\u00f1\7@\2\2\u00f1\u00f2"+
		"\7@\2\2\u00f2N\3\2\2\2\u00f3\u00f4\7\u0080\2\2\u00f4P\3\2\2\2\u00f5\u00f6"+
		"\7(\2\2\u00f6R\3\2\2\2\u00f7\u00f8\7~\2\2\u00f8T\3\2\2\2\u00f9\u00fa\7"+
		"`\2\2\u00faV\3\2\2\2\u00fb\u00fc\7?\2\2\u00fcX\3\2\2\2\u00fd\u00fe\7-"+
		"\2\2\u00fe\u00ff\7-\2\2\u00ffZ\3\2\2\2\u0100\u0101\7/\2\2\u0101\u0102"+
		"\7/\2\2\u0102\\\3\2\2\2\u0103\u0104\7\60\2\2\u0104^\3\2\2\2\u0105\u0106"+
		"\7p\2\2\u0106\u0107\7w\2\2\u0107\u0108\7n\2\2\u0108\u0109\7n\2\2\u0109"+
		"`\3\2\2\2\u010a\u010b\7v\2\2\u010b\u010c\7t\2\2\u010c\u010d\7w\2\2\u010d"+
		"\u0114\7g\2\2\u010e\u010f\7h\2\2\u010f\u0110\7c\2\2\u0110\u0111\7n\2\2"+
		"\u0111\u0112\7u\2\2\u0112\u0114\7g\2\2\u0113\u010a\3\2\2\2\u0113\u010e"+
		"\3\2\2\2\u0114b\3\2\2\2\u0115\u0119\t\2\2\2\u0116\u0118\t\3\2\2\u0117"+
		"\u0116\3\2\2\2\u0118\u011b\3\2\2\2\u0119\u0117\3\2\2\2\u0119\u011a\3\2"+
		"\2\2\u011a\u011e\3\2\2\2\u011b\u0119\3\2\2\2\u011c\u011e\7\62\2\2\u011d"+
		"\u0115\3\2\2\2\u011d\u011c\3\2\2\2\u011ed\3\2\2\2\u011f\u0124\7$\2\2\u0120"+
		"\u0123\5g\64\2\u0121\u0123\n\4\2\2\u0122\u0120\3\2\2\2\u0122\u0121\3\2"+
		"\2\2\u0123\u0126\3\2\2\2\u0124\u0125\3\2\2\2\u0124\u0122\3\2\2\2\u0125"+
		"\u0127\3\2\2\2\u0126\u0124\3\2\2\2\u0127\u0128\7$\2\2\u0128f\3\2\2\2\u0129"+
		"\u012a\7^\2\2\u012a\u0130\7p\2\2\u012b\u012c\7^\2\2\u012c\u0130\7^\2\2"+
		"\u012d\u012e\7^\2\2\u012e\u0130\7$\2\2\u012f\u0129\3\2\2\2\u012f\u012b"+
		"\3\2\2\2\u012f\u012d\3\2\2\2\u0130h\3\2\2\2\u0131\u0135\t\5\2\2\u0132"+
		"\u0134\t\6\2\2\u0133\u0132\3\2\2\2\u0134\u0137\3\2\2\2\u0135\u0133\3\2"+
		"\2\2\u0135\u0136\3\2\2\2\u0136j\3\2\2\2\u0137\u0135\3\2\2\2\u0138\u0139"+
		"\7\61\2\2\u0139\u013a\7\61\2\2\u013a\u013e\3\2\2\2\u013b\u013d\n\7\2\2"+
		"\u013c\u013b\3\2\2\2\u013d\u0140\3\2\2\2\u013e\u013c\3\2\2\2\u013e\u013f"+
		"\3\2\2\2\u013f\u0141\3\2\2\2\u0140\u013e\3\2\2\2\u0141\u0142\b\66\2\2"+
		"\u0142l\3\2\2\2\u0143\u0145\t\b\2\2\u0144\u0143\3\2\2\2\u0145\u0146\3"+
		"\2\2\2\u0146\u0144\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148\3\2\2\2\u0148"+
		"\u0149\b\67\2\2\u0149n\3\2\2\2\f\2\u0113\u0119\u011d\u0122\u0124\u012f"+
		"\u0135\u013e\u0146\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}