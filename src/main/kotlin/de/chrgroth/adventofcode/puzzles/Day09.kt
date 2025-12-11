package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.Coordinate
import de.chrgroth.adventofcode.puzzles.utils.skipBlank
import kotlin.math.abs
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

data object Day09 : Puzzle {

  internal data class Rectangle(val a: Coordinate, val b: Coordinate) {

    fun areaSize(): Long = abs(b.x - a.x).inc() * abs(b.y - a.y).inc()

    fun outlinePositions(): Set<Coordinate> {
      val minX = minOf(a.x, b.x)
      val maxX = maxOf(a.x, b.x)
      val minY = minOf(a.y, b.y)
      val maxY = maxOf(a.y, b.y)

      return buildSet {
        for (x in minX..maxX) {
          add(Coordinate(x = x, y = minY))
        }
        for (x in minX..maxX) {
          add(Coordinate(x = x, y = maxY))
        }
        for (y in (minY + 1) until maxY) {
          add(Coordinate(x = minX, y = y))
        }
        for (y in (minY + 1) until maxY) {
          add(Coordinate(x = maxX, y = y))
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

    // Using two red tiles as opposite corners, what is the largest area of any rectangle you can make?
    val rectangles = mutableListOf<Rectangle>()
    measureTime {
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
    }.also {
      println("$stage rec took $it")
    }


    // The Elves just remembered: they can only switch out tiles that are red or green. So, your rectangle can only include red or green tiles.
    // In your list, every red tile is connected to the red tile before and after it by a straight line of green tiles.
    // The list wraps, so the first red tile is also connected to the last red tile.
    // Tiles that are adjacent in your list will always be on either the same row or the same column
    val outerGreenTiles = measureTimedValue {
      redTilePositions.windowed(size = 2, step = 1).flatMap {
        it[0].connectTo(it[1])
      } + redTilePositions.first().connectTo(redTilePositions.last()).toSet()
    }.let {
      println("$stage outer took ${it.duration}")
      it.value
    }

    // In addition, all of the tiles inside this loop of red and green tiles are also green.
    // Using two red tiles as opposite corners, what is the largest area of any rectangle you can make using only red and green tiles?
    val outlineTilePosition = (redTilePositions + outerGreenTiles)
    val (byX, byY) = measureTimedValue {
      outlineTilePosition.groupBy { it.x }.mapValues { (_, coords) ->
        coords.map { it.y }.sorted()
      } to
          outlineTilePosition.groupBy { it.y }.mapValues { (_, coords) ->
            coords.map { it.x }.sorted()
          }
    }.let {
      println("$stage outline/coords took ${it.duration}")
      it.value
    }

    // Neue Funktion die die vorberechneten Maps nutzt
    val cachedOutline = outlineTilePosition.toSet()
    fun checkRectangle(rectangle: Rectangle): Boolean =
      rectangle.outlinePositions().parallelStream().allMatch { candidate ->
        cachedOutline.contains(candidate) || run {
          val xCoords = byY[candidate.y]
          val yCoords = byX[candidate.x]

          xCoords != null && xCoords.size >= 2 &&
              xCoords.first() < candidate.x && xCoords.last() > candidate.x &&
              yCoords != null && yCoords.size >= 2 &&
              yCoords.first() < candidate.y && yCoords.last() > candidate.y
        }
      }

    val partOne = measureTimedValue { rectangles.maxOfOrNull { it.areaSize() } ?: 0 }.let {
      println("$stage part 1 took ${it.duration}")
      it.value
    }
    val partTwo = measureTimedValue {
      rectangles.sortedByDescending { it.areaSize() }
        .parallelStream()
        .filter { checkRectangle(it) }
        .findFirst()
        .get().areaSize()
    }.let {
      println("$stage part 2 took ${it.duration}")
      it.value
    }

    return PuzzleSolution(partOne, partTwo)
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
}

suspend fun main() {
  Day09.run()
}
