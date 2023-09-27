#!/usr/bin/env bash

cd $CATALINA_HOME/logs/
chmod -R 777 *
cd $CATALINA_HOME/bin
bash catalina.sh run 
