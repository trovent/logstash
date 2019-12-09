#!/bin/bash

kill_process () {
	echo -n "Killing server process [PID:$pid]..."
	kill -9 $1
	echo "Done."
}

PORT=4567

echo -n "Starting logstash syntax checker server"
./bin/logstash-syntax --port $PORT &> /dev/null &

pid=$!

timeout=60
time=0
curl localhost:$PORT/api/ &> /dev/null
while [ "$?" -ne "0" ]
do
	sleep 1
	time=$((time + 1))
	if [ "$timeout" -eq "$time" ]
	then
		echo "Timeout!"
		kill_process $pid
		exit 1
	fi
	echo -n "."
	curl localhost:$PORT/api/ &> /dev/null
done
echo "Done"

./gradlew :logstash-integration-tests:syntaxTests

kill_process $pid
