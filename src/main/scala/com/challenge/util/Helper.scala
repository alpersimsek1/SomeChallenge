package com.challenge.util

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Helper {

  private val dateTimeFormat = "yyyy-MM-dd'T'HH:mm"

  /**
   * Parse Timestamp to DateTime Format.
   */
  def parseTimestampToDate(ts: Long): String = {
    formatDateTime(new DateTime(ts * 1000L))
  }

  /**
   * Format DateTime to desired format.
   */
  def formatDateTime(dt: DateTime): String = {
    val formatter = DateTimeFormat.forPattern(dateTimeFormat)
    dt.toString(formatter)
  }

  /**
   * Parse String DateTime to DateTime Object.
   */
  def fromStrToDT(dt: String): DateTime = {
    val formatter = DateTimeFormat.forPattern(dateTimeFormat)
    formatter.parseDateTime(dt)
  }


}
