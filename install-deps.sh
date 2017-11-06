#!/bin/bash -ex
mvn install:install-file -Dfile=dist/lib/babelnet-api-2.5.1.jar -DgroupId=it.uniroma1.lcl.jlt -DartifactId=babelnet-api -Dversion=2.5.1 -Dpackaging=jar
mvn install:install-file -Dfile=dist/lib/jltutils-2.2.jar -DgroupId=it.uniroma1.lcl.jlt -DartifactId=jltutils -Dversion=2.2 -Dpackaging=jar
