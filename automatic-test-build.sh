#!/bin/sh

docker-compose -f ./_docker/application/docker-compose-it.yml stop

case `uname` in
    Darwin)
	export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk17/Contents/Home
	echo "java-home: $JAVA_HOME"
	;;
    *)
	echo "not supported platform for java 17"
	exit
	;;
esac

mvn clean package || exit

rm -rf drop-unit-simulator/logs/*
rm -rf logs/* 
rm -rf engine-under-test/logs/*

docker-compose -f ./_docker/application/docker-compose-it.yml up -d --build

sleep 3
while ! curl --silent http://127.0.0.1:8080/
do
    sleep 1
done

( mvn test -Dtest=*IT -DfailIfNoTests=false && \
  mvn test -Dtest=*ITslow -DfailIfNoTests=false ) || exit

docker-compose -f ./_docker/application/docker-compose-it.yml stop

exit 1
