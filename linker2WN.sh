#!/bin/bash
FOLDER=$1
FILE=$2
ITERATIONS=$3
WORDNETDICTFOLDER=$4
java -cp target/LinkedSDDT.jar de.joint.Linker2WN $FOLDER $FILE $ITERATIONS $WORDNETDICTFOLDER
