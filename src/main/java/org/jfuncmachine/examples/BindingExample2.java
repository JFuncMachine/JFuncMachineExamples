package org.jfuncmachine.examples;

import org.jfuncmachine.compiler.classgen.ClassGenerator;
import org.jfuncmachine.compiler.classgen.ClassGeneratorOptions;
import org.jfuncmachine.compiler.classgen.ClassGeneratorOptionsBuilder;
import org.jfuncmachine.compiler.model.Access;
import org.jfuncmachine.compiler.model.MethodDef;
import org.jfuncmachine.compiler.model.expr.Binding;
import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.GetValue;
import org.jfuncmachine.compiler.model.expr.InlineCall;
import org.jfuncmachine.compiler.model.expr.constants.IntConstant;
import org.jfuncmachine.compiler.model.inline.Inlines;
import org.jfuncmachine.compiler.model.types.Field;
import org.jfuncmachine.compiler.model.types.SimpleTypes;

public class BindingExample2 {
    public static void main(String[] args) {
        try {
            MethodDef sumMethod = new MethodDef("sum", Access.PUBLIC,
                    new Field[] { },
                    SimpleTypes.INT,
                    new Binding(
                            new Binding.BindingPair[] {
                                    new Binding.BindingPair("x", new IntConstant(20)),
                                    new Binding.BindingPair("y",
                                            new InlineCall(Inlines.IntAdd,
                                                    new Expression[] {
                                                        new GetValue("x", SimpleTypes.INT),
                                                        new IntConstant(2)
                                                    }))
                            },
                            Binding.Visibility.Previous,
                            new InlineCall(Inlines.IntAdd,
                                    new Expression[] {
                                            new GetValue("x", SimpleTypes.INT),
                                            new GetValue("y", SimpleTypes.INT)
                                    })));

            ClassGeneratorOptions options = new ClassGeneratorOptionsBuilder()
                    .withLocalTailCallsToLoops(false)
                    .withFullTailCalls(true)
                    .build();
            ClassGenerator generator = new ClassGenerator(options);
            Object result = generator.invokeMethod("BindingExample", sumMethod);
            System.out.println(result);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }
}
