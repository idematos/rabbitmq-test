## RabbitMQ Test
This is an application that adds legitimate URLs in a whitelist and validates new URLs using it.

### To run this application:

    $ mvn clean install
    $ docker-compose up

**$INSERTION_QUEUE message format:**

    {"client": <string/nullable>, "regex": <string>}
    
**$VALIDATION_QUEUE message format:**

    {"client": <string>, "url": <string>, "correlationId": <integer>}

**$RESPONSE_EXCHANGE message format:**

    {"match": <boolean>, "regex": <string/nullable>, "correlationId": <integer>}
