package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.Coordinate
import de.chrgroth.adventofcode.puzzles.utils.Topology
import de.chrgroth.adventofcode.puzzles.utils.Vector
import de.chrgroth.adventofcode.puzzles.utils.findCoordinates
import de.chrgroth.adventofcode.puzzles.utils.skipBlank

private const val CHAR_OBSTACLE = '@'

data object Day04 : Puzzle {

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // The rolls of paper (@) are arranged on a large grid;
    // the Elves even have a helpful diagram (your puzzle input) indicating where everything is located.

    val topology = input.skipBlank().let { inputLines ->
      Topology<Unit>(
        rows = inputLines.size,
        columns = inputLines.maxOfOrNull { it.length } ?: 0,
        obstaclePositions = inputLines.findCoordinates(CHAR_OBSTACLE)
      )
    }

    // The forklifts can only access a roll of paper if there are fewer than four rolls of paper in the eight adjacent positions.
    val paperRollsToPickUp = topology.findPaperRollsReadyToCollect()
    val allPaperRollsToPickUp = collectAllPaperRolls(topology, 0)

    return PuzzleSolution(
      paperRollsToPickUp.size, allPaperRollsToPickUp
    )
  }

  private tailrec fun collectAllPaperRolls(topology: Topology<*>, alreadyCollected: Int): Int {
    val collected = topology.findPaperRollsReadyToCollect()
    return if (collected.isEmpty()) {
      alreadyCollected
    } else {
      collectAllPaperRolls(topology.cleanupObstacles(collected), alreadyCollected + collected.size)
    }
  }

  private fun Topology<*>.findPaperRollsReadyToCollect(): List<Coordinate> =
    obstaclePositions.parallelStream().filter { paperRoll ->
      Vector.directions.map { paperRoll.plus(it) }.count { obstaclePositions.contains(it) } < 4
    }.toList()

  private fun <T> Topology<T>.cleanupObstacles(positions: List<Coordinate>): Topology<T> =
    copy(obstaclePositions = obstaclePositions - positions.toSet())
}

suspend fun main() {
  Day04.run()
}
