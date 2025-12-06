package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.skipBlank
import de.chrgroth.adventofcode.puzzles.utils.splitAtBlankLine


data object Day05 : Puzzle {

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // The database operates on ingredient IDs.
    // It consists of a list of fresh ingredient ID ranges, a blank line, and a list of available ingredient IDs
    // The fresh ID ranges are inclusive: the range 3-5 means that ingredient IDs 3, 4, and 5 are all fresh.
    // The ranges can also overlap
    val (freshIdRanges, availableIds) = input.splitAtBlankLine().let { (freshIdRangesInput, availableIdsInput) ->
      freshIdRangesInput.skipBlank().map { line ->
        line.split('-')
          .let {
            check(it.size == 2) { "Invalid range: $line" }
            it[0].toLong()..it[1].toLong()
          }
      } to availableIdsInput.skipBlank().map { line -> line.toLong() }
    }

    // an ingredient ID is fresh if it is in any range.
    val freshIngredientIds = availableIds.filter { id -> freshIdRanges.any { range -> id in range } }

    // the Elves would like to know all of the IDs that the fresh ingredient ID ranges consider to be fresh.
    // An ingredient ID is still considered fresh if it is in any range.
    val allFreshIngredientIds = combineRanges(freshIdRanges)
      .fold(0.toLong()) { count, range ->
        count + (range.last - range.first + 1)
      }

    return PuzzleSolution(
      freshIngredientIds.size, allFreshIngredientIds
    )
  }

  fun combineRanges(ranges: List<LongRange>): Set<LongRange> =
    if (ranges.isEmpty()) {
      emptySet()
    } else {
      val result = mutableSetOf<LongRange>()

      val sorted = ranges.sortedBy { it.first }
      var current = sorted[0]
      for (i in 1 until sorted.size) {
        val next = sorted[i]
        if (current.last >= next.first - 1) {
          current = current.first..maxOf(current.last, next.last)
        } else {
          result.add(current)
          current = next
        }
      }
      result.add(current)

      result
    }
}

suspend fun main() {
  Day05.run()
}
