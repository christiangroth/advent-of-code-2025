package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank

data object Day06 : Puzzle {

  enum class MathOperation { ADD, MULTIPLY }
  data class MathProblem(val operation: MathOperation, val numbers: List<Long>) {
    fun solve(): Long =
      numbers.reduce { a, b ->
        when (operation) {
          MathOperation.ADD -> a + b
          MathOperation.MULTIPLY -> a * b
        }
      }
  }

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // The math worksheet (your puzzle input) consists of a list of problems;
    // each problem has a group of numbers that need to be either added (+) or multiplied (*) together.
    val mathProblems = input.skipBlank()
      .map {
        it.split("\\s+".toRegex()).filter { it.isNotBlank() }
      }.let { lines ->
        val numberOfProblems = lines[0].size
        (0 until numberOfProblems).map { index ->
          MathProblem(
            operation = when (lines.last()[index]) {
              "+" -> MathOperation.ADD
              "*" -> MathOperation.MULTIPLY
              else -> throw IllegalArgumentException("Unmapped operation.")
            },
            numbers = (0 until lines.size - 1).map { lineIndex ->
              lines[lineIndex][index].toLong()
            }
          )
        }
      }

    // To check their work, cephalopod students are given the grand total of adding together all of the answers to the individual problems.
    val solutions = mathProblems.map { it.solve() }.sum()

    return PuzzleSolution(
      solutions, null
    )
  }
}

suspend fun main() {
  Day06.run()
}
