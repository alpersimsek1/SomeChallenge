package com.challenge.model

case class MinuteOutput(timestamp: String, uniqueUserNumber: Int) {
  override def toString: String = s"$timestamp, $uniqueUserNumber"
}
