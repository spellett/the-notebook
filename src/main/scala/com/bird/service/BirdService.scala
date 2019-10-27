package com.bird.service

import com.bird.helper.LocationHelper
import com.bird.model.Bird

case class FurthestBird(id: String, distance: Double)
case class MostTravelledBird(id: String, distance: Double)

object BirdService {
  def getAvgSpeedAcrossAllRides(birds: List[Bird], totalTravelDistance: Double, totalTravelDuration: Int): Double = {
    val totalRides = birds.foldLeft(0){(total, b) => total + b.totalRides}
    val totalMiles = LocationHelper.convertMetersToMiles(totalTravelDistance)
    val totalHours = (totalTravelDuration.toDouble / 3600.toDouble)

    val avgMPH: Double = (totalMiles / totalHours) / totalRides

    avgMPH
  }


  def getFurthestBirdFromOrigin(birds: List[Bird]): Option[FurthestBird] = {
    if (birds.isEmpty) {
      None
    }
    else {
      var furthestDistance = 0.0
      var furthestBirdId: String = ""

      birds.foreach {
        bird =>
          // The distance the bird is from its' origin
          val distance = LocationHelper.getDistance(bird.dropLocation, bird.currentLocation)

          // In the event of a tie, the last Bird to be processed will be 
          // determined "the furthest"
          if (distance >= furthestDistance) {
            furthestDistance = distance
            furthestBirdId = bird.id
          }
      }

      Some(FurthestBird(furthestBirdId, LocationHelper.convertMetersToMiles(furthestDistance)))
    }
  }


  def getMostTravelledBird(birds: List[Bird]): Option[MostTravelledBird] = {
    if (birds.isEmpty) {
      None
    }
    else {
      var maxDistance = 0.0
      var mostTravelledBirdId: String = ""

      birds.foreach {
        bird =>
          if (bird.totalDistance >= maxDistance) {
            maxDistance = bird.totalDistance
            mostTravelledBirdId = bird.id
          }
      }

      Some(MostTravelledBird(mostTravelledBirdId, LocationHelper.convertMetersToMiles(maxDistance)))
    }
  }
}