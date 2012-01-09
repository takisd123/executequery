#!/bin/bash

if [ -z "$1" ]; then
  echo
  echo "Error: you must provide a version number for this deployment."
  echo "Exiting..."
  echo
  exit
fi

print_progress() {
  echo -n "."
}

#JAVA_HOME=/home/takisd/softdev/dev-env/java/jdk1.5.0_18
JAVA_HOME=/home/takisd/softdev/dev-env/java/jdk6
PATH=$JAVA_HOME/bin:$PATH
export PATH JAVA_HOME

echo
echo "using java version:"
java -version

REMOVE_CVS=/home/takisd/bin/remove-cvs

NEW_VERSION=$1
BUILD_NUMBER=$2

EQ_DIR=/home/takisd/softdev/workspaces/eclipse/ExecuteQuery
#EQ_DIR=/home/takisd/temp/workspace4/ExecuteQuery

DOCS_DIR=$EQ_DIR/docs
ANT_DIR=$EQ_DIR/ant
ANT_BUILDS=$ANT_DIR/builds/$NEW_VERSION

INSTALLER_TEMPLATE=$ANT_DIR/JavaInstaller

# check if the specified dir exists and ask to remove
if [ -d $ANT_BUILDS ]; then
  echo
  echo "The specified directory exists."
  echo "Continuing will remove the specified directory"
  echo -n "Do you wish to continue? (y/n) "
  read ANSWER
  echo

  if [ $ANSWER = "n" -o $ANSWER = "N" -o $ANSWER = "no" -o $ANSWER = "NO" ]; then
    echo "Action cancelled"
    echo "Exiting..."
    echo
    exit 1
  else
    if [ $ANSWER = "y" -o $ANSWER = "Y" -o $ANSWER = "yes" -o $ANSWER = "YES" ]; then
      echo "Removing directory $ANT_BUILDS"
      rm -Rf $ANT_BUILDS
      
      if [ -d $EQ_DIR/deployments/$NEW_VERSION ]; then
        rm -Rf $EQ_DIR/deployments/$NEW_VERSION
      fi
      
      echo
    else
      echo "Unknown response"
      echo "Exiting..."
      echo
      exit 2
    fi
  fi
fi

# generate and copy help
echo
echo "creating new help doc archive"
cd $DOCS_DIR/src
. build-docs-jar.sh
cp $DOCS_DIR/dist/eqhelp.jar $ANT_DIR/template-build/src/docs
echo 

# create the new dir within the eq ant dir
echo
echo "Creating new version directory $NEW_VERSION"

mkdir -p $ANT_BUILDS
mkdir -p $ANT_BUILDS/src/java

