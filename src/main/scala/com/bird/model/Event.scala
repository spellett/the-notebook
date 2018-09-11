package com.bird.model

case class Event(timestamp: Int, bird_id: String, event_type: String, lng: Double, lat: Double, user_id: Option[Int])