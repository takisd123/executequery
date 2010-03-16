#!/bin/bash

JAR_NAME=eqhelp.jar
DIST_DIR=../dist
BUILD_DIR=../build

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

RELEASE_NOTES_TEXT=`cat ../../src/org/executequery/release.notes`

cd $BUILD_DIR
sed -e "s/{release-notes}/$RELEASE_NOTES_TEXT/ig" $BUILD_DIR/html/releasenotes.html > $BUILD_DIR/html/releasenotes.html.1
mv $BUILD_DIR/html/releasenotes.html.1 $BUILD_DIR/html/releasenotes.html
$JAVA_HOME/bin/jar cvf $DIST_DIR/$JAR_NAME html/ images/ jhelpsearch/ eq.hs helpmap.jhm toc.xml 

#cd ../
cp -f $DIST_DIR/$JAR_NAME ../
