package com.yourcompany.android.tictactoe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yourcompany.android.tictactoe.viewmodel.TicTacToeViewModel

@Composable
fun HomeScreen(viewModel: TicTacToeViewModel) {
  HomeScreen(
    onHostClick = { boardSize -> viewModel.startHosting(boardSize) },
    onDiscoverClick = { viewModel.startDiscovering() }
  )
}

@Composable
fun HomeScreen(
  onHostClick: (boardSize: Int) -> Unit,
  onDiscoverClick: () -> Unit
) {
  var openDialog by remember { mutableStateOf(false) }
  Column(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Button(
      modifier = Modifier.fillMaxWidth(),
      onClick = { openDialog = true }
    ) {
      Text("Host")
    }
    if (openDialog) {
      BoardSizeChooserDialog(
        onBoardSizeClick = onHostClick,
        onDismiss = { openDialog = false }
      )
    }
    Button(
      modifier = Modifier.fillMaxWidth(),
      onClick = onDiscoverClick
    ) {
      Text(text = "Discover")
    }
  }
}

@Composable
fun BoardSizeChooserDialog(
  onBoardSizeClick: (boardSize: Int) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss
  ) {
    Card(
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          modifier = Modifier.padding(bottom = 16.dp),
          text = "Choose board size",
          textAlign = TextAlign.Center
        )

        val radioOptions = listOf("3x3", "4x4", "5x5", "6x6")
        radioOptions.forEach { text ->
          Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
              onBoardSizeClick(radioOptions.indexOf(text) + 3)
              onDismiss()
            }
          ) {
            Text(text = text)
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun HomeScreenPreview(
) {
  HomeScreen(onHostClick = {}, onDiscoverClick = {})
}

@Preview
@Composable
fun BoardSizeChooserDialogPreview(
) {
  BoardSizeChooserDialog(onBoardSizeClick = {}, onDismiss = {})
}