#!/bin/bash
[[ $JDBC_URL =~ mysql:([0-9]+) ]]
while ! nc -z mysql "${BASH_REMATCH[1]}"; do sleep 1; done
while ! nc -z rabbitmq "$RABBITMQ_PORT"; do sleep 1; done
java -jar rabbitmq_test.jar