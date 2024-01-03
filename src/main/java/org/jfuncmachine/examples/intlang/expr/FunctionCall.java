package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.compiler.model.expr.CallStaticMethod;
import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.compiler.model.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;

import java.util.Arrays;
import java.util.Map;

@ModelItem(defaultForClass = IntExpr.class, includeStartSymbol = true, varargStart = 1)
public record FunctionCall(String name, IntExpr[] args, String filename, int lineNumber)
        implements IntExpr {
    public Expression generate(Map<String, FunctionDef> functions) {
        FunctionDef funcDef = functions.get(name);
        if (funcDef == null) {
            throw new IntLangException(filename, lineNumber,
                    String.format("Function %s does not exist", name));
        }

        if (args.length != funcDef.argNames().length) {
            throw new IntLangException(filename, lineNumber,
                    String.format("Function %s takes %d arguments, but %d are used here",
                            name, funcDef.argNames().length, args.length));

        }
        Expression[] genArgs = new Expression[args.length];
        for (int i=0; i < genArgs.length; i++) {
            genArgs[i] = args[i].generate(functions);
        }
        Type[] argTypes = new Type[args.length];
        Arrays.fill(argTypes, SimpleTypes.INT);
        return new CallStaticMethod(name, argTypes, SimpleTypes.INT, genArgs);
    }
}
