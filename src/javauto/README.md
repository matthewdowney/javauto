javauto
=======
Javauto is the main jar file that makes up the
compiler for the javauto language.

#Create.java
Create.java handles the top level user code parsing. It
takes a .ja or .javauto file and parses a copy of the Javauto.java
file in order to create functions, it creates struct objects and class
variables, and then it uses the SimpleDebugger class to check its 
generated code. If everything checks out, it runs the code through
CustomJavaCompiler.java to generate class files; any errors at this
point are sent directly to DealWithCompilerErrors.java to be displayed. Finally CustomJarCompiler.java is used to bundle the class files into a jar.

#CustomJarCompiler.java
Takes a directory and adds each class file within it to a JAR file.

#CustomJavaCompiler.java
Takes Java code and generates a class file. Errors are sent to DealWithCompilerErrors.java

#DealWithCompilerErrors.java
Attempt to make any compiler errors from CustomJavaCompiler.java relevant to the user's original Javauto code and display these errors.

#Javauto.java
A copy of the current Javauto standard library.

#SimpleDebugger.java
Takes Java code and does a preliminary debugging to make sure things are ready to be compiled.
