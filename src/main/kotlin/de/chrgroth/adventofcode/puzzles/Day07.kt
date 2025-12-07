package de.chrgroth.adventofcode.puzzles

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

    val (beamEndPositions, numberOfSplits) = (startPosition.y until topology.rows)
      .fold(listOf(startPosition) to 0.toLong()) { (beamPositions, numberOfSplits), rowIndex ->

        if (stage == Stage.TEST) {
          topology.dump(
            specialMappings = beamPositions.associateWith { "|" }.minus(startPosition).plus(startPosition to "S")
          )
        }

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
      }

    return PuzzleSolution(
      numberOfSplits, null
    )
  }
}

suspend fun main() {
  Day07.run()
}
