## Challenge for a company I did long ago

### Requirements
* Java 1.8
* Scala 2.12
* sbt
* homebrew 
    * To Install Kafka use `brew install kafka`
    
---

### How to run

Clone the project and assemble the project jar. 
```
sbt assembly
```

Create topics 
* Producer topic. 
```$xslt
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic minute-topic
```
* Consumer topic. 
```$xslt
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test-topic
```

Then run the jar with 
```
java -jar {PROJECT_PATH}/target/scala-2.12/Challenge-assembly-0.1.jar
```

Start Sending Records with manually or generated to the kafka using this command.
```
cat ~/Downloads/stream.jsonl | head -30 | kafka-console-producer --broker-list localhost:9092 --topic test-topic
```
or send records between X-Y lines 
```
sed -n '100000,200000p' ~/Downloads/stream.jsonl | kafka-console-producer --broker-list localhost:9092 --topic test-topic
```
or send them all :D
```
cat ~/Downloads/stream.jsonl | kafka-console-producer --broker-list localhost:9092 --topic test-topic
```

Listen the result from the producer
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic minute-topic
```

Sample Result looks like this. 
````
{"timestamp":"2016-07-11T16:40","uniqueUserNumber":41130}
````

---

### Challenges / Improvements / Method

I used Scala for this project and as a data structure I used Map. 

Map consists of timestamps as key and List[uid] as a value. I used map because each key is unique and 
rather than grouping the keys in each minute I wanted to look the uniqueness of the ids'.

I had to normalize the current time to be able to monitor 1 minute time-windows, 
otherwise the code was checking exactly 1 minute ago from now. For example if the is 
16:32:15, it sends the result at 16:33:20 but I wanted to send it exactly at the 16:33:05 / 16:34:05 / 16:35:05...
Only in the first time this might delay, but with the second batch it normalizes. 

As mentioned in the challenge description there %99.9 of the records arrive at most in 5 seconds latency, 
so I wait 5 more seconds to create the output for the last minute.

My implementation can work with week, month and year. I need to create maps for week, month and year separately.
Then I need to monitor all of them separately. I don't need to parse the json more than one time. Only one time is enough, 
so the records can be mapped to the proper keys in each map. 

For this project kafka producers and consumers have only 1 partition. To scale it out I would increase the partition
 numbers. Also I would introduce threads and consume records in parallel. When the output result had to be generated, 
 threads sends the record map with timestamps and uids they created to the one leading or master thread to give proper result. 

For the crashing problems, rather than depending on kafka's consumer offset solution I would create my own offset file 
and depend on that. Because if I depend on Kafka's own offset solution based on the checkpoint I start consuming, I 
might lose or duplicate data. 

Json records comes with a schema and if the engineer has an access to that schema each record will be consumed easily.
In basic we don't need the schema of the json but if we do have schema this would be amazing. 
Frameworks like Spark generates json schema automatically and tries to parse each record with that schema. But this can 
result with corrupted data because there might some records with different schema. So to be able solve this, we can search
for the field names that we need in the json. Protobufs are also a good alternative but not strings (like csv formatted)
or xmls. Becase string parsing can result with a lot of corrupted data. 

Scala Try object helped me a lot to identify corrupted records. If the record can be parsable it parses, otherwise 
creates empty object rather than throwing an error.

I used Kafka Libraries, Spray-json for json parsing and Joda-Time for date-time parsing. 
slf4j is also imported but this is not use, this is only for version differences between other libraries. 

In my own computer, macbook pro with 8gb of memory and 1.3 ghz cpu I managed to consume and analyze all 
records in the stream.jsonl file.

One more thing I would do is creating properties.yml file to enter all of the properties for kafka consumer and producer, 
latency, time interval rather than hard coding into the project. 

I did create an StdIn version but I didn't open the branch for that and after I passed the time limit I didn't want to include it. 
And I also implemented the properties.yml but same. I focused on consuming all the records and analyzing them from the kafka.
