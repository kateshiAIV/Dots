package com.example.dots.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dots.databinding.ActivityMainBinding
import com.example.dots.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnPlayOffline.setOnClickListener {
            viewModel.onPlayOfflineClicked()
        }
    }

    private fun setupObservers() {
        viewModel.navigateToGame.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startGame()
                viewModel.onNavigationComplete()
            }
        }
    }

    private fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }
}