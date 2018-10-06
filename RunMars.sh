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

    if ((major == 1 && minor > 10)); then
        echo "Running Mars with default JRE"
        java -jar Mars.jar
    else
        echo "Attempting to run Mars with java-10"
        /usr/lib/jvm/java-10/bin/java -jar Mars.jar
    fi
fi