## rabbitmq-test
This is a Java 8 application that, through asynchronous messages, includes legitimate urls in a whitelist and validates new urls using the whitelist.

### To run this application:

    $ mvn clean install
    $ docker-compose up

**$INSERTION_QUEUE message format:**

    {"client": <string/nullable>, "regex": <string>}
    
**$VALIDATION_QUEUE message format:**

    {"client": <string>, "url": <string>, "correlationId": <integer>}

**$RESPONSE_EXCHANGE message format:**

    {"match": <boolean>, "regex": <string/nullable>, "correlationId": <integer>}
