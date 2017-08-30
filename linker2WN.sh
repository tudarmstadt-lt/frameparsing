FOLDER=$1
FILE=$2
ITERATIONS=$3
java -cp ./dist/lib/*:./dist/*  de.joint.Linker $FOLDER $FILE $ITERATIONS
