package com.yourcompany.android.tictactoe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.android.tictactoe.domain.model.GameState
import com.yourcompany.android.tictactoe.viewmodel.TicTacToeViewModel

@Composable
fun HostingScreen(
  viewModel: TicTacToeViewModel
) {
  val state: GameState by viewModel.state.observeAsState(GameState.Uninitialized)

  BackHandler(onBack = {
    viewModel.goToHome()
  })

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    WaitingScreen(
      title = "Hosting...",
      status = if (state.opponents.isEmpty()) "No opponents yet." else "Connected opponents: ${state.opponents.size}",
      onStopClick = { viewModel.goToHome() }
    )
    if (state.opponents.isNotEmpty()) {
      Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { viewModel.hostNewGame() }
      ) {
        Text(text = "Start!")
      }
    }
  }
}

@Composable
fun DiscoveringScreen(
  viewModel: TicTacToeViewModel
) {
  val state: GameState by viewModel.state.observeAsState(GameState.Uninitialized)

  BackHandler(onBack = {
    viewModel.goToHome()
  })

  WaitingScreen(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    title = if (state.opponents.isEmpty()) "Discovering..." else "Connected to host",
    status = if (state.opponents.isEmpty()) "" else "Waiting for host to start the game...",
    onStopClick = { viewModel.goToHome() }
  )
}

@Composable
fun WaitingScreen(
  modifier: Modifier = Modifier,
  title: String,
  status: String,
  onStopClick: () -> Unit
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(text = title)
    Text(text = status)
    CircularProgressIndicator(
      modifier = Modifier
        .padding(16.dp)
        .size(80.dp)
    )
    Button(
      modifier = Modifier.fillMaxWidth(),
      onClick = onStopClick
    ) {
      Text(text = "Stop")
    }
  }
}

@Preview
@Composable
fun WaitingScreenPreview() {
  WaitingScreen(title = "Waiting...", status = "This is a status", onStopClick = {})
}