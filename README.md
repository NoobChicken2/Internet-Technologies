# README
This is a project made for the subject Internet Technologies from the university Saxion University of Applied Sciences. It was made by 2 students.

It is a java program which has a Server and a Client. Clients when the server is running connect to it and they can do multiple actions like broadcast their messages, send private messages, start a survey with multiple users, basic file transfer and send Encrypted private messages.

The whole point of the program was to understand how Server Protocols work.

## Parameters
Parameters can be configured in the testconfig.properties file

| Parameter     | Description                                   |
| ------------- |-----------------------------------------------|
| host          | Ip address of host to connect to              |
| port          | Port where the chat client is running on       |
| ping_time_ms  | Time period (in ms) between ping requests from server |
| ping_time_ms_delta_allowed |  Maximum allowed time difference (in ms) for ping request as measured by client |

## To run
1. Make sure the server is started
```
node server.js
```
2. Start the integration test by simply running this program from within IntellIj

## Notes
In JUnit Jupiter you should use TestReporter when you want to output information to the console. See H2.12 on 
https://junit.org/junit5/docs/current/user-guide/
