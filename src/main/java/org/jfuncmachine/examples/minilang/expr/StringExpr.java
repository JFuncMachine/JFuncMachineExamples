package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.examples.minilang.types.StringType;

public abstract class StringExpr extends Expr {
    public StringExpr(String filename, int lineNumber) {
        super(filename, lineNumber);
        this.type.setType(new StringType(filename, lineNumber));
    }
}
