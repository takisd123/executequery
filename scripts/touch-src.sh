#!/bin/bash

SRC_DIR=/home/takisd/softdev/workspaces/eclipse/ExecuteQuery/src
#SRC_DIR=/home/takisd/softdev/jdevhome/netbeans-projects/UnderworldLabs/src

for FILE in `find $SRC_DIR -type f`
do
  if [ -z `ls $FILE | grep 'CVS'` ]; then
    touch $FILE
  fi
done

exit 0

