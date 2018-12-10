#!/bin/bash

if [ $# -gt 0 ]
then
	if [[ "$1" == "-h" || "$1" == "-help" || "$1" == "--help" ]]
	then
		echo "Usage: $0 [<kafka-home-dir>] "
		exit 0
	elif [ -d $1 ]
	then
		KAFKA_HOME=$1
	else
		>&2 echo "Unknown option: $1"
		exit 1
	fi
fi

if [ "$KAFKA_HOME" == "" ]
then
	>&2 echo "KAFKA_HOME is not set! Please reconfigure your env before trying to start it again."
	exit 42
fi

$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &

for yy in `find $KAFKA_HOME/config -name server*.properties`
do 
	$KAFKA_HOME/bin/kafka-server-start.sh $yy &
done
