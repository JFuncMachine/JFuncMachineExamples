package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.sexprlang.translate.ModelItem;

import java.util.Map;

@ModelItem
public interface IntExpr {
    Expression generate(Map<String,FunctionDef> functions);
}
