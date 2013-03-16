#!/bin/bash 


HOST=executequery.org
USER=equery

echo
stty -echo 
read -p "Enter password for FTP connection to $HOST: " PASS; echo 
stty echo

if [ -z $PASS ]; then

    echo -e "\nPassword required\nExiting.\n"
    exit 1

fi

JAR=/home/takisd/Development/tools/ide/executequery
cd $JAR

ftp -inv $HOST << EOF
    user $USER $PASS 
    cd httpdocs/temp/latest
    delete eq.jar
    put eq.jar
    ls
    bye 
EOF
