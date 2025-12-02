package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank

private const val STARTING_VALUE = 50
private const val MAX_VALUE = 100

data object Day01 : Puzzle {
  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    /*
    - The attached document (your puzzle input) contains a sequence of rotations
    - one per line, which tell you how to open the safe.
    - A rotation starts with an L or R which indicates whether the rotation should be to the left (toward lower numbers) or to the right (toward higher numbers).
    - Then, the rotation has a distance value which indicates how many clicks the dial should be rotated in that direction.
     */
    val rotations = input.skipBlank()
      .map { line -> line[0] to line.substring(1).toInt() }
      .map {
        when (it.first) {
          'L' -> it.second * -1
          else -> it.second
        }
      }

    /*
    - Part 1:
    - Because the dial is a circle, turning the dial left from 0 one click makes it point at 99.
    - Similarly, turning the dial right from 99 one click makes it point at 0.
    - The dial starts by pointing at 50.
    - The actual password is the number of times the dial is left pointing at 0 after any rotation in the sequence.
    */

    /*
    - Part 2:
    - "Due to newer security protocols, please use password method 0x434C49434B until further notice."
    - "method 0x434C49434B" means you're actually supposed to count the number of times any click causes the dial to point at 0
    - regardless of whether it happens during a rotation or at the end of one.
    */

    val rotationResults = rotations.fold(listOf(STARTING_VALUE to 0)) { results, nextRotation ->
      val lastValue = results.last().first
      val nextResult = (lastValue + nextRotation).mod(MAX_VALUE) to
          countZerosDuringModulo(lastValue, nextRotation)
      results + nextResult
    }

    return PuzzleSolution(
      rotationResults.count { it.first == 0 },
      rotationResults.sumOf { it.second }
    )
  }

  private fun countZerosDuringModulo(start: Int, add: Int): Int {
    val sum = start + add
    return when {
      sum >= MAX_VALUE -> sum / MAX_VALUE
      sum < 0 -> -(sum / MAX_VALUE) + if (start > 0) 1 else 0
      sum == 0 -> 1
      else -> 0
    }
  }
}

suspend fun main() {
  Day01.run()
}


