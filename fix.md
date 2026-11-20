# Fix for `ExceptionInInitializerError` during compilation

## Problem

The `CalculStatistique-Service` module is configured for Java 17 in
`CalculStatistique-Service/pom.xml`, but IntelliJ is compiling it with:

```text
javac 27.0.1.0.2
```

The error:

```text
java.lang.ExceptionInInitializerError
com.sun.tools.javac.tree.EndPosTable
```

occurs while `javac` is starting its annotation processors. This is commonly
caused by a Lombok/compiler compatibility problem. Lombok uses internal
`javac` APIs, and the Lombok version resolved by the project may not support
JDK 27 yet. It is not normally a problem in the application's Java code.

## Recommended fix in IntelliJ IDEA Community

1. Install a supported JDK, preferably JDK 17 for this project. JDK 21 can
   also be used if all project dependencies support it.
2. Open **File > Project Structure > Project**.
3. Set **Project SDK** to the installed JDK 17.
4. Set **Project language level** to **17** (or **SDK default** when the SDK
   is JDK 17).
5. In **Project Structure > Modules**, select `CalculStatistique-Service` and
   make sure its module SDK is **Project SDK**.
6. Open **File > Settings > Build, Execution, Deployment > Build Tools >
   Maven**:
   - Set **Maven home path** to the project Maven wrapper or bundled Maven.
   - Set **JDK for importer** to JDK 17.
7. Open **Settings > Build, Execution, Deployment > Build Tools > Maven >
   Runner** and set **JRE** to JDK 17.
8. Open **Settings > Build, Execution, Deployment > Compiler > Java Compiler**
   and ensure the project bytecode target is **17**.
9. Open **Settings > Build, Execution, Deployment > Compiler > Annotation
   Processors** and leave annotation processing enabled, because this project
   uses Lombok. Use the Maven profile/default configuration unless the project
   has a documented custom configuration.
10. Click **Apply**, then **OK**.

## Clean and rebuild

In IntelliJ, use:

1. **Build > Clean Project**
2. **Build > Rebuild Project**

If IntelliJ still shows the same cached compiler error, close the project and
remove only the module's generated `CalculStatistique-Service/target`
directory, then reopen IntelliJ and reload the Maven project. Do not remove
the source directories.

You can also verify the effective compiler outside IntelliJ from the
`CalculStatistique-Service` directory:

```text
.\mvnw.cmd -version
.\mvnw.cmd clean test
```

The `Java version` reported by the wrapper should be JDK 17 (or another
explicitly supported version), not JDK 27.

## If JDK 27 must be used

Use a Lombok release that explicitly supports the selected JDK and refresh the
Maven project. This requires changing the dependency version in the Maven
configuration, so it is not the first choice when the goal is only to restore
the current project build. Using JDK 17 is the safer fix for the existing
Java 17 configuration.
