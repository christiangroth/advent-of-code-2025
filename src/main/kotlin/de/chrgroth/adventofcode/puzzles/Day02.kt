package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank

data object Day02 : Puzzle {
  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // The ranges are separated by commas (,); each range gives its first ID and last ID separated by a dash (-).
      val ranges = input.skipBlank()
          .flatMap { line -> line.split(',') }
          .mapNotNull { it.trim().takeIf { it.isNotBlank()} }
          .map { IntRange(it.split('-')[0].toInt(), it.split('-')[1].toInt()) }

      // you can find the invalid IDs by looking for any ID which is made only of some sequence of digits repeated twice.
      // So, 55 (5 twice), 6464 (64 twice), and 123123 (123 twice) would all be invalid IDs.
      val invalidIds = ranges.flatMap {
          it.filter { id -> id.toString().isDoubleSequence() }
      }

    return PuzzleSolution(
        invalidIds.sum(),
        null
    )
  }

    private fun String.isDoubleSequence(): Boolean {
        val chunks = this.chunked(length / 2)
        return chunks.size == 2 && chunks[0] == chunks[1]
    }
}

suspend fun main() {
  Day02.run()
}
