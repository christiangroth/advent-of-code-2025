package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank

data object Day11 : Puzzle {

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    val input = input.skipBlank()

    return PuzzleSolution(
      0,
      null
    )
  }
}

suspend fun main() {
  Day11.run()
}
