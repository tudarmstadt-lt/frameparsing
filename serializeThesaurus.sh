#!/bin/bash
FOLDER=$1
FILE=$2
java -cp target/LinkedSDDT.jar de.joint.thesaurus.Thesaurus "$FOLDER" "$FILE"
