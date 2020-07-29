package com.challenge.model

case class UserInput(uid: String, ts: String) {
  override def toString: String = s"$uid, $ts"
}
