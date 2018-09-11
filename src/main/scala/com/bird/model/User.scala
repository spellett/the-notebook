package com.bird.model

class User(val id: Int) {
  var balance: Double = 0.0
  var ridesTaken: Int = 0

  override def toString(): String = {
    s"User: id - $id, rides_taken - $ridesTaken, balance - $balance"
  }
}

object User {

}