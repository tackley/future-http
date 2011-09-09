#!/bin/bash

SBT_VERSION="0.10.1"

if [ -f "project/build.properties" ]; then
  grep "^sbt\.version=0.7" project/build.properties > /dev/null 2>&1
  if [ "$?" -eq "0" ]; then
    echo "You've got a project/build.properties that says it requires sbt 0.7.x"
    echo "Please run sbt7 instead of this script, which is for sbt $SBT_VERSION only."
    exit 1
  fi
fi

SBT_BOOT_DIR=$HOME/.sbt/boot/

if [ ! -d "$SBT_BOOT_DIR" ]; then
  mkdir -p $SBT_BOOT_DIR
fi

# echo "extra params: $SBT_EXTRA_PARAMS"

java -Dfile.encoding=UTF8 -Xmx1536M -XX:+CMSClassUnloadingEnabled -XX:+UseCompressedOops -XX:MaxPermSize=512m \
	$SBT_EXTRA_PARAMS \
	-Dsbt.boot.directory=$SBT_BOOT_DIR \
	-jar `dirname $0`/sbt-launch-$SBT_VERSION.jar "$@"


