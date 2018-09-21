package com.bird

import com.bird.helper.{LocationHelper, RideHelper}
import com.bird.model._
import scala.io.Source
import java.io.FileNotFoundException

/**
 * Nothing to see here.
 *
 * Author: Scott Pellett - spellett@gmail.com
 */
object BirdChallenge extends App {
  // Event types
  val DROP = "DROP"
  val END_RIDE = "END_RIDE"
  val START_RIDE = "START_RIDE"

  val MISSING = "NULL"
  
  val NO_BIRDS_RESPONSE = "Answer: No Birds dropped during the simulation\n"

  def getAvgSpeedAcrossAllRides(birds: List[Bird], totalTravelDistance: Double, totalTravelDuration: Int): Unit = {
    val totalRides = birds.foldLeft(0){(total, b) => total + b.totalRides}
    val totalMiles = LocationHelper.convertMetersToMiles(totalTravelDistance)
    val totalHours = (totalTravelDuration.toDouble / 3600.toDouble)

    val avgMPH = (totalMiles / totalHours) / totalRides

    println("6. What is the average speed travelled across all rides?")
    println(s"Answer: The average speed across all rides was $avgMPH MPH")
  }

  def getFurthestBirdFromOrigin(birds: List[Bird]): Unit = {
    println("2. Which Bird ends up the farthest away from its drop location? What is the distance?")

    if (birds.isEmpty) {
      println(NO_BIRDS_RESPONSE)
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

      println(s"Answer: $furthestBirdId is ${LocationHelper.convertMetersToMiles(furthestDistance)} mi away from its drop location\n")
    }
  }


  def getMostTravelledBird(birds: List[Bird]): Unit = {
    println("3. Which Bird has traveled the longest distance in total on all of its rides? How far is it?")

    if (birds.isEmpty) {
      println(NO_BIRDS_RESPONSE)
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

      println(s"Answer: $mostTravelledBirdId is the most travelled at a distance of ${LocationHelper.convertMetersToMiles(maxDistance)} mi\n")
    }
  }


  def getUserWithLargestBalance(users: List[User]): Unit = {
    println("4. Which user has paid the most? How much is it?")

    if (users.isEmpty) {
      println("No users rode any Birds")
    }
    else {
      var maxSpend = 0.0
      var topSpendingUser: String = ""

      users.foreach {
        user =>
          if (user.balance >= maxSpend) {
            maxSpend = user.balance
            topSpendingUser = user.id.toString
          }
      }

      println(s"Answer: $topSpendingUser has the highest balance at ${RideHelper.prettyCost(maxSpend)}\n")
    }
  }

  var totalTravelDistance = 0.0 // in meters
  var totalTravelDuration = 0 // in seconds

  var maxDowntime = 0.0 // in seconds
  var maxDowntimeBird = ""

  var activeRides = Map[String, Ride]() // Holds rides that are actively in progress, keyed by Bird id
  var birds = Map[String, Bird]() // Holds all Birds in the simulation, keyed by Bird id

  // Holds all Birds that are idle, keyed by Bird id, value is when the Bird was last used
  var idleBirds = Map[String, Int]()
  
  var users = Map[Int, User]() // Holds all users in the simulation, keyed by user id

  try {
    // If no file is passed into the program, we will use the given example file
    // as the source of this run.
    val sourceFile = if (args.nonEmpty) {
      Option(args(0)) match {
        case Some(requestedFile) if requestedFile.nonEmpty => Source.fromFile(requestedFile)
        case _ => Source.fromResource("events.txt")
      }
    }
    else {
      Source.fromResource("events.txt")
    }

    for (line <- sourceFile.getLines) {
      val data = line.split(",").toList

      // Expected format of input file - timestamp: Int, bird_id: String, event_type: String, lng: Double, lat: Double, user_id: Option[Int]

      // Converts the input's user_id from a string into the correct Int data 
      // type. When there is no user_id associated with the event the value 
      // "NULL" is passed as the value which cannot be converted into an Int so
      // we test for this value and turn our user id into Option[Int]
      val userId = data(5) match {
        case uid: String if uid != MISSING => Option(uid.toInt)
        case _ => None
      }

      val event = Event(data(0).toInt, data(1), data(2), data(3).toDouble, data(4).toDouble, userId)

      event.event_type match {
        case DROP => {
          val bird = new Bird(event.bird_id, event.timestamp, Location(event.lat, event.lng))

          birds = birds + (event.bird_id -> bird)
        }
        case END_RIDE => {
          birds.get(event.bird_id) match {
            case Some(bird) => {
              // Update the bird's geocoordinates
              bird.currentLocation = Location(event.lat, event.lng)

              activeRides.get(bird.id) match {
                case Some(ride) => {
                  val rideDistance = LocationHelper.getDistance(ride.origin, Location(event.lat, event.lng))
                  totalTravelDistance += rideDistance

                  val rideDuration = event.timestamp - ride.start_time
                  totalTravelDuration += rideDuration

                  // Update the bird's distance travelled and ride count
                  bird.totalDistance += rideDistance
                  bird.totalRides += 1

                  // Update the user's charges and rides taken
                  users.get(ride.user_id) match {
                    case Some(user) => {
                      user.balance += RideHelper.getCost(rideDuration)
                      user.ridesTaken += 1
                    }
                    case _ => // Unknown user
                  }

                  // Mark the bird as idle (inbetween rides)
                  idleBirds = idleBirds + (bird.id -> event.timestamp)
                }
                case _ => // Why are we ending a ride that was not started?
              }
            }
            case _ => // If this happens then that means we did no log the Bird dropping into the system
          }
        }
        case START_RIDE => {
          event.user_id match {
            case Some(uid) => {
              // Add user to the system if they've never ridden before
              if (!users.contains(uid)) {
                users = users + (uid -> new User(uid))
              }

              // Check to see if the bird used was idle (between rides)
              idleBirds.get(event.bird_id) match {
                case Some(idleStart) => {
                  val idleTime = event.timestamp - idleStart

                  // If the Bird has the longest idle so far, log it
                  if (idleTime >= maxDowntime) {
                    maxDowntime = idleTime
                    maxDowntimeBird = event.bird_id
                  }
                }
                case _ => 
              }

              // Log an active ride, we use the id of the Bird in use as the key.
              // When the ride ends we will remove the entry.
              activeRides = activeRides + (event.bird_id -> Ride(Location(event.lat, event.lng), uid, event.timestamp))
            }
            case _ => // Can't start a ride without a user
          }
        }
        case _ => // Unknown event type
      }
    }

    // Question 1
    println("1. What is the total number of Bird vehicles dropped off in the simulation?")
    println(s"Answer: ${birds.keySet.size} Birds were dropped off in the simulation\n")

    // Question 2
    getFurthestBirdFromOrigin(birds.values.toList)

    // Question 3
    getMostTravelledBird(birds.values.toList)

    // Question 4
    getUserWithLargestBalance(users.values.toList)

    // Question 5
    println("5. Which Bird has the longest wait time between two rides? How many seconds is it?")
    println(s"Answer: $maxDowntimeBird had the longest idle time between rides at $maxDowntime seconds\n")

    // Question 6
    getAvgSpeedAcrossAllRides(birds.values.toList, totalTravelDistance, totalTravelDuration)
  }
  catch {
    case fnf: FileNotFoundException => println("Unable to find requested file. Please check your path")
    case ex: Exception => println(ex.toString)
  }
}