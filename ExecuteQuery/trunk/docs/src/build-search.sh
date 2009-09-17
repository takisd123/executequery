#!/bin/bash

SEARCH_DB=jhelpsearch
JHELP_HOME=/home/takisd/softdev/dev-env/java/jh2.0/javahelp/

if [ -d $SEARCH_DB ]; then
  rm -Rf $SEARCH_DB
fi

$JHELP_HOME/bin/jhindexer -c index.conf -db jhelpsearch -verbose
