package com.yourcompany.android.tictactoe.domain.model

data class GameState(
  val localPlayer: Int,
  val playerTurn: Int,
  val playerWon: Int,
  val isOver: Boolean,
  val board: Array<Array<Int>>,
  val opponents: List<String>
) {
  companion object {
    val Uninitialized = GameState(0, 0, 0, false, emptyArray(), emptyList())
  }
}