package com.yourcompany.android.tictactoe

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.yourcompany.android.tictactoe.routing.Screen
import com.yourcompany.android.tictactoe.routing.TicTacToeRouter
import com.yourcompany.android.tictactoe.ui.screens.DiscoveringScreen
import com.yourcompany.android.tictactoe.ui.screens.GameScreen
import com.yourcompany.android.tictactoe.ui.screens.HomeScreen
import com.yourcompany.android.tictactoe.ui.screens.HostingScreen
import com.yourcompany.android.tictactoe.ui.theme.TicTacToeTheme
import com.yourcompany.android.tictactoe.viewmodel.TicTacToeViewModel
import com.yourcompany.android.tictactoe.viewmodel.TicTacToeViewModelFactory

class MainActivity : ComponentActivity() {

  private val viewModel: TicTacToeViewModel by viewModels {
    TicTacToeViewModelFactory()
  }

  private val requestMultiplePermissions = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    if (permissions.entries.any { !it.value }) {
      Toast.makeText(this, "Required permissions needed", Toast.LENGTH_LONG).show()
      finish()
    } else {
      recreate()
    }
  }

  override fun onStart() {
    super.onStart()
    if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
      requestMultiplePermissions.launch(
        REQUIRED_PERMISSIONS
      )
    }
  }

  private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
    return permissions.isEmpty() || permissions.all {
      ContextCompat.checkSelfPermission(
        context,
        it
      ) == PackageManager.PERMISSION_GRANTED
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      TicTacToeTheme {
        MainActivityScreen()
      }
    }
  }

  @Composable
  private fun MainActivityScreen() {
    Surface {
      when (TicTacToeRouter.currentScreen) {
        is Screen.Home -> HomeScreen(viewModel)
        is Screen.Hosting -> HostingScreen(viewModel)
        is Screen.Discovering -> DiscoveringScreen(viewModel)
        is Screen.Game -> GameScreen(viewModel)
      }
    }
  }

  private companion object {
    val REQUIRED_PERMISSIONS = emptyArray<String>()
  }
}