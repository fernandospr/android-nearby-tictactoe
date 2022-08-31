package com.yourcompany.android.tictactoe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
  var expanded by remember { mutableStateOf(false) }
  val suggestions = listOf("3x3", "4x4", "5x5", "6x6")
  Column(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Button(
      modifier = Modifier.fillMaxWidth(),
      onClick = { expanded = !expanded }
    ) {
      Text("Host")
      Icon(
        imageVector = Icons.Filled.ArrowDropDown,
        contentDescription = null
      )
    }
    DropdownMenu(
      modifier = Modifier.fillMaxWidth(),
      expanded = expanded,
      onDismissRequest = { expanded = false }
    ) {
      suggestions.forEachIndexed { index, label ->
        DropdownMenuItem(
          onClick = {
            expanded = false
            onHostClick(index + 3)
          },
          text = {
            Text(text = label)
          }
        )
      }
    }
    Button(
      modifier = Modifier.fillMaxWidth(),
      onClick = onDiscoverClick
    ) {
      Text(text = "Discover")
    }
  }
}

@Preview
@Composable
fun HomeScreenPreview(
) {
  HomeScreen(onHostClick = {}, onDiscoverClick = {})
}