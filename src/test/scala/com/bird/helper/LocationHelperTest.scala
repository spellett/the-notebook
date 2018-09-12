package com.bird.helper

import com.bird.model.Location
import org.scalatest._

class LocationHelperTest extends FunSuite with DiagrammedAssertions {
  test("Distance between (36.12, -86.67) and (33.94, -118.40)") {
    val start = Location(36.12, -86.67)
    val end = Location(33.94, -118.40)

    // Result is acceptable if it is within a tenth of a mile (in meters)
    val dDistance = 160.934

    val actual = LocationHelper.getDistance(start, end)
    val expected = 2887259.9

    assert(Math.abs(expected - actual) < dDistance)
  }
}