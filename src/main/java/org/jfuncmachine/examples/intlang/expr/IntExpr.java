package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;

import java.util.Map;

public interface IntExpr {
    Expression generate(Map<String,FunctionDef> functions);
}
