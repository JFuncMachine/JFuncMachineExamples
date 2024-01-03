package org.jfuncmachine.examples;

import org.jfuncmachine.compiler.classgen.ClassGenerator;
import org.jfuncmachine.compiler.classgen.ClassGeneratorOptions;
import org.jfuncmachine.compiler.classgen.ClassGeneratorOptionsBuilder;
import org.jfuncmachine.compiler.model.*;
import org.jfuncmachine.compiler.model.expr.*;
import org.jfuncmachine.compiler.model.expr.bool.BinaryComparison;
import org.jfuncmachine.compiler.model.expr.bool.tests.Tests;
import org.jfuncmachine.compiler.model.expr.constants.LongConstant;
import org.jfuncmachine.compiler.model.expr.javainterop.CallJavaConstructor;
import org.jfuncmachine.compiler.model.expr.javainterop.CallJavaMethod;
import org.jfuncmachine.compiler.model.expr.javainterop.GetJavaStaticField;
import org.jfuncmachine.compiler.model.inline.Inlines;
import org.jfuncmachine.compiler.model.types.*;

public class FactorialTimingLocal {
    public static void main(String[] args) {
        try {
            MethodDef factMethod = new MethodDef("fact", Access.PUBLIC,
                    new Field[] { new Field("n", SimpleTypes.LONG),
                        new Field("acc", SimpleTypes.LONG)},
                    SimpleTypes.LONG,
                    new If(new BinaryComparison(Tests.LE, new GetValue("n", SimpleTypes.LONG),
                            new LongConstant(1)),
                            new GetValue("acc", SimpleTypes.LONG),
                            new CallMethod("fact", new Type[] { SimpleTypes.LONG, SimpleTypes.LONG},
                                    SimpleTypes.LONG,
                                    new GetValue("this", new ObjectType()),
                                    new Expression[] {
                                        new InlineCall(Inlines.LongSub, new Expression[] {
                                                new GetValue("n", SimpleTypes.LONG),
                                                new LongConstant(1)}),
                                        new InlineCall(Inlines.LongAdd, new Expression[] {
                                                new GetValue("acc", SimpleTypes.LONG),
                                                new GetValue("n", SimpleTypes.LONG)
                                        })

                                    })
                            ));

            MethodDef mainMethod = new MethodDef("main", Access.PUBLIC + Access.STATIC,
                    new Field[] { new Field("args", new ArrayType(SimpleTypes.STRING))},
                    SimpleTypes.UNIT,
                    new CallJavaMethod("java.io.PrintStream", "println",
                            // Get the PrintStream object from System.out, that is the object
                            // that we will be calling println on
                            SimpleTypes.UNIT, new GetJavaStaticField("java.lang.System", "out",
                            new ObjectType("java.io.PrintStream")),
                            new Expression[] { new CallMethod("fact", new Type[] { SimpleTypes.LONG, SimpleTypes.LONG },
                                SimpleTypes.LONG,
                                    new CallJavaConstructor(null, new Expression[0]),
                                    new Expression[] {
                                            new LongConstant(100000000), new LongConstant(1)
                                    })}));

            ConstructorDef constructor = ConstructorDef.generateWith(new Field[0]);

            ClassGeneratorOptions options = new ClassGeneratorOptionsBuilder()
                    .withLocalTailCallsToLoops(true)
                    .withFullTailCalls(false)
                    .build();

            ClassDef factClass = new ClassDef("org.jfuncmachine.examples", "FactorialWithLocal",
                    Access.PUBLIC, new MethodDef[] { constructor, factMethod, mainMethod }, new ClassField[0], new String[0]);
            ClassGenerator generator = new ClassGenerator(options);
            generator.generate(factClass, "test");

            options = new ClassGeneratorOptionsBuilder()
                    .withLocalTailCallsToLoops(false)
                    .withFullTailCalls(true)
                    .build();

            factClass = new ClassDef("org.jfuncmachine.examples", "FactorialWithFullTail",
                    Access.PUBLIC, new MethodDef[] { constructor, factMethod, mainMethod }, new ClassField[0], new String[0]);
            generator = new ClassGenerator(options);
            generator.generate(factClass, "test");
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }
}
