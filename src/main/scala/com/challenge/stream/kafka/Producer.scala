package com.challenge.stream.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object Producer {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props)

  /**
   * Send Record to the kafka.
   */
  def kafkaProducer(topic: String, key: String, value: String): Unit = {
    val record = new ProducerRecord[String, String](topic, key, value)
    producer.send(record)
  }

  /**
   * Don't open the producer each time. But also, don't forget the close.
   */
  def closeProducer(): Unit = {
    producer.close()
  }
}
