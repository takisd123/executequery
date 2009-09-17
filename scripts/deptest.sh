#!/bin/bash

rm -Rf /home/takisd/softdev/jdevhome/netbeans-projects/ExecuteQuery/ant/2.0rc1

if [ -z "$1" ]; then
  echo
  echo "Error: you must provide a version number for this deployment."
  echo "Exiting..."
  echo
  exit
fi

NEW_VERSION=$1

EQ_DIR=/home/takisd/softdev/jdevhome/netbeans-projects/ExecuteQuery

echo creating directory $NEW_VERSION
mkdir $EQ_DIR/ant/$NEW_VERSION

echo copying src...
cp -R $EQ_DIR/ant/template-build/* $EQ_DIR/ant/$NEW_VERSION/
cp -R $EQ_DIR/src/* $EQ_DIR/ant/$NEW_VERSION/src

SRC_DIR=$EQ_DIR/ant/$NEW_VERSION/src

for DIR in `find $SRC_DIR -type d -name 'CVS'`
do
  echo "removing CVS directory: $DIR"
  rm -Rf $DIR
done

echo "done..."
