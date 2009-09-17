#!/bin/bash

cd ../

EQ_DIR=/home/takisd/softdev/jdevhome/netbeans-projects/ExecuteQuery
ULABS_DIR=/home/takisd/softdev/jdevhome/netbeans-projects/UnderworldLabs

JAR_DIR=$EQ_DIR/jars

BUILD_DIR=$JAR_DIR/temp-build

if [ -d $BUILD_DIR ]; then
    rm -R $BUILD_DIR
fi

mkdir $BUILD_DIR
cp -R $EQ_DIR/src $BUILD_DIR
cp -R $ULABS_DIR/src $BUILD_DIR

print_progress() {
  echo -n "."
}

# remove CVS dirs from copied source
SRC_DIR=$BUILD_DIR/src
COUNT=0

echo
echo "removing CVS directories from source..."
for DIR in `find $SRC_DIR -type d -name 'CVS'`
do
  print_progress
  let COUNT=$COUNT+1
  rm -Rf $DIR
done

echo
echo
echo "Removed $COUNT directories"
echo "CVS directories removed"
echo

cd $BUILD_DIR
rm $JAR_DIR/eq-src.tar.gz
tar cvf $JAR_DIR/eq-src.tar src/*
gzip -9 $JAR_DIR/eq-src.tar

#rm -R $BUILD_DIR
