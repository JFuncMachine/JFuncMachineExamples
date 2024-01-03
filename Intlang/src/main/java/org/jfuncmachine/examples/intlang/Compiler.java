package org.jfuncmachine.examples.intlang;

import org.jfuncmachine.compiler.classgen.ClassGenerator;
import org.jfuncmachine.compiler.classgen.ClassGeneratorOptions;
import org.jfuncmachine.compiler.classgen.ClassGeneratorOptionsBuilder;
import org.jfuncmachine.compiler.model.Access;
import org.jfuncmachine.compiler.model.ClassDef;
import org.jfuncmachine.compiler.model.ClassField;
import org.jfuncmachine.compiler.model.MethodDef;
import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.javainterop.CallJavaMethod;
import org.jfuncmachine.compiler.model.expr.javainterop.GetJavaStaticField;
import org.jfuncmachine.compiler.model.types.ArrayType;
import org.jfuncmachine.compiler.model.types.Field;
import org.jfuncmachine.compiler.model.types.ObjectType;
import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.examples.intlang.expr.FunctionDef;
import org.jfuncmachine.examples.intlang.expr.IntExpr;
import org.jfuncmachine.sexprlang.parser.Parser;
import org.jfuncmachine.sexprlang.parser.SexprItem;
import org.jfuncmachine.sexprlang.parser.SexprList;
import org.jfuncmachine.sexprlang.translate.ModelMapper;
import org.jfuncmachine.sexprlang.translate.SexprToModel;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please supply a filename");
        }

        try {
            SexprItem item = Parser.parseFile(args[0], true, new IntlangSymbolMatcher());

            if (!(item instanceof SexprList itemList)) {
                System.out.println("Error, the parsed file is not a list");
                return;
            }

            File argFile = new File(args[0]);
            String className = argFile.getName().substring(0, argFile.getName().indexOf("."));
            if (!Character.isUpperCase(className.charAt(0))) {
                className = Character.toUpperCase(className.charAt(0))+className.substring(1);
            }

            Map<String, FunctionDef> functions = new HashMap<>();
            boolean parsedMainExpr = false;

            Object[] exprs = SexprToModel.sexprsToModel(itemList,
                    new ModelMapper("org.jfuncmachine.exampes.intlang.expr"),
                    IntExpr.class);

            for (Object obj: exprs) {
                if (obj instanceof FunctionDef functionDef) {
                    functions.put(functionDef.name(), functionDef);
                }
            }

            List<MethodDef> methods = new ArrayList<>();

            for (Object obj: exprs) {
                if (obj instanceof FunctionDef functionDef) {
                    Field[] fields = new Field[functionDef.argNames().length];
                    for (int i=0; i < fields.length; i++) {
                        fields[i] = new Field(functionDef.argNames()[i], SimpleTypes.INT);
                    }
                    methods.add(new MethodDef(functionDef.name(), Access.PUBLIC + Access.STATIC,
                            fields, SimpleTypes.INT, functionDef.body().generate(functions)));
                } else if (obj instanceof IntExpr mainExpr) {
                    if (parsedMainExpr) {
                        System.out.println("File should only contain one expression for the main method");
                        return;
                    }
                    methods.add(new MethodDef("main", Access.PUBLIC + Access.STATIC,
                            new Field[] { new Field("args", new ArrayType(SimpleTypes.STRING)) },
                            SimpleTypes.UNIT,
                            new CallJavaMethod("java.io.PrintStream", "println",
                                    SimpleTypes.UNIT,
                                    new GetJavaStaticField("java.lang.System", "out",
                                            new ObjectType(PrintStream.class)),
                                            new Expression[] { mainExpr.generate(functions) })));
                    parsedMainExpr = true;
                }
            }

            ClassDef classDef = new ClassDef("", className, Access.PUBLIC,
                    methods.toArray(new MethodDef[0]), new ClassField[0], new String[0]);

            ClassGeneratorOptions options = new ClassGeneratorOptionsBuilder()
                    .build();

            ClassGenerator generator = new ClassGenerator(options);
            generator.generate(classDef, "intlangout");

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
