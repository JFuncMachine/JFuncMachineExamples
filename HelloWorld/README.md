+++
title = 'Helloworld'
date = 2023-12-12T17:50:23-05:00
draft = true
+++
## Introduction

Because JFuncMachine is a library that let you describe what kind of
code to compile, it is possible to write a simple "Hello World" program
with it. Normally you wouldn't write programs directly in JFuncMachine
because it is meant to be the back-end to a language compiler.

A typical Java "Hello World" program looks like this:
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
```

What you can see in the code above is that you need to define
a class that contains a static method named `main` that takes
an array of strings as an argument, and then invokes
`System.out.println` to display a string.

One thing you might now know is that the Java compiler will generate
a default constructor for your HelloWorld class even though you only
have a static method. Technically, you don't need to do that, and
JFuncMachine will not do it automatically, but it does provide
an easy way to generate a default constructor if you wish.

So, let's begin with the `main` method. While it looks like a simple
method call, it is really doing two things - fetching the `out`
member variable from the System class, and then invoking its `println`
method.  One other thing to note is that the argument to `println` is just
a string constant.

### Defining a Method
So, to construct this method with JFuncMachine,
we define a method like this:

```java
MethodDef main = new MethodDef("main", Access.PUBLIC + Access.STATIC,
        new Field[] { new Field("args", new ArrayType(SimpleTypes.STRING)) },
        SimpleTypes.UNIT,
        new CallJavaMethod("java.io.PrintStream", "println",
                SimpleTypes.UNIT,
                new GetJavaStaticField("java.lang.System", "out",
                        new ObjectType("java.io.PrintStream")),
                new Expression[] { new StringConstant("Hello World!") }
        ));
```

Let's walk through that line-by-line:

```
MethodDef main = new MethodDef("main", Access.PUBLIC + Access.STATIC,
```

We are creating a MethodDef (method definition) for a method named
"main", and it should be both public and static (this accounts for the
"public static" part of the Java Hello World program.


```
new Field[] { new Field("args", new ArrayType(SimpleTypes.STRING)) },
```

Next, we define the names and types of the parameters to the `main` method.
The parameter types are an array of `Field` objects, each containing
a parameter name and a type. JFuncMachine has a SimpleTypes class that
contains static definitions for the native Java types (boolean, byte, char,
double, float, int, and short) and also a simple type for string, even
though it is actually an object.

The `ArrayType` class is a compound type. In this case, we create it
with `SimpleTypes.STRING` as the parameter, indicating that the type
is an array of strings. This accounts for the `String[] args` part
of the Hello World program.

```
SimpleTypes.UNIT,
```
Next is the return type of the method. Since JFuncMachine was originally
intended for implementing functional languages, I used the name UNIT
instead of VOID for the void type. So, the SimpleTypes.UNIT parameter
indicates that the method returns void (i.e. has no return value).

```
new CallJavaMethod("java.io.PrintStream", "println",
```

Next we start creating the class that represents the calling of a Java
method. The first two parameters are the name of the class and the
method to invoke.

```
SimpleTypes.UNIT,
```

The println method returns void, so we specify that here as UNIT. Although
we are not using the option here, the `CallJavaMethod` constructor can
take and array of `Type` objects to specify a method's parameter types,
but if they are not supplied, the parameter types are derived from the
method argument expressions.

```
new GetJavaStaticField("java.lang.System", "out",
    new ObjectType("java.io.PrintStream")),
```

When you call a non-static method, you are calling it on a particular
object. In JFuncMachine that object is referred to as the `target` and
in this case, we want the target to be `System.out`, which means the
static `out` field in the `System` class, so we use `GetJavaStaticField`
to fetch the target, telling it the class name containing the field,
the name of the field, and its type, which shows how you represent
an arbitrary Java object type in JFuncMachine.

```
new Expression[] { new StringConstant("Hello World!") }));
```

Finally, we pass the argument to the `println` methods by giving it
a `StringConstant` as an argument.

### Defining a Class
Now that we have defined the method, we need to define that class that
contains it:

```
ClassDef newClass = new ClassDef(null, "HelloWorld",
    Access.PUBLIC,
    new MethodDef[] { main }, new ClassField[0], new String[0]);
```

We are defining a class with a null package name (no package name)
and a class name of "HelloWorld". The class is public, contains a
single method, which is contained in the `main` variable defined above,
no fields (an empty array of ClassField) and it doesn't implement
any interfaces (the `new String[0]` at the end).

### Generating Byte Code
There are several ways to generate Java byte code in JFuncMachine, we'll
start with the most basic, which is to generate a .class file in
a specified output directory. To do that, we create a `ClassGenerator`
and ask it to generate the class into a directory named `out`:

```
ClassGenerator generator = new ClassGenerator();

generator.generate(newClass, "out");
```

### The Complete "Hello World" program

Here is the complete program including its inputs and try-catch block:

```java
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
```

### Building and Running the Generated Class

If we have a Gradle configuration for our Hello World generator,
we can run it with:
```shell
gradlew run
```

We should see that there is now a directory named "out". We can go to that
directory and run the HelloWorld:

```
mark:HelloWorld/ $ gradlew run

BUILD SUCCESSFUL in 465ms
2 actionable tasks: 2 executed
mark:HelloWorld/ $ cd out
mark:out/ $ ls
HelloWorld.class
mark:out/ $ java -cp . HelloWorld
Hello World!
mark:out/ $ 
```

The complete repository with the Gradle script and a local copy of
Gradle is available at: https://github.com/JFuncMachine/JFuncMachineExamples.git

