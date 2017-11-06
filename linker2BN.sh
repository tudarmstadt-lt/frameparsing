#!/bin/bash
FOLDER=$1
FILE=$2
ITERATIONS=$3
java -cp target/LinkedSDDT.jar de.joint.Linker2BN $FOLDER $FILE $ITERATIONS
