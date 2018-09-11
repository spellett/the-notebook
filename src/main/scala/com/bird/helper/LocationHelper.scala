package com.bird.helper

import com.bird.model.Location
import scala.math._

object LocationHelper {
  val METER_IN_KM = .001
  val METER_IN_MILES = .00062137
  val RADIUS_IN_METERS = 6372800

  /**
   * 1 meter = .000621371 miles
   */
  def convertMetersToKM(meters: Double): Double = {
    METER_IN_KM * meters
  }


  /**
   * 1 meter = .000621371 miles
   */
  def convertMetersToMiles(meters: Double): Double = {
    METER_IN_MILES * meters
  }


  /**
   * Computes the haversine distance between to locations.
   *
   * The haversine formula determines the great-circle distance between two 
   * points on a sphere given their longitudes and latitudes. 
   * 
   * This code is from https://rosettacode.org/wiki/Haversine_formula#Scala 
   */
  def getDistance(start: Location, end: Location): Double = {
    val dLat = (end.lat - start.lat).toRadians
    val dLng = (end.lng - start.lng).toRadians

    val a = pow(sin(dLat/2), 2) + pow(sin(dLng/2), 2) * cos(start.lat.toRadians) * cos(end.lat.toRadians)
    val c = 2 * asin(sqrt(a))

    RADIUS_IN_METERS * c
  }
}