# copy the source from current dev
echo "copying src..."
cp -R $ANT_DIR/template-build/* $ANT_BUILDS
cp -R $EQ_DIR/src/* $ANT_BUILDS/src/java

# remove CVS dirs from copied source
SRC_DIR=$ANT_BUILDS/src
echo
echo "removing SVN directories from source..."
$REMOVE_CVS $SRC_DIR

echo
echo
echo "SVN directories removed"
echo

# copy release notes
RELEASE_NOTES=$ANT_BUILDS/src/java/org/executequery/release.notes
echo
echo "copying new release notes to the Java installer and source dirs"
cp $RELEASE_NOTES $INSTALLER_TEMPLATE/conf
cp $RELEASE_NOTES $ANT_DIR/template-build/src/README.txt
cp $RELEASE_NOTES $ANT_BUILDS/src/README.txt

echo
echo "copying third-party libraries to template build dir"
rm -f $ANT_DIR/template-build/src/lib/*
cp $EQ_DIR/lib/* $ANT_DIR/template-build/src/lib


# start ant
cd $ANT_BUILDS
echo "starting Ant"
echo "using version: `ant -version`"
echo
ant

CMD_RESULT=$?

if [ ! $CMD_RESULT -eq 0 ]; then
  echo
  echo "Error: process failed"
  echo "Check the stack trace to determine what went wrong"
  echo "Exiting..."
  echo
  exit 3
fi

# create the deployment dir and copy over build
echo "creating new deployment directory:"
echo "$EQ_DIR/deployments/$NEW_VERSION"
echo

echo -n "copying files "
mkdir -p $EQ_DIR/deployments/$NEW_VERSION
print_progress
mkdir -p $EQ_DIR/deployments/$NEW_VERSION/ExecuteQuery
print_progress
cp -R $ANT_BUILDS/build/* $EQ_DIR/deployments/$NEW_VERSION/ExecuteQuery
print_progress
echo

# get the zip archive file count
EQ_DEPLOY_DIR=$EQ_DIR/deployments/$NEW_VERSION/ExecuteQuery
FILE_COUNT=0
for FILE in `find $EQ_DEPLOY_DIR`
do
  if [ ! -d $FILE ]; then
    let FILE_COUNT=$FILE_COUNT+1
  fi
done

EQ_DEPLOY_DIR=$EQ_DIR/deployments/$NEW_VERSION

# create archive files
echo "creating archives..."
echo

cd $EQ_DEPLOY_DIR

echo "creating zip file..."
echo

ZIP_FILE=executequery-v$NEW_VERSION.zip
zip -r $ZIP_FILE ExecuteQuery/ 

echo
echo "creating gzip file..."
echo

tar cvf executequery-v$NEW_VERSION.tar ExecuteQuery/*
gzip -9 executequery-v$NEW_VERSION.tar

echo
echo -n "creating source archive..."
print_progress

mkdir $EQ_DEPLOY_DIR/src
print_progress
mkdir $EQ_DEPLOY_DIR/src/ExecuteQuery
print_progress

#cp -R $EQ_DIR/ant/$NEW_VERSION/src/* $EQ_DEPLOY_DIR/src/ExecuteQuery/
cp -R $ANT_BUILDS/* $EQ_DEPLOY_DIR/src/ExecuteQuery/
rm -R $EQ_DEPLOY_DIR/src/ExecuteQuery/build
print_progress

# add header to src files
echo
echo -n "adding source code file header"
HEADER_REMOVE_SCRIPT=$ANT_DIR/src-file-header
cd $HEADER_REMOVE_SCRIPT
java SourceHeader $EQ_DEPLOY_DIR/src/ExecuteQuery/src/java/org \
     $HEADER_REMOVE_SCRIPT/header.txt

print_progress

cd $EQ_DEPLOY_DIR/src
print_progress
tar cf ../executequery-src-v$NEW_VERSION.tar ExecuteQuery/
print_progress
cd ../
print_progress
gzip -9 executequery-src-v$NEW_VERSION.tar
print_progress
cp executequery-src-v$NEW_VERSION.tar.gz ../
print_progress
echo
echo "source archive created"
echo

# build the java installer
echo -n "building java installer"
print_progress

INSTALLER_BUILD_DIR=$ANT_DIR/JavaInstaller/build

print_progress
mkdir $EQ_DEPLOY_DIR/JavaInstaller
print_progress
cp -R $INSTALLER_TEMPLATE/installer.manifest $EQ_DEPLOY_DIR/JavaInstaller/
print_progress
cp -R $INSTALLER_TEMPLATE/build $EQ_DEPLOY_DIR/JavaInstaller/
print_progress
cp $INSTALLER_TEMPLATE/conf/gnu.license $INSTALLER_TEMPLATE/build/org/executequery/installer/conf
print_progress
cp $INSTALLER_TEMPLATE/conf/release.notes $INSTALLER_TEMPLATE/build/org/executequery/installer/conf
print_progress
cp $INSTALLER_TEMPLATE/conf/release.notes $EQ_DEPLOY_DIR
mv $EQ_DEPLOY_DIR/release.notes $EQ_DEPLOY_DIR/release-notes.txt
print_progress

# modify the installer properties for this release
PROPS_TEMPLATE=$INSTALLER_TEMPLATE/conf/installer.properties.template
PROPS_FILE=$INSTALLER_BUILD_DIR/org/executequery/installer/conf/installer.properties

print_progress
sed -e "s/{eq-version}/$NEW_VERSION/ig" $PROPS_TEMPLATE > $PROPS_FILE.1
print_progress
sed -e "s/{zip-file}/$ZIP_FILE/ig" $PROPS_FILE.1 > $PROPS_FILE.2
print_progress
sed -e "s/{archive-file-count}/$FILE_COUNT/ig" $PROPS_FILE.2 > $PROPS_FILE
print_progress
rm -f $PROPS_FILE.*
print_progress

# create the program archive
print_progress
cd $EQ_DEPLOY_DIR/ExecuteQuery
print_progress
rm -f $INSTALLER_BUILD_DIR/org/executequery/installer/program/executequery-*.zip
zip -rq $INSTALLER_BUILD_DIR/org/executequery/installer/program/$ZIP_FILE *
print_progress

# build the installer jar file
print_progress

INSTALLER_TEMP=$INSTALLER_TEMPLATE/temp
mkdir -p $INSTALLER_TEMP
cp -R $INSTALLER_BUILD_DIR $INSTALLER_TEMP

cd $INSTALLER_TEMP/build
$REMOVE_CVS .

INSTALLER_FILE_NAME=executequery-installer-v$NEW_VERSION.jar

print_progress
jar cmf $EQ_DEPLOY_DIR/JavaInstaller/installer.manifest \
        $EQ_DEPLOY_DIR/$INSTALLER_FILE_NAME org/**

print_progress
rm -Rf $EQ_DEPLOY_DIR/JavaInstaller
rm -Rf $INSTALLER_TEMP
print_progress

# ---------------------------------------
# build deb package

echo
echo
echo "Building deb package"

DEB_TEMPLATE=$ANT_DIR/deb-pkg
DEB_TEMP=$EQ_DEPLOY_DIR/deb-pkg
cd $EQ_DEPLOY_DIR
cp -R $DEB_TEMPLATE $EQ_DEPLOY_DIR
print_progress
cp ExecuteQuery/README.txt $DEB_TEMP/usr/share/doc/executequery
rm -Rf $DEB_TEMP/usr/share/executequery/*
print_progress
cp -R ExecuteQuery/eq.jar ExecuteQuery/lib ExecuteQuery/docs $DEB_TEMP/usr/share/executequery
print_progress
cp ExecuteQuery/eq.png $DEB_TEMP/usr/share/pixmaps/executequery.png
print_progress
cp $DEB_TEMP/usr/share/doc/executequery/* $DEB_TEMP/usr/share/executequery/docs
print_progress

MAN_PAGE_TEMPLATE=$DEB_TEMP/usr/share/man/man1/executequery.1.template
cd $DEB_TEMP/usr/share/man/man1
print_progress
sed -e "s/{todays_date}/`date "+%d %B, %Y"`/ig" $MAN_PAGE_TEMPLATE > $MAN_PAGE_TEMPLATE.1
sed -e "s/{eq-version}/$NEW_VERSION-$BUILD_NUMBER/ig" $MAN_PAGE_TEMPLATE.1 > $MAN_PAGE_TEMPLATE.2
print_progress
mv $MAN_PAGE_TEMPLATE.2 executequery.1
gzip -9 executequery.1
rm -f $MAN_PAGE_TEMPLATE.*
print_progress
rm -f $MAN_PAGE_TEMPLATE

print_progress

cd $DEB_TEMP
INSTALL_SIZE=`du usr | grep usr$ | sed -e "s/\tusr//ig"`

cd $DEB_TEMP/DEBIAN
print_progress
sed -e "s/{eq-version}/$NEW_VERSION-$BUILD_NUMBER/ig" control > control.1
mv control.1 control

sed -e "s/{install_size}/$INSTALL_SIZE/ig" control > control.1
mv control.1 control

print_progress

cd $DEB_TEMP
md5sum `find . -type f | grep -v '^[.]/DEBIAN/'` >DEBIAN/md5sums

print_progress
echo

$REMOVE_CVS $DEB_TEMP
echo

cd $EQ_DEPLOY_DIR
fakeroot dpkg-deb --build $DEB_TEMP executequery_$NEW_VERSION-$BUILD_NUMBER.deb

rm -Rf $DEB_TEMP

# ---------------------------------------

# copy new jar to current deploy dir for use
#cp -f $ANT_BUILDS/build/eq.jar /home/takisd/softdev/deployments/final_jre 

rm -Rf $ANT_BUILDS/build
rm -f $INSTALLER_BUILD_DIR/org/executequery/installer/program/executequery-*.zip

echo
echo
echo "finishing..."
echo
echo "check the stack for any errors"
echo
echo "Done"
echo
echo "Exiting..."
echo

#echo creating the source archive
#mkdir tmp-src/ExecuteQuery
#cp -R $EQ_DIR/ant/$NEW_VERSION/* tmp-src/ExecuteQuery

