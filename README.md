# cs3100-Kasteler-Spencer-assn2
The only sources for this assignment were the official Oracle documents and examples that Erik used in class.

Use gradle build to build the project but do not use gradle run to run it. use the jar file in build/libs folder ie use:
```java -jar builds/libs/Assn2.jar
```
to run the project.

This shell does not support mutiple pipes in one command. The Assn2.java is a wrapper class with main. the Shell.java does the meat of processing the main loop including parsing and it does the process building for external commands. the Builtins.java is used to have static methods for the builtin command mainly sepatate for organization purposes.
