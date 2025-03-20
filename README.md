**Dice Game**

**Overview**

The Dice Game is a command-line Java application that allows players to compete against the computer using special dice with predefined values. The game ensures fair play using cryptographic techniques such as HMAC and SecureRandom.

**Features**

Supports three or more dice, each with exactly six faces.
Uses HMAC (SHA3-256) for provable fair random number generation.
Players and the computer take turns selecting dice and rolling them.
The first move is determined fairly using a cryptographic random selection process.
ASCII-based user interface for an interactive gameplay experience.

**Prerequisites**

Java 17 or later

A terminal or command prompt to run the game

**Installation**

Clone the repository:

Compile the Java source files:
Or open the project with IntelliJ IDEA

**Usage**

Run the game with command-line arguments specifying at least three dice configurations:

**Handling Errors**

The program gracefully handles the following errors:
Less than three dice provided.
Incorrect dice format (e.g., non-integer values).
Incorrect input for dice selection or rolling.
How Fair Randomness Works
The computer generates a cryptographically secure random key (256-bit).
It selects a random number in the required range and computes HMAC.
The HMAC is displayed before the player selects their number.
The player selects a number, and both values are combined using modular arithmetic.
The key is revealed to verify fairness.

**Contributing**

Feel free to submit issues or download the game and modify it for yourself.

**License**

This project is licensed under the MIT License.

____________________________________________________________
Enjoy the game it may the best dice game I've ever created!
