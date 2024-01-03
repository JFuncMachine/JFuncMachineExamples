package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.sexprlang.translate.ModelItem;

@ModelItem(symbol = "define")
public record FunctionDef(String name, String[] params, IntExpr body) {

}
