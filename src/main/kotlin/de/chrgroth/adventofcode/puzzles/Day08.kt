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

    // the Elves would like to focus on connecting pairs of junction boxes that are as close together as possible
    // By connecting these two junction boxes together they become part of the same circuit.
    // connect together pairs of junction boxes which are closest together.
    // Afterward, what do you get if you multiply together the sizes of the three largest circuits?

    val numberOfConnections = if (stage == Stage.TEST) NUMBER_OF_CONNECTIONS_TEST else NUMBER_OF_CONNECTIONS_PROD
    val allDistances = computePairDistances(junctionBoxPositions)
    val closestPairs = findClosestPairs(numberOfConnections, allDistances)
    val initialCircuits = junctionBoxPositions.map { Circuit(junctionBoxes = listOf(it)) }
    val sortedCircuits = closestPairs.fold(initialCircuits) { result, connection ->
      val circuitOne = result.first { it.junctionBoxes.contains(connection.first) }
      val circuitTwo = result.first { it.junctionBoxes.contains(connection.second) }

      if (circuitOne != circuitTwo) {
        val mergedCircuit = circuitOne + circuitTwo
        result - circuitOne - circuitTwo + mergedCircuit
      } else {
        result
      }
    }.sortedBy { it.junctionBoxes.size }.reversed()

    return PuzzleSolution(
      sortedCircuits.take(3).fold(1.toLong()) { product, circuit ->
        product * circuit.junctionBoxes.size.toLong()
      },
      null
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

  private fun findClosestPairs(x: Int, sortedDistances: List<Coordinate3DDistance>): List<Pair<Coordinate3D, Coordinate3D>> {
    val result = mutableListOf<Pair<Coordinate3D, Coordinate3D>>()
    for (distance in sortedDistances) {
      if (result.size >= x) {
        break
      }

      val newConnection = Pair(distance.from, distance.to)
      if (!result.contains(newConnection) && !result.contains(Pair(distance.to, distance.from))) {
        result.add(newConnection)
      }
    }

    return result
  }
}

suspend fun main() {
  Day08.run()
}
