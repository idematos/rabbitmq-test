FROM openjdk:8-jre
COPY target/rabbitmq_test-1.0.0.jar rabbitmq_test.jar
COPY src/start.sh start.sh
RUN chmod +x start.sh
RUN apt-get update
RUN apt-get install -y netcat
CMD ["./start.sh"]