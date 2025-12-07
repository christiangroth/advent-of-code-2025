package de.chrgroth.adventofcode.puzzles

import de.chrgroth.adventofcode.puzzles.utils.Coordinate
import de.chrgroth.adventofcode.puzzles.utils.Topology
import de.chrgroth.adventofcode.puzzles.utils.Vector
import de.chrgroth.adventofcode.puzzles.utils.findCoordinates
import de.chrgroth.adventofcode.puzzles.utils.skipBlank

data object Day07 : Puzzle {

  const val CHAR_OBSTACLE = '^'
  const val CHAR_START_POSITION = 'S'

  override suspend fun solve(stage: Stage, input: List<String>): PuzzleSolution {

    // You quickly locate a diagram of the tachyon manifold (your puzzle input).
    // A tachyon beam enters the manifold at the location marked S;
    // tachyon beams always move downward.
    // Tachyon beams pass freely through empty space (.).
    // if a tachyon beam encounters a splitter (^) a new tachyon beam continues from the immediate left and from the immediate right of the splitter.

    val (topology, startPosition) = input.skipBlank().let { inputLines ->
      Topology<Unit>(
        rows = inputLines.size,
        columns = inputLines.maxOfOrNull { it.length } ?: 0,
        obstaclePositions = inputLines.findCoordinates(CHAR_OBSTACLE)
      ) to inputLines.findCoordinates(CHAR_START_POSITION).first()
    }

    // To repair the teleporter, you first need to understand the beam-splitting properties of the tachyon manifold.

    val numberOfSplits = (startPosition.y until topology.rows)
      .fold(listOf(startPosition) to 0.toLong()) { (beamPositions, numberOfSplits), rowIndex ->

        val obstaclesInRow = topology.obstaclePositions.filter { it.y == rowIndex + 1 }
        if (obstaclesInRow.isEmpty()) {
          beamPositions.map { it.plus(Vector.DOWN) } to numberOfSplits
        } else {
          val nextLinePositions = beamPositions.flatMap { beamPosition ->
            if (obstaclesInRow.any { it.x == beamPosition.x }) {
              listOf(beamPosition.plus(Vector.LEFT).plus(Vector.DOWN), beamPosition.plus(Vector.RIGHT).plus(Vector.DOWN))
            } else {
              listOf(beamPosition.plus(Vector.DOWN))
            }
          }
          val splitsHappened = nextLinePositions.size - beamPositions.size

          nextLinePositions.distinct() to numberOfSplits + splitsHappened
        }
      }.second

    // With a quantum tachyon manifold, only a single tachyon particle is sent through the manifold.
    // A tachyon particle takes both the left and right path of each splitter encountered.
    // Since this is impossible, the manual recommends the many-worlds interpretation of quantum tachyon splitting:
    // each time a particle reaches a splitter, it's actually time itself which splits.
    // In one timeline, the particle went left, and in the other timeline, the particle went right.
    // To fix the manifold, what you really need to know is the number of timelines active after a single particle completes all of its possible journeys through the manifold.

    val beamEndPositions = findEndPositions(topology, startPosition)

    return PuzzleSolution(
      numberOfSplits, beamEndPositions
    )
  }

  private fun findEndPositions(topology: Topology<Unit>, beamPosition: Coordinate): Long =
    if (topology.rows < 1) {
      1
    } else {
      val nextTopology = topology.copy(
        rows = topology.rows.dec(),
        obstaclePositions = topology.obstaclePositions.filter { it.y != 0.toLong() },
        pointsOfInterest = topology.pointsOfInterest.filter { it.first.y != 0.toLong() },
      )

      if (topology.obstaclePositions.contains(beamPosition)) {
        val solutionsLeftPath = findEndPositions(nextTopology, beamPosition.plus(Vector.DOWN).plus(Vector.LEFT))
        val solutionsRightPath = findEndPositions(nextTopology, beamPosition.plus(Vector.DOWN).plus(Vector.RIGHT))
        solutionsLeftPath + solutionsRightPath
      } else {
        findEndPositions(nextTopology, beamPosition.plus(Vector.DOWN))
      }
    }
}

suspend fun main() {
  Day07.run()
}
