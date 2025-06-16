package com.example.dots

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dots.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playOfflineBtn.setOnClickListener{
            createOfflineGame()
        }

        binding.createOnlineGameBtn.setOnClickListener{
            createOnlineGame()
        }
        binding.joinOnlineGameBtn.setOnClickListener{
            joinOnlineGame()
        }

    }

    fun createOfflineGame() {
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }


    fun createOnlineGame() {
        GameData.myID = "RED"
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.CREATED,
                gameId = Random.nextInt(0 .. 99999).toString()
            )
        )
        startGame()
    }

    fun joinOnlineGame() {

        var gameId = binding.gameIdInput.text.toString()
        if(gameId.isEmpty()){
            binding.gameIdInput.setError("Please enter game ID")
            return
        }

        GameData.myID = "GREEN"
        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model = it?.toObject(GameModel::class.java)
                if(model == null){
                    binding.gameIdInput.setError("Please enter valid game ID")
                }else{
                    model.gameStatus = GameStatus.JOINED
                    GameData.saveGameModel(model)
                    startGame()
                }
            }

    }


    fun startGame(){
        startActivity(Intent(this,GameActivity::class.java))
    }




}