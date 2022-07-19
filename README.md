# Teletrac

## Aim
Practical web service developed in Java using Vertx 4.

## Developer
- Charly Njoume 

## Tools
- Java 15
- Vertx 4.1.2
- Hsqldb


## Requirement to run
- Java 13, 14 or 15
- Maven
 
## Running 
- Open command line and navigate to the project root
- Run  command <b>mvn clean package</b>
- Run  command <b>java -jar target/teletrac-exercise-0.0.1-SNAPSHOT-fat.jar -conf  src/main/conf/teletrac-exercise-qa.json</b>
  
- Keep an eye on the terminal the second to last line should contain the generated token and it will look similar to this "17:51:40.562 [vert.x-eventloop-thread-1] WARN  com.teletrac.ApiVerticle - Your token is <b>eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NTgwNzY3MDB9.ZSK383xnD7cfxeuHc_v--prpkASSTZqUqVeBHs9P4Ig</b>"
- Use that token by adding it to header <b>"Authorization"</b> when making api requests

## End points
- post(http://localhost:8080/api/nocontent)
- post(http://localhost:8080/api/echo)
- post(http://localhost:8080/api/device)
- get(http://localhost:8080/api/devices)

