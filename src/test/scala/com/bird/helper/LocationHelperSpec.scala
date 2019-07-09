package com.bird.helper

import com.bird.model.Location
import org.scalatest._

class LocationHelperSpec extends FlatSpec {
  "A distance calculation" should "be accurate within a tenth of a mile (in meters)" in {
    val start = Location(36.12, -86.67)
    val end = Location(33.94, -118.40)

    // a tenth of a mile (in meters)
    val dDistance = 160.934

    val actual = LocationHelper.getDistance(start, end)
    val expected = 2887259.9

    assert(Math.abs(expected - actual) < dDistance)
  }
}