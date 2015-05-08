#!/bin/bash
# This scripts generates a diagram of the dependencies between the different jars.
# To run it both jdeps (comes with jdk 1.8) and dot (comes with graphviz) need to
# be present. The diagram, batik-jars-deps.svg, can be found in the checkdeps folder.
# NOTE: Before running the script run "ant jars maven-artifacts".
rm -rf checkdeps
mkdir checkdeps
ls -l batik-svn-trunk/maven/batik-*/*/*.jar | awk '{printf("cp %s checkdeps/\n", $9)}' > cp.sh
chmod +x cp.sh
./cp.sh
cp lib/*.jar checkdeps
rm cp.sh
cd checkdeps
jdeps -dotoutput dot -classpath . *
grep batik dot/batik* | grep jar | grep -v 'not found' | grep -v 'java\.' | grep -v javax | grep -v xml-apis | grep -v xalan | grep -v JDK | grep -v Path | grep -v '{' | awk '{print $1 " -> " $5}' | sort -u | sed s/'dot\/'/'"'/g | sed s/'.dot:'/'"'/g | sed s/'('/'"'/g | sed s/')"'/'"'/g  > deps.dot
echo 'digraph "batik-jars-deps" {' > batikdeps.dot
awk '{if ($1";" != $3) {print $0}}' deps.dot >> batikdeps.dot
echo '}' >> batikdeps.dot
sed s/-svn-trunk//g batikdeps.dot | sed s/'.jar'//g > batikdeps-2.dot
dot -Tsvg batikdeps-2.dot -o batik-jars-deps.svg
rm deps.dot batikdeps.dot batikdeps-2.dot
