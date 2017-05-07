# transaction-statistics
This project gives the real-time transaction statistics for last 60 seconds

The application is built using vertx. The application exposes two APIs one for posting transaction and one for getting statistics of transaction for past 60 secs

The code is developed into multiple verticles of vertx each responsible for its own activity.
The data storage is provided by in memory linkedlist

The accuracy of the application is to the precision of a second.

This statistics API is designed to fetch results at space time complexity of O(1)

## How it works?
The data is stored in linkedlist with each node/bucket containing the data recieved in past corresponding one minute interval at. For example data with current timestamp goes to index 0 while data 30 sec old goes to index 29. 
There is a periodic job running that creates a new node of data every second. Also this job calculates the min and max values if the min/max expires

## How to run?
mvn clean package
java -jar target/transaction-statistics-1.0.0-SNAPSHOT.jar 8080

hit localhost:8080

