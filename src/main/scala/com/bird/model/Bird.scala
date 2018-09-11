package com.bird.model

case class Location(lat: Double, lng: Double)
case class Ride(origin: Location, user_id: Int, start_time: Int)

class Bird(val id: String, val dropTime: Int, val dropLocation: Location) {
  var currentLocation: Location = dropLocation
  var totalDistance: Double = 0.0
  var totalRides: Int = 0
  
  override def toString(): String = {
    s"Bird: id - $id, drop_time - $dropTime, total_rides - $totalRides, distance_travelled - $totalDistance, drop_location - (${dropLocation.lat}, ${dropLocation.lng}), current_location - (${currentLocation.lat}, ${currentLocation.lng})"
  }
}

object Bird {
  
}