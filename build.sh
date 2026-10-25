#!/bin/bash
# Builds NoSpawnInAdminClaims.jar with a plain JDK and no Maven, for a box that already runs Paper + GriefPrevention.
# Needs: a JDK (JAVA_HOME, or ~/.local/jdk/jdk-25*), the paper-api jar paperclip extracts under ${MC_DIR:-/opt/minecraft}/libraries,
# and ${MC_DIR:-/opt/minecraft}/plugins/GriefPrevention.jar. Override the server dir with MC_DIR. The Maven build (pom.xml) is the
# portable route; this script exists so the server can rebuild itself without network access to Maven Central.
set -euo pipefail
cd "$(dirname "$0")"
JDK="${JAVA_HOME:-$(ls -d ~/.local/jdk/jdk-25* | head -1)}"
API="$(ls ${MC_DIR:-/opt/minecraft}/libraries/io/papermc/paper/paper-api/*/paper-api-*.jar | head -1)"
KYORI="$(find ${MC_DIR:-/opt/minecraft}/libraries/net/kyori -name '*.jar' | tr '\n' ':')"
GP=${MC_DIR:-/opt/minecraft}/plugins/GriefPrevention.jar
rm -rf out && mkdir -p out/classes
"$JDK/bin/javac" --release 25 -Xlint:all,-classfile -cp "$API:$GP:$KYORI" -d out/classes $(find src -name '*.java')
cp resources/plugin.yml resources/config.yml out/classes/
"$JDK/bin/jar" --create --file out/NoSpawnInAdminClaims.jar -C out/classes .
sha256sum out/NoSpawnInAdminClaims.jar
