package com.challenge.util

import Helper.parseTimestampToDate
import com.challenge.model.{MinuteOutput, UserInput}
import spray.json._

import scala.util.Try

object JsonParser {

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val outputFormat = jsonFormat2(MinuteOutput.apply)
  }

  /**
   * Parse the desired fields from json.
   */
  def parseDesiredField(fieldName: List[String], record: String): Try[UserInput] = {
    Try(parseToUserInput(record.parseJson.asJsObject().getFields(fieldName:_*)))
  }

  /**
   * Create UserInput with parsed fields from json input.
   */
  def parseToUserInput(value: Seq[JsValue]): UserInput = {
    val uid = value.head.toString()
    val date = parseTimestampToDate(value(1).toString().toLong)
    UserInput(uid, date)
  }

  /**
   * Generate json of the output.
   */
  def outputToJson(value: MinuteOutput): String = {
    import MyJsonProtocol._
    value.toJson.toString()
  }



}

