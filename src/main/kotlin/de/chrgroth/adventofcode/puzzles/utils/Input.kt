package de.chrgroth.adventofcode.puzzles.utils

internal fun List<String>.skipBlank(): List<String> =
  filter { it.isNotBlank() }

fun List<String>.splitAtBlankLine(): Pair<List<String>, List<String>> =
  splitAt { it.isBlank() }

fun List<String>.splitAt(predicate: (String) -> Boolean): Pair<List<String>, List<String>> {
  val index = indexOfFirst(predicate)
  return if (index == -1) {
    this to emptyList()
  } else {
    subList(0, index) to subList(index + 1, size)
  }
}
