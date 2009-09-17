#!/bin/bash

print_progress() {
  echo -n "."
}

#EQ_DIR=/home/takisd/softdev/jdevhome/netbeans-projects/ExecuteQuery
EQ_DIR=/home/takisd/softdev/jdevhome/netbeans-projects/ExecuteQueryInstaller/

#EQ_DIR=/home/takisd/softdev/jdevhome/netbeans-projects/UnderworldLabs/backup

# remove CVS dirs from copied source
SRC_DIR=$EQ_DIR
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
exit 0
