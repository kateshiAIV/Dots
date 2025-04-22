package com.example.dots.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _navigateToGame = MutableLiveData<Boolean>()
    val navigateToGame: LiveData<Boolean> = _navigateToGame

    fun onPlayOfflineClicked() {
        _navigateToGame.value = true
    }

    fun onNavigationComplete() {
        _navigateToGame.value = false
    }
}