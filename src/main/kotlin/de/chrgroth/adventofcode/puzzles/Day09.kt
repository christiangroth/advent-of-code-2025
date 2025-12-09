package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.Coordinate
import de.chrgroth.adventofcode.puzzles.utils.skipBlank
import kotlin.math.abs

data object Day09 : Puzzle {

  internal data class Rectangle(val a: Coordinate, val b: Coordinate) {

    fun area(): Long = abs(b.x - a.x).inc() * abs(b.y - a.y).inc()

    override fun toString(): String {
      return "Rectangle(a=$a, b=$b, area=${area()})"
    }
  }

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    val redTilePositions = input.skipBlank().map { line ->
      line.split(',').let { parts ->
        check(parts.size == 2) { "Need two values for coordinate!" }
        Coordinate(x = parts[0].toLong(), y = parts[1].toLong())
      }
    }

    // Using two red tiles as opposite corners, what is the largest area of any rectangle you can make?
    val rectangles = mutableListOf<Rectangle>()
    for (i in 0 until redTilePositions.size) {
      for (j in i + 1 until redTilePositions.size) {
        val c1 = redTilePositions[i]
        val c2 = redTilePositions[j]

        if (c1.x == c2.x || c1.y == c2.y) {
          continue
        }

        rectangles.add(Rectangle(c1, c2))
      }
    }

    return PuzzleSolution(
      rectangles.maxOfOrNull { it.area() } ?: 0,
      null
    )
  }
}

suspend fun main() {
  Day09.run()
}
