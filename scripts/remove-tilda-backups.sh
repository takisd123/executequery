#!/bin/bash

EQ_DIR=/home/takisd/softdev/jdevhome/netbeans-projects/ExecuteQuery

SRC_DIR=$EQ_DIR/src
COUNT=0

echo
echo "removing backup files ending with *.java~ from source..."
for FILE in `find $SRC_DIR -type f -name *.java~`
do
  echo "removing $FILE..."
  let COUNT=$COUNT+1
  rm -f $FILE
done

echo
echo
echo "Removed $COUNT files"
echo
exit 0
