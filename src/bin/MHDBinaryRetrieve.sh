#!/bin/sh
# -------------------------------------------------------------------------
# MHDSEND  Launcher
# -------------------------------------------------------------------------

MAIN_CLASS=kr.irm.fhir.MHDBinaryRetrieve

DIRNAME="`dirname "$0"`"

# Setup $MHDBINARYRETRIEVE_HOME
if [ "x$MHDBINARYRETRIEVE_HOME" = "x" ]; then
    MHDBINARYRETRIEVE_HOME=`cd "$DIRNAME"/..; pwd`
fi

# Setup the JVM
if [ "x$JAVA_HOME" != "x" ]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA="java"
fi

# Setup the classpath
CP="$MHDBINARYRETRIEVE_HOME/etc/MHDManifestSearch/"
for s in $MHDBINARYRETRIEVE_HOME/lib/*.jar
do
	CP="$CP:$s"
done

# Execute the JVM

exec $JAVA $JAVA_OPTS -cp "$CP" $MAIN_CLASS "$@"