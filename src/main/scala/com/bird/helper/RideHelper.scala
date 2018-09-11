package com.bird.helper

object RideHelper {
  val MINUTE = 60

  /**
   * Computes the cost of a ride based on the time in seconds. If the ride
   * lasts under a minute (60 seconds), the ride is free. Otherwise, there is a
   * flat fee of $1 and $0.15 for every started minute.
   *
   * totalTime - the ride length
   */
  def getCost(totalTime: Int): Double = {
    if (totalTime < MINUTE) {
      0.0
    }
    else {
      val flatFee = 1
      var minutes = totalTime / MINUTE

      // If the totalTime is not evenly divisible by a minute, then taht means
      // we've started another minute an need to charge for it.
      if (totalTime % MINUTE > 0) {
        minutes += 1
      }

      flatFee + (.15 * minutes)
    }
  }


  def prettyCost(cost: Double): String = {
    val formatter = java.text.NumberFormat.getCurrencyInstance

    formatter.format(cost)
  }
}