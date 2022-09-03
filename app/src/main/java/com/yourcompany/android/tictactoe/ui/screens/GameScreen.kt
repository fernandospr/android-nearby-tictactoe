package com.yourcompany.android.tictactoe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.android.tictactoe.domain.model.GameState
import com.yourcompany.android.tictactoe.viewmodel.TicTacToeViewModel

@Composable
fun GameScreen(viewModel: TicTacToeViewModel) {
  val state: GameState by viewModel.state.observeAsState(GameState.Uninitialized)

  BackHandler(onBack = {
    viewModel.goToHome()
  })

  if (state.isOver) {
    GameOverScreen(
      playerWon = state.playerWon,
      localPlayer = state.localPlayer,
      onContinueClick = { viewModel.nextGame() }
    )
  } else {
    OngoingGameScreen(
      localPlayer = state.localPlayer,
      playerTurn = state.playerTurn,
      board = state.board,
      onBucketClick = { position -> viewModel.play(position) }
    )
  }
}

@Composable
fun GameOverScreen(
  playerWon: Int,
  localPlayer: Int,
  onContinueClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(text = "Game over")
    Text(
      text = if (playerWon > 0) {
        if (playerWon != localPlayer) "Player $playerWon won!" else "You won!"
      } else {
        "It's a tie!"
      },
      fontWeight = FontWeight.Bold
    )
    Button(
      modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 16.dp),
      onClick = onContinueClick
    ) {
      Text(text = "Continue")
    }
  }
}

@Composable
fun OngoingGameScreen(
  localPlayer: Int,
  playerTurn: Int,
  board: Array<Array<Int>>,
  onBucketClick: (position: Pair<Int, Int>) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(text = "You're player $localPlayer")
      Box(
        modifier = Modifier
          .padding(4.dp, 0.dp)
          .size(10.dp)
          .background(color = getPlayerColor(localPlayer))
      )
    }
    Text(
      text = if (localPlayer == playerTurn) "Your turn!" else "Waiting for player $playerTurn...",
      fontWeight = FontWeight.Bold
    )
    Board(
      board = board,
      onBucketClick = { position -> onBucketClick(position) }
    )
  }
}

@Composable
fun Board(
  board: Array<Array<Int>>,
  onBucketClick: (position: Pair<Int, Int>) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxSize()
  ) {
    for (i in board.indices) {
      Column(modifier = Modifier.weight(1f)) {
        for (j in board.indices) {
          Bucket(
            modifier = Modifier
              .fillMaxSize()
              .weight(1f),
            player = board[i][j],
            onClick = { onBucketClick(i to j) }
          )
        }
      }
    }
  }
}

@Composable
fun Bucket(
  modifier: Modifier,
  player: Int,
  onClick: () -> Unit
) {
  OutlinedButton(
    modifier = modifier,
    colors = ButtonDefaults.buttonColors(getPlayerColor(player)),
    onClick = onClick
  ) {}
}

private fun getPlayerColor(player: Int): Color {
  return when (player) {
    0 -> Color.White
    1 -> Color.Red
    2 -> Color.Green
    3 -> Color.Blue
    4 -> Color.Yellow
    else -> throw IllegalArgumentException("Missing color for player $player")
  }
}

@Preview
@Composable
fun GameOverOtherPlayerWonScreenPreview() {
  GameOverScreen(playerWon = 2, localPlayer = 1, onContinueClick = {})
}

@Preview
@Composable
fun GameOverCurrentPlayerWonScreenPreview() {
  GameOverScreen(playerWon = 1, localPlayer = 1, onContinueClick = {})
}

@Preview
@Composable
fun GameOverTieScreenPreview() {
  GameOverScreen(playerWon = 0, localPlayer = 1, onContinueClick = {})
}

@Preview
@Composable
fun OngoingGameScreenPreview() {
  OngoingGameScreen(
    localPlayer = 1,
    playerTurn = 2,
    board = arrayOf(arrayOf(0, 0, 0), arrayOf(0, 0, 0), arrayOf(0, 0, 0)),
    onBucketClick = {}
  )
}