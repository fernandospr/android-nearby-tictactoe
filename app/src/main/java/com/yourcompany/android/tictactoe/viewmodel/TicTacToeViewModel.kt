package com.yourcompany.android.tictactoe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.yourcompany.android.tictactoe.BuildConfig
import com.yourcompany.android.tictactoe.domain.model.GameState
import com.yourcompany.android.tictactoe.domain.model.TicTacToe
import com.yourcompany.android.tictactoe.routing.Screen
import com.yourcompany.android.tictactoe.routing.TicTacToeRouter
import java.util.*
import kotlin.text.Charsets.UTF_8

class TicTacToeViewModel(private val connectionsClient: ConnectionsClient) : ViewModel() {
  private val localUsername = UUID.randomUUID().toString()

  private var isHost = false
  private var boardSize = 0

  private lateinit var game: TicTacToe

  private val _state = MutableLiveData(GameState.Uninitialized)
  val state: LiveData<GameState> = _state

  private fun getOpponents() = _state.value?.opponents.orEmpty()

  private val payloadCallback = object : PayloadCallback() {
    override fun onPayloadReceived(endpointId: String, payload: Payload) {
      Log.d(TAG, "onPayloadReceived")
      // This always gets the full data of the payload. Is null if it's not a BYTES payload.
      if (payload.type == Payload.Type.BYTES) {
        when {
          payload.isNewGame() -> {
            val newGame = payload.toNewGame()
            Log.d(TAG, "Received $newGame from $endpointId")
            newGame(newGame.assignedPlayerNumber, newGame.boardSize, newGame.players)
          }
          payload.isPlay() -> {
            val play = payload.toPlay()
            Log.d(TAG, "Received $play from $endpointId")
            play(play.player, play.position)
            if (isHost) {
              getOpponents().filter { it != endpointId }.forEach {
                sendPlay(it, play.player, play.position)
              }
            }
          }
        }
      }
    }

    override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
      // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
      // after the call to onPayloadReceived().
      Log.d(TAG, "onPayloadTransferUpdate")
    }
  }

  private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
    override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
      Log.d(TAG, "onEndpointFound")

      Log.d(TAG, "Requesting connection...")
      connectionsClient.requestConnection(
        localUsername,
        endpointId,
        connectionLifecycleCallback
      ).addOnSuccessListener {
        // Successfully requested a connection. Now both sides
        // must accept before the connection is established.
        Log.d(TAG, "Successfully requested a connection")
      }.addOnFailureListener {
        // Failed to request the connection.
        Log.d(TAG, "Failed to request the connection")
      }
    }

    override fun onEndpointLost(endpointId: String) {
      // A previously discovered endpoint has gone away.
      Log.d(TAG, "onEndpointLost")
    }
  }

  private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
      Log.d(TAG, "onConnectionInitiated")

      // Instead of accepting connection, you can show an AlertDialog on both devices so both have to accept
      // See: https://developers.google.com/nearby/connections/android/manage-connections#authenticate_a_connection
      Log.d(TAG, "Accepting connection...")
      connectionsClient.acceptConnection(endpointId, payloadCallback)
    }

    override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
      Log.d(TAG, "onConnectionResult")

      when (resolution.status.statusCode) {
        ConnectionsStatusCodes.STATUS_OK -> {
          // Connected! Can now start sending and receiving data.
          Log.d(TAG, "ConnectionsStatusCodes.STATUS_OK")

          connectionsClient.stopDiscovery()
          val opponents = getOpponents() + endpointId
          _state.value = GameState.Uninitialized.copy(opponents = opponents)
          Log.d(TAG, "Added opponentEndpointId: $endpointId")
        }
        ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
          // The connection was rejected by one or both sides.
          Log.d(TAG, "ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED")
        }
        ConnectionsStatusCodes.STATUS_ERROR -> {
          // The connection broke before it was able to be accepted.
          Log.d(TAG, "ConnectionsStatusCodes.STATUS_ERROR")
        }
        else -> {
          // Unknown status code
          Log.d(TAG, "Unknown status code ${resolution.status.statusCode}")
        }
      }
    }

    override fun onDisconnected(endpointId: String) {
      // Disconnected from this endpoint. No more data can be
      // sent or received.
      Log.d(TAG, "onDisconnected")
      goToHome()
    }
  }

  fun startHosting(boardSize: Int) {
    Log.d(TAG, "Start advertising...")
    TicTacToeRouter.navigateTo(Screen.Hosting)
    val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()

    connectionsClient.startAdvertising(
      localUsername,
      BuildConfig.APPLICATION_ID,
      connectionLifecycleCallback,
      advertisingOptions
    ).addOnSuccessListener {
      // Advertising...
      Log.d(TAG, "Advertising...")
      this.isHost = true
      this.boardSize = boardSize
    }.addOnFailureListener {
      // Unable to start advertising
      Log.d(TAG, "Unable to start advertising")
      TicTacToeRouter.navigateTo(Screen.Home)
    }
  }

  fun startDiscovering() {
    Log.d(TAG, "Start discovering...")
    TicTacToeRouter.navigateTo(Screen.Discovering)
    val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()

    connectionsClient.startDiscovery(
      BuildConfig.APPLICATION_ID,
      endpointDiscoveryCallback,
      discoveryOptions
    ).addOnSuccessListener {
      // Discovering...
      Log.d(TAG, "Discovering...")
    }.addOnFailureListener {
      // Unable to start discovering
      Log.d(TAG, "Unable to start discovering")
      TicTacToeRouter.navigateTo(Screen.Home)
    }
  }

  fun play(position: Pair<Int, Int>) {
    val localPlayer = _state.value?.localPlayer
    if (game.playerTurn != localPlayer) return
    if (game.isPlayedBucket(position)) return

    play(localPlayer, position)
    getOpponents().forEach {
      sendPlay(it, localPlayer, position)
    }
  }

  private fun sendPlay(opponentEndpointId: String, player: Int, position: Pair<Int, Int>) {
    val play = Play(player, position)
    Log.d(TAG, "Sending $play to $opponentEndpointId")
    connectionsClient.sendPayload(
      opponentEndpointId,
      play.toPayload()
    )
  }

  private fun play(player: Int, position: Pair<Int, Int>) {
    Log.d(TAG, "Player $player played [${position.first},${position.second}]")

    game.play(player, position)
    _state.value = _state.value?.copy(
      playerTurn = game.playerTurn,
      playerWon = game.playerWon,
      isOver = game.isOver
    )
  }

  fun hostNewGame() {
    connectionsClient.stopAdvertising()

    val opponents = getOpponents()
    val players = (1..opponents.size + 1).shuffled()
    val localPlayer = players[0]
    opponents.forEachIndexed { index, opponent ->
      val assignedPlayerNumber = players[index + 1]
      sendNewGame(
        opponent,
        boardSize,
        players.size,
        assignedPlayerNumber
      )
    }

    newGame(localPlayer, boardSize, players.size)
  }

  private fun sendNewGame(
    opponentEndpointId: String,
    boardSize: Int,
    players: Int,
    assignedPlayerNumber: Int
  ) {
    val newGame = NewGame(boardSize, players, assignedPlayerNumber)
    Log.d(TAG, "Sending $newGame to $opponentEndpointId")
    connectionsClient.sendPayload(
      opponentEndpointId,
      newGame.toPayload()
    )
  }

  private fun newGame(localPlayer: Int, boardSize: Int, players: Int) {
    Log.d(TAG, "Starting new game")
    game = TicTacToe(boardSize = boardSize, players = players)
    _state.value = _state.value?.copy(
      localPlayer = localPlayer,
      playerTurn = game.playerTurn,
      playerWon = game.playerWon,
      isOver = game.isOver,
      board = game.board
    )
    TicTacToeRouter.navigateTo(Screen.Game)
  }

  fun nextGame() {
    if (isHost) {
      hostNewGame()
    } else {
      TicTacToeRouter.navigateTo(Screen.Discovering)
    }
  }

  fun goToHome() {
    stopClient()
    TicTacToeRouter.navigateTo(Screen.Home)
  }

  private fun stopClient() {
    Log.d(TAG, "Stop advertising, discovering, all endpoints")
    connectionsClient.stopAdvertising()
    connectionsClient.stopDiscovery()
    connectionsClient.stopAllEndpoints()

    isHost = false
    boardSize = 0
    _state.value = GameState.Uninitialized
  }

  override fun onCleared() {
    stopClient()
    super.onCleared()
  }

  private companion object {
    const val TAG = "TicTacToeVM"
    val STRATEGY = Strategy.P2P_STAR
  }
}

