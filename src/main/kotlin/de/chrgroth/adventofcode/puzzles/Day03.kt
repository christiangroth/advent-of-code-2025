package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank

data object Day03 : Puzzle {
  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // When you get to the main elevators, however, you discover that each one has a red light above it: they're all offline.
    // There are batteries nearby that can supply emergency power to the escalator for just such an occasion.
    // The batteries are each labeled with their joltage rating, a value from 1 to 9. You make a note of their joltage ratings (your puzzle input).
    // The batteries are arranged into banks; each line of digits in your input corresponds to a single bank of batteries.

    val banks = input.skipBlank()
      .map { it.trim().map { char -> char.digitToInt() } }

    // Within each bank, you need to turn on exactly two batteries;
    // the joltage that the bank produces is equal to the number formed by the digits on the batteries you've turned on.
    // For example, if you have a bank like 12345 and you turn on batteries 2 and 4, the bank would produce 24 jolts.
    // (You cannot rearrange batteries.)

    // You'll need to find the largest possible joltage each bank can produce.
    // The total output joltage is the sum of the maximum joltage from each bank

    val maxJoltages = banks.map {
      it.maxJoltage()
    }

    return PuzzleSolution(
      maxJoltages.sum(), null
    )
  }

  private fun List<Int>.maxJoltage(): Int {
    println(this)
    val firstDigit = dropLast(1).findHighestContainedDigit()
    print(firstDigit)
    val firstDigitIndex = indexOf(firstDigit)
    val secondDigit = drop(firstDigitIndex + 1).findHighestContainedDigit()
    println(secondDigit)
    return "$firstDigit$secondDigit".toInt()
  }

  private fun List<Int>.findHighestContainedDigit(): Int =
    (9 downTo 1).firstOrNull { digit ->
      contains(digit)
    } ?: 0
}

suspend fun main() {
  Day03.run()
}
