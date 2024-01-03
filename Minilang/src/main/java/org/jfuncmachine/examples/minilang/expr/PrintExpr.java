package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.If;
import org.jfuncmachine.compiler.model.expr.bool.UnaryComparison;
import org.jfuncmachine.compiler.model.expr.bool.tests.Tests;
import org.jfuncmachine.compiler.model.expr.constants.StringConstant;
import org.jfuncmachine.compiler.model.expr.javainterop.CallJavaMethod;
import org.jfuncmachine.compiler.model.expr.javainterop.GetJavaStaticField;
import org.jfuncmachine.compiler.model.types.ObjectType;
import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.MinilangException;
import org.jfuncmachine.examples.minilang.types.BoolType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.examples.minilang.types.UnitType;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(symbol="print")
public class PrintExpr extends Expr {
    public final Expr expr;
    protected TypeHolder exprType;
    public PrintExpr(Expr expr, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.expr = expr;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> unitType = new TypeHolder<>(new UnitType(filename, lineNumber));
        other.unify(unitType);
        exprType = new TypeHolder<>();
        expr.unify(exprType, env);
        type.unify(unitType);
    }

    public Expression generate() {
        if (!exprType.isFull()) {
            throw new MinilangException(
                    String.format("%s %d: Can't determine type of expression to print",
                            filename, lineNumber));
        }

        Expression exprToPrint;
        if (exprType.concreteType instanceof BoolType) {
            exprToPrint = new If(new UnaryComparison(Tests.IsTrue, expr.generate()),
                    new StringConstant("true"), new StringConstant("false"),
                    expr.filename, expr.lineNumber);
        } else {
            exprToPrint = expr.generate();
        }

        return new CallJavaMethod("java.io.PrintStream", "println",
                // Get the PrintStream object from System.out, that is the object
                // that we will be calling println on
                SimpleTypes.UNIT, new GetJavaStaticField("java.lang.System", "out",
                new ObjectType(java.io.PrintStream.class)),
                // Load up the arguments to println, which is just one, that is a string constant
                new Expression[] { exprToPrint });
    }
}
