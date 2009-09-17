#!/bin/bash

JAR_NAME=eqhelp.jar
DIST_DIR=../dist
BUILD_DIR=../build

if [ -d $BUILD_DIR ]; then
  rm -f $BUILD_DIR/*
fi

if [ ! -d $DIST_DIR ]; then
  mkdir $DIST_DIR
fi

if [ -f $DIST_DIR/$JAR_NAME ]; then
  rm -f $DIST_DIR/$JAR_NAME
fi

cp -Ra html/ images/ jhelpsearch/ eq.hs helpmap.jhm toc.xml $BUILD_DIR
for DIR in `find $BUILD_DIR -type d -name 'CVS'`
do
  rm -Rf $DIR
done

cd $BUILD_DIR
jar cvf $DIST_DIR/$JAR_NAME html/ images/ jhelpsearch/ eq.hs helpmap.jhm toc.xml 

#cd ../
cp -f $DIST_DIR/$JAR_NAME ../
