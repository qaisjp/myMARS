#!/usr/bin/env bash

if type -p java; then
    echo "Found Java executable in PATH"
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo "Found Java executable in JAVA_HOME"
    _java="$JAVA_HOME/bin/java"
else
    echo "Could not find an installation of Java"
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "version $version"

    IFS=. read major minor extra <<< "$version";

    mkdir "out"
    mkdir "out/production"
    mkdir "out/production/mymars"

    if ((major > 9 || (major == 1 && minor > 9))); then
        echo "Compiling Mars with default javac"
        find . -name "*.java" | xargs javac -d "out/production/mymars"
    else
        echo "Attempting to compile Mars with java-10"
        find . -name "*.java" | xargs /usr/lib/jvm/java-10/bin/javac -d "out/production/mymars"
    fi
fi

jar cfm Mars.jar \
        META-INF/MANIFEST.MF \
        README.md \
        LICENSE.md \
        PseudoOps.txt \
        Config.properties \
        Syscall.properties \
        Settings.properties \
        MipsXRayOpcode.xml \
        registerDatapath.xml \
        controlDatapath.xml \
        ALUcontrolDatapath.xml \
        docs \
        help \
        images \
        -C out/production/mymars/ Mars.class \
        -C out/production/mymars/ mars

echo "CreateMarsJar finished"