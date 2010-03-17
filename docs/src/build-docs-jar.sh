#!/bin/bash

JAR_NAME=eqhelp.jar
DIST_DIR=../dist
BUILD_DIR=../build

. build-search.sh

if [ -d $BUILD_DIR ]; then
  rm -Rf $BUILD_DIR
fi
mkdir $BUILD_DIR

if [ ! -d $DIST_DIR ]; then
  mkdir $DIST_DIR
fi

if [ -f $DIST_DIR/$JAR_NAME ]; then
  rm -f $DIST_DIR/$JAR_NAME
fi

echo
cp -Ra html/ images/ jhelpsearch/ eq.hs helpmap.jhm toc.xml $BUILD_DIR
for DIR in `find $BUILD_DIR -type d -name 'CVS'`
do
  echo -n "."
  rm -Rf $DIR
done
echo

for DIR in `find $BUILD_DIR -type d -name '.svn'`
do
  echo -n "."
  rm -Rf $DIR
done
echo
echo

RELEASE_NOTES_DIR=$BUILD_DIR/../src/release-notes-html
cat $RELEASE_NOTES_DIR/releasenotes-part1.html > $BUILD_DIR/html/releasenotes.html
cat ../../src/org/executequery/release.notes >> $BUILD_DIR/html/releasenotes.html
cat $RELEASE_NOTES_DIR/releasenotes-part2.html >> $BUILD_DIR/html/releasenotes.html

cd $BUILD_DIR
$JAVA_HOME/bin/jar cvf $DIST_DIR/$JAR_NAME html/ images/ jhelpsearch/ eq.hs helpmap.jhm toc.xml 

#cd ../
cp -f $DIST_DIR/$JAR_NAME ../

