package com.challenge

import com.challenge.stream.kafka.Consumer

object App {

  def main(args: Array[String]): Unit = {
    kafkaApp()
  }

  def kafkaApp(): Unit = {
    Consumer.consumeFromKafka(List("test-topic"))
  }

  def stdInApp(): Unit = {

  }
}
