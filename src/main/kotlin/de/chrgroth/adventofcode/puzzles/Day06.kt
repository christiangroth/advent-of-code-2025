package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank

data object Day06 : Puzzle {

  enum class MathOperation { ADD, MULTIPLY }
  enum class MathMode { NAIVE, CEPHALOPOD }
  data class MathProblem(val operation: MathOperation, val rawNumbersInput: List<String>) {
    fun solve(mode: MathMode): Long =
      convertNumbers(mode).reduce { a, b ->
        when (operation) {
          MathOperation.ADD -> a + b
          MathOperation.MULTIPLY -> a * b
        }
      }

    private fun convertNumbers(mode: MathMode): List<Long> =
      when (mode) {
        MathMode.NAIVE -> rawNumbersInput.map { it.trim().toLong() }
        MathMode.CEPHALOPOD -> {

          // Cephalopod math is written right-to-left in columns.
          // Each number is given in its own column, with the most significant digit at the top and the least significant digit at the bottom.
          // (Problems are still separated with a column consisting only of spaces, and the symbol at the bottom of the problem is still the operator to use.)

          val maxDigits = rawNumbersInput.maxBy { it.length }.length
          // fix padding needed for last column
          val rightToLeftReadyInputs = rawNumbersInput.map { it.padEnd(maxDigits, ' ') }
          (0 until maxDigits).map { digitIndex ->
            rightToLeftReadyInputs.mapNotNull { input ->
              input[digitIndex].let { char ->
                if (char.isDigit()) char else null
              }
            }.joinToString(separator = "").toLong()
          }
        }
      }
  }

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // The math worksheet (your puzzle input) consists of a list of problems;
    // each problem has a group of numbers that need to be either added (+) or multiplied (*) together.
    val lines = input.skipBlank()
    val columnStartIndexes = lines.last().mapIndexedNotNull { index, char ->
      if (char.isWhitespace()) null else index
    }
    val mathProblems = lines
      .map { line ->
        columnStartIndexes.windowed(2).map { (columnStartIndex, columnEndIndex) ->
          line.substring(columnStartIndex, columnEndIndex - 1)
        } + line.substring(columnStartIndexes.last())
      }.let { lines ->
        val numberOfProblems = lines[0].size
        (0 until numberOfProblems).map { index ->
          MathProblem(
            operation = when (lines.last()[index].trim()) {
              "+" -> MathOperation.ADD
              "*" -> MathOperation.MULTIPLY
              else -> throw IllegalArgumentException("Unmapped operation.")
            },
            rawNumbersInput = (0 until lines.size - 1).map { lineIndex ->
              lines[lineIndex][index]
            }
          )
        }
      }

    // To check their work, cephalopod students are given the grand total of adding together all of the answers to the individual problems.
    val naiveSolutions = mathProblems.sumOf { it.solve(MathMode.NAIVE) }
    val cephalopodSolutions = mathProblems.sumOf { it.solve(MathMode.CEPHALOPOD) }

    return PuzzleSolution(
      naiveSolutions, cephalopodSolutions
    )
  }
}

suspend fun main() {
  Day06.run()
}
