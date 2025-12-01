package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank

const val STARTING_VALUE = 50
const val MAX_VALUE = 100

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
    - Because the dial is a circle, turning the dial left from 0 one click makes it point at 99.
    - Similarly, turning the dial right from 99 one click makes it point at 0.
    - The dial starts by pointing at 50.
    - The actual password is the number of times the dial is left pointing at 0 after any rotation in the sequence.
    */
    val rotatedValues = rotations.fold(listOf(STARTING_VALUE)) { result, next ->
      val nextValue = ((result.last() + next).mod(MAX_VALUE))
      println("${result.last()} + $next = $nextValue")

      result + nextValue
    }

    return PuzzleSolution(rotatedValues.count { it == 0 }, null)
  }
}

suspend fun main() {
  Day01.run()
}
