package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.If;
import org.jfuncmachine.compiler.model.expr.bool.And;
import org.jfuncmachine.compiler.model.expr.bool.BooleanExpr;
import org.jfuncmachine.compiler.model.expr.bool.Or;
import org.jfuncmachine.compiler.model.expr.bool.UnaryComparison;
import org.jfuncmachine.compiler.model.expr.bool.tests.Tests;
import org.jfuncmachine.compiler.model.expr.constants.IntConstant;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.BoolType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(includeStartSymbol = true)
public class BoolBinaryExpr extends BoolExpr {
    @ModelItem(isExprStart = true)
    public enum ExprType {
        And ("and"),
        Or ("or");

        public final String symbol;

        ExprType(String symbol) {
            this.symbol = symbol;
        }

    }

    public final ExprType exprType;
    public final Expr left;
    public final Expr right;
    public BoolBinaryExpr(ExprType exprType, Expr left, Expr right, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.exprType = exprType;
        this.left = left;
        this.right = right;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> boolType = new TypeHolder<>(new BoolType(filename, lineNumber));
        left.unify(boolType, env);
        right.unify(boolType, env);
        other.unify(boolType);
        this.type.unify(boolType);
    }

    public Expression generate() {
        return new If(generateBooleanExpr(), new IntConstant(1), new IntConstant(0));
    }

    public BooleanExpr generateBooleanExpr() {
        BooleanExpr leftExpr;
        if (left instanceof BoolExpr leftBool) {
            leftExpr = leftBool.generateBooleanExpr();
        } else {
            leftExpr = new UnaryComparison(Tests.IsTrue, left.generate(), left.filename, left.lineNumber);
        }

        BooleanExpr rightExpr;
        if (right instanceof BoolExpr rightBool) {
            rightExpr = rightBool.generateBooleanExpr();
        } else {
            rightExpr = new UnaryComparison(Tests.IsTrue, right.generate(), right.filename, right.lineNumber);
        }

        return switch (exprType) {
            case And -> new And(leftExpr, rightExpr, filename, lineNumber);
            case Or -> new Or(leftExpr, rightExpr, filename, lineNumber);
        };
    }
}
