package com.example.dots.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dots.databinding.ActivityGameBinding
import com.example.dots.viewmodel.GameViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup game board and observers
        setupGame()
    }

    private fun setupGame() {
        // Initialize game board and set up touch listeners
        // Observe ViewModel for game state changes
    }
}