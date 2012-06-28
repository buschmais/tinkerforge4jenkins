#!/bin/bash
BASEDIR=$(dirname $0)
java -jar $BASEDIR/tinkerforge4jenkins-client.jar $*
