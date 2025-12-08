package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.Coordinate3D
import de.chrgroth.adventofcode.puzzles.utils.Coordinate3DDistance
import de.chrgroth.adventofcode.puzzles.utils.skipBlank

const val NUMBER_OF_CONNECTIONS_TEST = 10
const val NUMBER_OF_CONNECTIONS_PROD = 1000

data object Day08 : Puzzle {

  private data class Circuit(val junctionBoxes: List<Coordinate3D>) {

    operator fun plus(other: Circuit): Circuit =
      Circuit(junctionBoxes = junctionBoxes + other.junctionBoxes)

    override fun toString(): String {
      return "Circuit(size=${junctionBoxes.size}, junctionBoxes=$junctionBoxes)"
    }
  }

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // The Elves are trying to figure out which junction boxes to connect so that electricity can reach every junction box.
    // They even have a list of all of the junction boxes' positions in 3D space (your puzzle input).

    val junctionBoxPositions = input.skipBlank().map { line ->
      line.split(',').map { it.trim() }.map { it.toLong() }.let {
        check(it.size == 3) { "Need three values for 3D coordinate!" }
        Coordinate3D(it[0], it[1], it[2])
      }
    }

    val allDistancesSorted = computePairDistances(junctionBoxPositions).sortedBy { it.distance }
    val initialCircuits = junctionBoxPositions.map { Circuit(junctionBoxes = listOf(it)) }

    // the Elves would like to focus on connecting pairs of junction boxes that are as close together as possible
    // By connecting these two junction boxes together they become part of the same circuit.
    // connect together pairs of junction boxes which are closest together.
    // Afterward, what do you get if you multiply together the sizes of the three largest circuits?

    val closestPairs = findClosestPairs(
      limit = if (stage == Stage.TEST) NUMBER_OF_CONNECTIONS_TEST else NUMBER_OF_CONNECTIONS_PROD,
      sortedDistances = allDistancesSorted
    )
    val sortedCircuits = closestPairs.fold(initialCircuits) { result, connection ->
      result.merge(connection)
    }.sortedBy { it.junctionBoxes.size }.reversed()

    // Continue connecting the closest unconnected pairs of junction boxes together until they're all in the same circuit.
    // What do you get if you multiply together the X coordinates of the last two junction boxes you need to connect?

    var singleCircuitList = initialCircuits
    for (connection in allDistancesSorted) {
      singleCircuitList = singleCircuitList.merge(connection.from to connection.to)
      if (singleCircuitList.size == 1) {
        break
      }
    }
    val firstSingleCircuit = singleCircuitList.first()

    return PuzzleSolution(
      sortedCircuits.take(3).fold(1.toLong()) { product, circuit ->
        product * circuit.junctionBoxes.size.toLong()
      },
      firstSingleCircuit.junctionBoxes.takeLast(2).map { it.x }.reduce { a, b -> a * b }
    )
  }

  private fun computePairDistances(positions: List<Coordinate3D>): List<Coordinate3DDistance> {
    val pairs = ArrayList<Coordinate3DDistance>((positions.size * (positions.size - 1)) / 2)
    for (i in 0 until positions.size) {
      for (j in i + 1 until positions.size) {
        pairs.add(
          Coordinate3DDistance(
            positions[i],
            positions[j],
            positions[i].distanceToSuqared(positions[j]).toDouble()
          )
        )
      }
    }

    return pairs.sortedBy { it.distance }
  }

  private fun findClosestPairs(limit: Int, sortedDistances: List<Coordinate3DDistance>): List<Pair<Coordinate3D, Coordinate3D>> {
    val processedCache = HashSet<UnorderedPair>()
    val result = mutableListOf<Pair<Coordinate3D, Coordinate3D>>()

    for (distance in sortedDistances) {
      if (result.size >= limit) {
        break
      }

      val cacheKex = UnorderedPair(distance.from, distance.to)
      if (processedCache.add(cacheKex)) {
        result.add(Pair(distance.from, distance.to))
      }
    }

    return result
  }

  private fun List<Circuit>.merge(connection: Pair<Coordinate3D, Coordinate3D>): List<Circuit> {
    val circuitOne = first { it.junctionBoxes.contains(connection.first) }
    val circuitTwo = first { it.junctionBoxes.contains(connection.second) }

    return if (circuitOne != circuitTwo) {
      val mergedCircuit = circuitOne + circuitTwo
      this - circuitOne - circuitTwo + mergedCircuit.copy(
        // Reorder to have both junction boxes last. Important for part 2 solution.
        junctionBoxes = mergedCircuit.junctionBoxes - connection.first - connection.second + connection.first + connection.second
      )
    } else {
      this
    }
  }

  private data class UnorderedPair(val a: Coordinate3D, val b: Coordinate3D) {
    override fun equals(other: Any?): Boolean {
      if (other !is UnorderedPair) return false
      return (a == other.a && b == other.b) || (a == other.b && b == other.a)
    }

    override fun hashCode(): Int {
      // Symmetrischer Hash
      val h1 = a.hashCode()
      val h2 = b.hashCode()
      return if (h1 < h2) 31 * h1 + h2 else 31 * h2 + h1
    }
  }
}

suspend fun main() {
  Day08.run()
}
