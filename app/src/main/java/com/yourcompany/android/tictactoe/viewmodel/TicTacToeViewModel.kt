package com.yourcompany.android.tictactoe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yourcompany.android.tictactoe.domain.model.GameState
import com.yourcompany.android.tictactoe.domain.model.TicTacToe
import com.yourcompany.android.tictactoe.routing.Screen
import com.yourcompany.android.tictactoe.routing.TicTacToeRouter
import java.util.*

class TicTacToeViewModel() : ViewModel() {
  private val localUsername = UUID.randomUUID().toString()
  private var localPlayer: Int = 0
  private var opponentPlayer: Int = 0
  private var opponentEndpointId: String = ""

  private var game = TicTacToe()

  private val _state = MutableLiveData(GameState.Uninitialized)
  val state: LiveData<GameState> = _state

  fun startHosting() {
    TicTacToeRouter.navigateTo(Screen.Hosting)
  }

  fun startDiscovering() {
    TicTacToeRouter.navigateTo(Screen.Discovering)
  }

  fun newGame() {
    Log.d(TAG, "Starting new game")
    game = TicTacToe()
    _state.value = GameState(localPlayer, game.playerTurn, game.playerWon, game.isOver, game.board)
  }

  fun play(position: Pair<Int, Int>) {
    if (game.playerTurn != localPlayer) return
    if (game.isPlayedBucket(position)) return

    play(localPlayer, position)
    sendPosition(position)
  }

  private fun play(player: Int, position: Pair<Int, Int>) {
    Log.d(TAG, "Player $player played [${position.first},${position.second}]")

    game.play(player, position)
    _state.value = GameState(localPlayer, game.playerTurn, game.playerWon, game.isOver, game.board)
  }

  private fun sendPosition(position: Pair<Int, Int>) {
    Log.d(TAG, "Sending [${position.first},${position.second}] to $opponentEndpointId")
  }

  fun goToHome() {
    TicTacToeRouter.navigateTo(Screen.Home)
  }

  private companion object {
    const val TAG = "TicTacToeVM"
  }
}