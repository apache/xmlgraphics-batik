./builddeps.sh
rm -rf checkpoms
mkdir checkpoms
ls -l checkdeps/dot/batik-* | grep -v js | sed s/'checkdeps\/dot\/'//g | sed s/-svn-trunk.jar.dot//g | awk '{printf("./buildpom.sh %s > checkpoms/%s.pom.template\n", $9, $9)}' > generate-poms.sh
chmod +x generate-poms.sh
./generate-poms.sh
rm generate-poms.sh
