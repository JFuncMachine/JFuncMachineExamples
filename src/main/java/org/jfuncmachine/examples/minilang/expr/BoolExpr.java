package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.bool.BooleanExpr;
import org.jfuncmachine.examples.minilang.types.BoolType;

public abstract class BoolExpr extends Expr {
    public BoolExpr(String filename, int lineNumber) {
        super(filename, lineNumber);
        this.type.setType(new BoolType(filename, lineNumber));
    }

    public abstract BooleanExpr generateBooleanExpr();
}
