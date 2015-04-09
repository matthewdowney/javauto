#!/bin/bash

# turn verbose on
set -x

# build javauto
cd javauto
javac Create.java
jar -cvfm ../../jars/javauto.jar manifest.txt *.class Javauto.java

# build javauto-helper
cd ../javauto-helper
javac Helper.java
jar -cvfm ../../jars/javauto-helper.jar manifest.txt *.class

# build javauto-lookup
cd ../javauto-lookup
javac Lookup.java
jar -cvfm ../../jars/javauto-lookup.jar manifest.txt *.class Javauto.java

# make them executable
cd ../../jars/
chmod +x javauto.jar
chmod +x javauto-helper.jar
chmod +x javauto-lookup.jar
