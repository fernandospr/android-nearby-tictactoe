package com.yourcompany.android.tictactoe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TicTacToeViewModelFactory() :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TicTacToeViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return TicTacToeViewModel() as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}