// Generated from M.g4 by ANTLR 4.7.2
package Compiler.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(MParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(MParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#classDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDecl(MParser.ClassDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#funcDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDecl(MParser.FuncDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#constructorDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDecl(MParser.ConstructorDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#varInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarInit(MParser.VarInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(MParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#paramDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamDecl(MParser.ParamDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#funcBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncBody(MParser.FuncBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#classBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBody(MParser.ClassBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(MParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#atomType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomType(MParser.AtomTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(MParser.PrimitiveTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#classType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassType(MParser.ClassTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStat(MParser.IfStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhileStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStat(MParser.WhileStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStat(MParser.ForStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BreakStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStat(MParser.BreakStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContinueStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStat(MParser.ContinueStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStat(MParser.ReturnStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarDeclStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclStat(MParser.VarDeclStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStat(MParser.ExprStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BlockStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStat(MParser.BlockStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EmptyStat}
	 * labeled alternative in {@link MParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmptyStat(MParser.EmptyStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PostfixExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixExpr(MParser.PostfixExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayExpr(MParser.ArrayExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MemberExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberExpr(MParser.MemberExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpr(MParser.BinaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpr(MParser.NewExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpr(MParser.PrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(MParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PrefixExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixExpr(MParser.PrefixExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignExpr(MParser.AssignExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FuncCallExpr}
	 * labeled alternative in {@link MParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCallExpr(MParser.FuncCallExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#exprlist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprlist(MParser.ExprlistContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#creator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreator(MParser.CreatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MParser#empty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmpty(MParser.EmptyContext ctx);
}