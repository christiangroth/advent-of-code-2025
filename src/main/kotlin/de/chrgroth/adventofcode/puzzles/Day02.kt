package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank
import kotlin.math.sqrt

data object Day02 : Puzzle {
  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // The ranges are separated by commas (,); each range gives its first ID and last ID separated by a dash (-).
    val ranges = input.skipBlank()
      .flatMap { line -> line.split(',') }
      .mapNotNull { it.trim().takeIf { it.isNotBlank() } }
      .map {
        val boundaries = it.split('-')
        LongRange(boundaries[0].toLong(), boundaries[1].toLong())
      }

    // you can find the invalid IDs by looking for any ID which is made only of some sequence of digits repeated twice.
    // So, 55 (5 twice), 6464 (64 twice), and 123123 (123 twice) would all be invalid IDs.
    val invalidIds = ranges.flatMap {
      it.filter { id -> id.toString().isDoubleSequence() }
    }

    // Now, an ID is invalid if it is made only of some sequence of digits repeated at least twice.
    // So, 12341234 (1234 two times),
    // 123123123 (123 three times),
    // 1212121212 (12 five times),
    // and 1111111 (1 seven times) are all invalid IDs.
    val strictInvalidIds = ranges.flatMap {
      it.filter { id -> id.toString().isRepeatingPattern() }
    }

    return PuzzleSolution(
      invalidIds.sum(), strictInvalidIds.sum()
    )
  }

  private fun String.isDoubleSequence(): Boolean =
    if (length < 2) {
      false
    } else {
      val chunks = this.chunked(length / 2)
      chunks.size == 2 && chunks[0] == chunks[1]
    }

  private fun String.isRepeatingPattern(): Boolean =
    if (length < 2) {
      false
    } else {
      findDividersFor(this.toInt()).any { divider ->
        windowed(divider).distinct().size == 1
      }
    }

  private val dividerCache = mutableMapOf<Int, List<Int>>()
  private fun findDividersFor(n: Int): List<Int> =
    dividerCache.getOrPut(n) {
      (2..sqrt(n.toDouble()).toInt()).fold(emptySet<Int>()) { results, i ->
        if (n % i == 0) {
          results + i + (n / i)
        } else {
          results
        }
      }.sorted()
    }
}

suspend fun main() {
  Day02.run()
}
