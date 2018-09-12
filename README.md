# Bird Engineering: Coding Exercise

## How to run:

### Using .zip:

Inside the project repo, you will find the following file, scott\_pellett\_bird\_challenge.zip

Unzip the file. In the example below I am unzipping to /tmp:
```
unzip -o -d /tmp/ scott_pellett_bird_challenge.zip
```

Once the file is unzipped, from the directory that application was unzipped to run the following:
```
./bird-0.1.0/bin/bird [optional - full path to file]
```

Another example of the above command using specific file: 
```
./bird-0.1.0/bin/bird ~/dev/test.txt 
```

Running w/ default file:
```
./bird-0.1.0/bin/bird
```

### Using Docker:

Pull down the image:
```
docker pull spellett/bird
```

Once the image is downloaded, run the following command:
```
run -v ~/dev:/data spellett/bird:v1 /data/test.txt
```

Running w/ default file:
```
run spellett/bird:v1
```

What the above command does is bindmounts a local directory with my test files, in this case ~/dev,
to a new directory in the container called /data. That way I can pass files from that directory in
the container to the application spellett/bird:v1

### Notes:

In the event that no file is passed in, the application will use the sample
file given with the prompt.

## Description

Thank you for your continued interest in Bird!

This at-home coding challenge is an opportunity for you to write some clean code that shows us how you use data structures to solve simple algorithmic problems.

  * You may use any programming language of your choice.
  * Use your preferred IDE or editor and whatever tooling you're comfortable with.
  * Feel free to use whatever references you'd like, including Google.
  * When you’re finished, make sure your code is committed to the repo with instructions on how to run it.
  * Please press the submit button in the top-right corner in Py to let us know you're done.
  * Your solution should be self-contained and not require additional software to run it.

We don't expect this challenge to take that long as we know you've got a busy schedule. However, we want to understand more about you as an engineer so feel free to express your craft as you see fit. We will, however, time box you to one week from the time you receive the test.

Please complete all three questions as these are required, but if you’re up for a challenge, we are also offering up a few bonus questions.


## Bird Events:

The input to your program is a text file containing a list of Bird events from a completed simulation. Bird events are events which happen in our system, e.g. when a ride is started or ended. A drop event is when a Bird is initially put into the simulation. The format of the events is:

| Data        | Type           | Description  |
| ------------- |-------------| -----|
| timestamp       | Integer        | The time in seconds since the start of the simulation |
| bird_id       | String        | The id of the associated Bird vehicle, e.g. JK5T |
| event_type       | String        | The type of the event is one of START_RIDE, END_RIDE, DROP |
| x       | Double        | The x coordinate of the location of where the event happened in the simulation |
| y       | Double        | The y coordinate of the location of where the event happened in the simulation |
| user_id       | Integer        | The id of the associated user or NULL if the event does not have an associated user |
   
Each column is separated by a comma (,) and each line represents a single event. The list is ordered by time starting with the first event that happened.

## Goal:

The goal of the program is to parse a list of such events and print out to the command line (stdout) answers to the following questions. A list of sample events of one completed simulation is sent as a separate file, so you can test your code with them. Assume each question has exactly one valid answer, each bird has been dropped off as its first event and all rides have a start and end event.

## Questions:

1. What is the total number of Bird vehicles dropped off in the simulation?
2. Which Bird ends up the farthest away from its drop location? What is the distance?
3. Which Bird has traveled the longest distance in total on all of its rides? How far is it?

## Bonus:

4. Which user has paid the most? How much is it? The cost of a ride is $1 to start and $0.15 for every started minute. If the ride lasts less than 1 minute, the cost is $0.
For example:
- Ride time 98 seconds = $1.30
- Ride time 153 seconds = $1.45
- Ride time 52 seconds = $0.00
5. Which Bird has the longest wait time between two rides? How many seconds is it?
6. What is the average speed travelled across all rides?
at long as we know you've got a busy schedule. However, we want to understand more about you as an engineer so feel free to express your craft as you see fit. We will, however, time box you to one week from the time you receive the test.

Please complete all three questions as these are required, but if you’re up for a challenge, we are also offering up a few bonus questions.
