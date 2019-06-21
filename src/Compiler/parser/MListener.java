// Generated from M.g4 by ANTLR 4.7.2
package Compiler.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MParser}.
 */
public interface MListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(MParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(MParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(MParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(MParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void enterClassDecl(MParser.ClassDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void exitClassDecl(MParser.ClassDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#funcDecl}.
	 * @param ctx the parse tree
	 */
	void enterFuncDecl(MParser.FuncDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#funcDecl}.
	 * @param ctx the parse tree
	 */
	void exitFuncDecl(MParser.FuncDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#constructorDecl}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDecl(MParser.ConstructorDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#constructorDecl}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDecl(MParser.ConstructorDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#varInit}.
	 * @param ctx the parse tree
	 */
	void enterVarInit(MParser.VarInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#varInit}.
	 * @param ctx the parse tree
	 */
	void exitVarInit(MParser.VarInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(MParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(MParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#paramDecl}.
	 * @param ctx the parse tree
	 */
	void enterParamDecl(MParser.ParamDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#paramDecl}.
	 * @param ctx the parse tree
	 */
	void exitParamDecl(MParser.ParamDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#funcBody}.
	 * @param ctx the parse tree
	 */
	void enterFuncBody(MParser.FuncBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#funcBody}.
	 * @param ctx the parse tree
	 */
	void exitFuncBody(MParser.FuncBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(MParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(MParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#atomType}.
	 * @param ctx the parse tree
	 */
	void enterAtomType(MParser.AtomTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#atomType}.
	 * @param ctx the parse tree
	 */
	void exitAtomType(MParser.AtomTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(MParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(MParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(MParser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(MParser.ClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterIfStat(MParser.IfStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitIfStat(MParser.IfStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhileStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterWhileStat(MParser.WhileStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhileStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitWhileStat(MParser.WhileStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterForStat(MParser.ForStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitForStat(MParser.ForStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BreakStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterBreakStat(MParser.BreakStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BreakStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitBreakStat(MParser.BreakStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ContinueStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterContinueStat(MParser.ContinueStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ContinueStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitContinueStat(MParser.ContinueStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterReturnStat(MParser.ReturnStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitReturnStat(MParser.ReturnStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VarDeclStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclStat(MParser.VarDeclStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VarDeclStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclStat(MParser.VarDeclStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterExprStat(MParser.ExprStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitExprStat(MParser.ExprStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BlockStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterBlockStat(MParser.BlockStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BlockStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitBlockStat(MParser.BlockStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EmptyStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStat(MParser.EmptyStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EmptyStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStat(MParser.EmptyStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PostfixExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpr(MParser.PostfixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PostfixExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpr(MParser.PostfixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterArrayExpr(MParser.ArrayExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitArrayExpr(MParser.ArrayExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMemberExpr(MParser.MemberExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMemberExpr(MParser.MemberExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(MParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(MParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(MParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(MParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(MParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(MParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(MParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(MParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrefixExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPrefixExpr(MParser.PrefixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrefixExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPrefixExpr(MParser.PrefixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAssignExpr(MParser.AssignExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAssignExpr(MParser.AssignExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FuncCallExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallExpr(MParser.FuncCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FuncCallExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallExpr(MParser.FuncCallExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#exprlist}.
	 * @param ctx the parse tree
	 */
	void enterExprlist(MParser.ExprlistContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#exprlist}.
	 * @param ctx the parse tree
	 */
	void exitExprlist(MParser.ExprlistContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreator(MParser.CreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreator(MParser.CreatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#empty}.
	 * @param ctx the parse tree
	 */
	void enterEmpty(MParser.EmptyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#empty}.
	 * @param ctx the parse tree
	 */
	void exitEmpty(MParser.EmptyContext ctx);
}