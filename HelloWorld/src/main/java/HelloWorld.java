import org.jfuncmachine.compiler.classgen.ClassGenerator;
import org.jfuncmachine.compiler.model.Access;
import org.jfuncmachine.compiler.model.ClassDef;
import org.jfuncmachine.compiler.model.ClassField;
import org.jfuncmachine.compiler.model.MethodDef;
import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.constants.StringConstant;
import org.jfuncmachine.compiler.model.expr.javainterop.CallJavaMethod;
import org.jfuncmachine.compiler.model.expr.javainterop.GetJavaStaticField;
import org.jfuncmachine.compiler.model.types.ArrayType;
import org.jfuncmachine.compiler.model.types.Field;
import org.jfuncmachine.compiler.model.types.ObjectType;
import org.jfuncmachine.compiler.model.types.SimpleTypes;

import java.lang.reflect.Method;

public class HelloWorld {
    public static void main(String[] args) {
        try {
            // Create a public static method named "main"
            MethodDef main = new MethodDef("main", Access.PUBLIC + Access.STATIC,
                    new Field[]{new Field("args", new ArrayType(SimpleTypes.STRING))},
                    SimpleTypes.UNIT,
                    new CallJavaMethod("java.io.PrintStream", "println",
                            SimpleTypes.UNIT, new GetJavaStaticField("java.lang.System", "out",
                            new ObjectType("java.io.PrintStream")),
                            new Expression[]{new StringConstant("Hello World!")}
                    ));

            ClassDef newClass = new ClassDef(null, "HelloWorld",
                    Access.PUBLIC,
                    new MethodDef[]{main}, new ClassField[0], new String[0]);

            ClassGenerator generator = new ClassGenerator();
            generator.generate(newClass, "out");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
