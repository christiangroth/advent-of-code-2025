package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.Coordinate
import de.chrgroth.adventofcode.puzzles.utils.Topology
import de.chrgroth.adventofcode.puzzles.utils.skipBlank
import kotlin.math.abs

data object Day09 : Puzzle {

  internal data class Rectangle(val a: Coordinate, val b: Coordinate) {

    fun areaSize(): Long = abs(b.x - a.x).inc() * abs(b.y - a.y).inc()

    fun areaPositions(): Set<Coordinate> {
      val minX = minOf(a.x, b.x)
      val maxX = maxOf(a.x, b.x)
      val minY = minOf(a.y, b.y)
      val maxY = maxOf(a.y, b.y)

      return buildSet {
        for (x in minX..maxX) {
          for (y in minY..maxY) {
            add(Coordinate(x = x, y = y))
          }
        }
      }
    }

    override fun toString(): String {
      return "Rectangle(a=$a, b=$b, area=${areaSize()})"
    }
  }

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    val redTilePositions = input.skipBlank().map { line ->
      line.split(',').let { parts ->
        check(parts.size == 2) { "Need two values for coordinate!" }
        Coordinate(x = parts[0].toLong(), y = parts[1].toLong())
      }
    }

    val baseTopology = Topology<Unit>(
      rows = redTilePositions.maxOf { it.y }.toInt() + 3,
      columns = redTilePositions.maxOf { it.x }.toInt() + 3,
      obstaclePositions = redTilePositions
    )

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

    // The Elves just remembered: they can only switch out tiles that are red or green. So, your rectangle can only include red or green tiles.

    // In your list, every red tile is connected to the red tile before and after it by a straight line of green tiles.
    // The list wraps, so the first red tile is also connected to the last red tile.
    // Tiles that are adjacent in your list will always be on either the same row or the same column
    val outerGreenTiles = redTilePositions.windowed(size = 2, step = 1).flatMap {
      it[0].connectTo(it[1])
    } + redTilePositions.first().connectTo(redTilePositions.last()).toSet()

    if (stage == Stage.TEST) {
      baseTopology.copy(
        pointsOfInterest = outerGreenTiles.map { it to Unit }
      ).dump()
    }

    // In addition, all of the tiles inside this loop of red and green tiles are also green.
    // Using two red tiles as opposite corners, what is the largest area of any rectangle you can make using only red and green tiles?
    val innerGreenTilePosition = (redTilePositions + outerGreenTiles).findPositionsInside()
    val redAndGreenTilePositions = innerGreenTilePosition + outerGreenTiles.toSet() + redTilePositions.toSet()

    return PuzzleSolution(
      rectangles.maxOfOrNull { it.areaSize() } ?: 0,
      rectangles.sortedByDescending { it.areaSize() }.first { rectangle ->
        rectangle.areaPositions().all { redAndGreenTilePositions.contains(it) }
      }.areaSize()
    )
  }

  private fun Coordinate.connectTo(other: Coordinate): List<Coordinate> {
    check(x == other.x || y == other.y) { "Need matching x or y coordinate!" }

    return if (x == other.x) {
      val minY = minOf(y, other.y)
      val maxY = maxOf(y, other.y)

      (minY + 1 until maxY).map {
        Coordinate(x = x, y = it)
      }
    } else {
      val minX = minOf(x, other.x)
      val maxX = maxOf(x, other.x)

      (minX + 1 until maxX).map {
        Coordinate(x = it, y = y)
      }
    }
  }

  private fun List<Coordinate>.findPositionsInside(): Set<Coordinate> {
    val minX = minOf { it.x }
    val maxX = maxOf { it.x }
    val minY = minOf { it.y }
    val maxY = maxOf { it.y }

    val cachedOutline = toSet()
    val insidePositions = mutableSetOf<Coordinate>()
    for (x in minX..maxX) {
      for (y in minY..maxY) {
        val candidate = Coordinate(x = x, y = y)
        if (!cachedOutline.contains(candidate)) {
          val xInside = any { it.x < candidate.x && it.y == candidate.y } && any { it.x > candidate.x && it.y == candidate.y }
          val yInside = any { it.y < candidate.y && it.x == candidate.x } && any { it.y > candidate.y && it.x == candidate.x }
          if (xInside && yInside) {
            insidePositions.add(candidate)
          }
        }
      }
    }
    return insidePositions
  }
}

suspend fun main() {
  Day09.run()
}
