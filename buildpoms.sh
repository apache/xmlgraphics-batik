#!/bin/bash
# This script is used to generate the pom files needed to build the maven jars.
# After running the script the new pom template files are in the checkpoms directory;
# check them and if they look OK move them to the sources directory. Note that
# this script runs builddeps first.
./builddeps.sh
rm -rf checkpoms
mkdir checkpoms
ls -l checkdeps/dot/batik-* | grep -v js | sed s/'checkdeps\/dot\/'//g | sed s/-1.8.jar.dot//g | awk '{printf("./buildpom.sh %s > checkpoms/%s.pom.template\n", $9, $9)}' > generate-poms.sh
chmod +x generate-poms.sh
./generate-poms.sh
rm generate-poms.sh