data class Play(val player: Int, val position: Pair<Int, Int>)

fun Payload.isPlay() = String(asBytes()!!, UTF_8).startsWith("PLAY")

fun Play.toPayload() =
  Payload.fromBytes("PLAY($player,${position.first},${position.second})".toByteArray(UTF_8))

fun Payload.toPlay(): Play {
  val playStr = String(asBytes()!!, UTF_8)
  val (player, posX, posY) = playStr.removeSurrounding("PLAY(", ")").split(",")
  return Play(player.toInt(), posX.toInt() to posY.toInt())
}


data class NewGame(val boardSize: Int, val players: Int, val assignedPlayerNumber: Int)

fun Payload.isNewGame() = String(asBytes()!!, UTF_8).startsWith("NEWGAME")

fun NewGame.toPayload() =
  Payload.fromBytes("NEWGAME($boardSize,$players,$assignedPlayerNumber)".toByteArray(UTF_8))

fun Payload.toNewGame(): NewGame {
  val newGameStr = String(asBytes()!!, UTF_8)
  val (boardSize, players, assignedPlayerNumber) = newGameStr.removeSurrounding("NEWGAME(", ")")
    .split(",")
  return NewGame(boardSize.toInt(), players.toInt(), assignedPlayerNumber.toInt())
}