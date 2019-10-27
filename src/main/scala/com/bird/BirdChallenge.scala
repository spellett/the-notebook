package com.bird

import com.bird.helper.{LocationHelper, RideHelper}
import com.bird.model._
import com.bird.service.BirdService
import scala.io.Source
import com.typesafe.config.ConfigFactory
import com.redis._
import java.io.FileNotFoundException
import com.typesafe.scalalogging.LazyLogging

/**
 * Nothing to see here.
 *
 * Author: Scott Pellett - spellett@gmail.com
 */
object BirdChallenge extends App with LazyLogging {
  val redisConfig = ConfigFactory.load().getConfig("redis")

  val redisHost = redisConfig.getString("host")

  val redisPort = redisConfig.getInt("port")

  // Event types
  val Drop = "DROP"
  val EndRide = "END_RIDE"
  val StartRide = "START_RIDE"

  val Missing = "NULL"
  
  val NoBirdsResponse = "Answer: No Birds dropped during the simulation\n"

  def getUserWithLargestBalance(cache: RedisClient): Unit = {
    logger.info("4. Which user has paid the most? How much is it?")

    cache.zrangeWithScore("spenders", 0, 0, RedisClient.DESC) match {
      case Some(maxSpend) => logger.info(s"Answer: ${maxSpend.head._1} has the highest balance at ${RideHelper.prettyCost(maxSpend.head._2)}\n")
      case _ => logger.info("No users rode any Birds")
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
    val cache = new RedisClient(redisHost, redisPort)

    cache.set("birdcount", 0)

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
        case uid: String if uid != Missing => Option(uid.toInt)
        case _ => None
      }

      val event = Event(data(0).toInt, data(1), data(2), data(3).toDouble, data(4).toDouble, userId)

      event.event_type match {
        case Drop => {
          cache.incr("birdcount")

          val bird = new Bird(event.bird_id, event.timestamp, Location(event.lat, event.lng))

          birds = birds + (event.bird_id -> bird)
        }
        case EndRide => {
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
                      cache.zscore("spenders", ride.user_id) match {
                        case Some(existingSpend) => cache.zadd("spenders", (existingSpend + RideHelper.getCost(rideDuration)), ride.user_id)
                        case _ => cache.zadd("spenders", RideHelper.getCost(rideDuration), ride.user_id)
                      }
                    
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
        case StartRide => {
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
    logger.info("1. What is the total number of Bird vehicles dropped off in the simulation?")
    logger.info(s"Answer: ${cache.get("birdcount").getOrElse(0)} Birds were dropped off in the simulation\n")

    // Question 2
    logger.info("2. Which Bird ends up the farthest away from its drop location? What is the distance?")

    BirdService.getFurthestBirdFromOrigin(birds.values.toList) match {
      case Some(bird) => logger.info(s"Answer: ${bird.id} is ${bird.distance} mi away from its drop location\n")
      case _ => logger.info(NoBirdsResponse)
    }

    // Question 3
    logger.info("3. Which Bird has traveled the longest distance in total on all of its rides? How far is it?")
    
    BirdService.getMostTravelledBird(birds.values.toList) match {
      case Some(bird) => logger.info(s"Answer: ${bird.id} is the most travelled at a distance of ${bird.distance} mi\n")
      case _ => logger.info(NoBirdsResponse)
    }

    // Question 4
    getUserWithLargestBalance(cache)

    // Question 5
    logger.info("5. Which Bird has the longest wait time between two rides? How many seconds is it?")
    logger.info(s"Answer: $maxDowntimeBird had the longest idle time between rides at $maxDowntime seconds\n")

    // Question 6
    logger.info("6. What is the average speed travelled across all rides?")

    val avgMPH = BirdService.getAvgSpeedAcrossAllRides(birds.values.toList, totalTravelDistance, totalTravelDuration)
    logger.info(s"Answer: The average speed across all rides was $avgMPH MPH")
  }
  catch {
    case fnf: FileNotFoundException => logger.info("Unable to find requested file. Please check your path")
    case ex: Exception => logger.info(ex.toString)
  }
}