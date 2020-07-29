package com.challenge.stream.kafka

import com.challenge.util.Helper._
import com.challenge.util.JsonParser._
import java.util.{NoSuchElementException, Properties}

import com.challenge.model.{MinuteOutput, UserInput}
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.joda.time.DateTime

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Try

object Consumer {

  val latency = 5 // second

  val producerTopic = "minute-topic"

  // Kafka Consumer properties
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "latest")
  props.put("group.id", "consumer-group")
  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)

  def consumeFromKafka(topic: List[String]) = {
    consumer.subscribe(topic.asJava)

    // this map holds the timestamp and corresponding uid's in that time interval.
    var dateMap = mutable.Map[String, ListBuffer[String]]()

    // take the current time in the beginning
    var date = fromStrToDT(formatDateTime(DateTime.now()))
    println(date.toString)
    while (true) {
      val record = consumer.poll(1000)
      // Add latency to be able to consume everything for the interval.
      if ((fromStrToDT(formatDateTime(DateTime.now())).minusMinutes(1).getMillis
        - date.getMillis) / 1000 > latency) {
        date = fromStrToDT(formatDateTime(DateTime.now()))
        dateMap.foreach {
          case (key, ids) =>
            // find the unique number of uids' for the corresponding timestamp.
            val count = ids.groupBy(identity).size
            val output = MinuteOutput(key, count)
            val outputJson = outputToJson(output)
            Producer.kafkaProducer(producerTopic, "key", outputJson)
            System.out.println(outputJson)
            if (key != formatDateTime(date)) {
              // remove the latest interval from the map not the current one
              dateMap.remove(key)
            }
        }
      }
      record.iterator().asScala.toList.map {
        rec =>
          // If the record is corrupted don't let it fail.
          val parsedRecord = parseRecord(rec).getOrElse(UserInput("empty", "empty"))

          // Check the timestamp is in the map or not.
          try {
            val maybeKey = dateMap.apply(parsedRecord.ts.toString)
            maybeKey += parsedRecord.uid
            dateMap(parsedRecord.ts) = maybeKey
          } catch {
            // If the key is not in the map it throws NoSuchElementException, catch and put it.
            case _: NoSuchElementException =>
              dateMap += (parsedRecord.ts.toString -> ListBuffer(parsedRecord.uid))
          }
      }
    }
  }


  def parseRecord(record: ConsumerRecord[String, String]): Try[UserInput] = {
    parseDesiredField(List("uid", "ts"), record.value())
  }

}