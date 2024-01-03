package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;

@ModelItem
public class ParamField {
    public final String name;
    public final TypeHolder<Type> type;

    public ParamField(String name) {
        this.name = name;
        this.type = new TypeHolder<>();
    }
}
