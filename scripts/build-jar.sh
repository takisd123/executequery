#!/bin/bash

EQ_DIR=/home/takisd/softdev/workspaces/eclipse/ExecuteQuery
ULABS_DIR=/home/takisd/softdev/workspaces/eclipse/UnderworldLabs

SCRIPTS_DIR=$EQ_DIR/scripts
JAR_DIR=$EQ_DIR/jars
MANIFEST_FILE=$SCRIPTS_DIR/eq.manifest
JAR_FILE=$JAR_DIR/eq.jar

CLASS_DIR=org

BUILD_DIR=$JAR_DIR/temp-build

if [ -d $BUILD_DIR ]; then
    rm -R $BUILD_DIR
fi

mkdir $BUILD_DIR
cp -R $EQ_DIR/bin/* $BUILD_DIR
cp -R $ULABS_DIR/bin/* $BUILD_DIR

mv -f $JAR_DIR/eq.jar $JAR_DIR/eq.jar.last
cd $BUILD_DIR
jar cvmf $MANIFEST_FILE $JAR_FILE $CLASS_DIR/**

cp $JAR_DIR/eq.jar /home/takisd/softdev/dev-env/ide/ExecuteQuery

rm -R $BUILD_DIR
