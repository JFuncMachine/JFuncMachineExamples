package org.jfuncmachine.examples.minilang;

import org.jfuncmachine.compiler.classgen.ClassGenerator;
import org.jfuncmachine.compiler.classgen.ClassGeneratorOptions;
import org.jfuncmachine.compiler.classgen.ClassGeneratorOptionsBuilder;
import org.jfuncmachine.compiler.model.*;
import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.conv.ToUnit;
import org.jfuncmachine.compiler.model.types.ArrayType;
import org.jfuncmachine.compiler.model.types.Field;
import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.examples.minilang.expr.Expr;
import org.jfuncmachine.examples.minilang.types.FuncType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.parser.Parser;
import org.jfuncmachine.sexprlang.parser.SexprItem;
import org.jfuncmachine.sexprlang.parser.SexprList;
import org.jfuncmachine.sexprlang.translate.ModelMapper;
import org.jfuncmachine.sexprlang.translate.SexprToModel;
import org.jfuncmachine.util.unification.TypeHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please supply an input filename");
            return;
        }

        try {
            SexprItem item = Parser.parseFile(args[0], true, new MinilangSymbolMatcher());

            if (!(item instanceof SexprList sexprList)) {
                System.out.println("Internal error, parser did not return a list");
                return;
            }

            File argFile = new File(args[0]);
            String className = argFile.getName().substring(0, argFile.getName().indexOf("."));
            if (!Character.isUpperCase(className.charAt(0))) {
                className = Character.toUpperCase(className.charAt(0))+className.substring(1);
            }

            ArrayList<Func> funcs = new ArrayList<>();
            boolean parsedBody = false;

            Object[] exprs = SexprToModel.sexprsToModel(sexprList,
                    new ModelMapper("org.jfuncmachine.examples.minilang"),
                    Expr.class);

            Expression mainExpr = null;

            List<MethodDef> methods = new ArrayList<>();

            Map<String, FuncType> definedFunctions = new HashMap<>();

            for (Object exprObj: exprs) {
                if (exprObj instanceof Func func) {
                    definedFunctions.put(func.name, func.getType());
                }
            }

            for (Object exprObj: exprs) {
                if (exprObj instanceof Func func) {
                    Environment<TypeHolder<Type>> env = new Environment<>();
                    for (String key: definedFunctions.keySet()) {
                        env.define(key, new TypeHolder<>(definedFunctions.get(key)));
                    }
                    func.unify(env);
                    definedFunctions.put(func.name, func.getType());
                    MethodDef method = func.generate();
                    methods.add(method);
                } else if (exprObj instanceof Expr expr) {
                    if (mainExpr != null) {
                        throw new MinilangException(
                                String.format("%s %d: There can only be one top-level main expression",
                                        expr.filename, expr.lineNumber));
                    }
                    TypeHolder<Type> exprType = new TypeHolder<>();
                    Environment<TypeHolder<Type>> env = new Environment<>();
                    for (String key: definedFunctions.keySet()) {
                        env.define(key, new TypeHolder<>(definedFunctions.get(key)));
                    }
                    expr.unify(exprType, env);
                    mainExpr = expr.generate();
                }
            }

            if (mainExpr != null) {
                if (!mainExpr.getType().equals(SimpleTypes.UNIT)) {
                    mainExpr = new ToUnit(mainExpr, mainExpr.filename, mainExpr.lineNumber);
                }

                MethodDef main = new MethodDef("main", Access.PUBLIC + Access.STATIC,
                        new Field[]{
                                new Field("args", new ArrayType(SimpleTypes.STRING))
                        }, SimpleTypes.UNIT, mainExpr);
                methods.add(main);
            }

            methods.add(ConstructorDef.generateWith(new Field[0], mainExpr.filename, 0));

            ClassDef classDef = new ClassDef("", className, Access.PUBLIC,
                    methods.toArray(new MethodDef[0]), new ClassField[0], new String[0]);

            ClassGeneratorOptions options =
                    new ClassGeneratorOptionsBuilder()
                            .withFullTailCalls(true)
                            .build();
            ClassGenerator generator = new ClassGenerator(options);
            generator.generate(classDef, "out");

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
