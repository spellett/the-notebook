package com.bird.helper

import org.scalatest._
import com.bird.common.LegacyUnitSpec

class RideHelperTest extends LegacyUnitSpec {
  test("Ride time 98 seconds = $1.30") {
    assert(RideHelper.prettyCost(RideHelper.getCost(98)) == "$1.30")
  }

  test("Ride time 153 seconds = $1.45") {
    assert(RideHelper.prettyCost(RideHelper.getCost(153)) == "$1.45")
  }

  test("Ride time 52 seconds = $0.00") {
    assert(RideHelper.prettyCost(RideHelper.getCost(52)) == "$0.00")
  }

  test("Ride time 60 seconds = $1.15") {
    assert(RideHelper.prettyCost(RideHelper.getCost(60)) == "$1.15")
  }
}