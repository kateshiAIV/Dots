package com.example.dots

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dots.databinding.ActivityGameBinding


class GameActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityGameBinding

    private var gameModel: GameModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameBtn.setOnClickListener(this)

        binding.dotsGrid.onDotClickListener = { x, y ->
            //Toast.makeText(this, "Clicked on dot at: ($x, $y)", Toast.LENGTH_SHORT).show()
            gameModel?.apply{
                if(gameStatus!= GameStatus.INPROGRESS){
                    Toast.makeText(applicationContext, "Game did not started", Toast.LENGTH_SHORT).show()
                }
                val clickedX = x
                val clickedY = y
                val alreadyFilled = filledPos.any { it.x == x && it.y == y }
                if (alreadyFilled) {
                    Toast.makeText(applicationContext, "This cell is already filled", Toast.LENGTH_SHORT).show()
                    return@apply
                }
                // Если свободно — добавляем точку
                filledPos.add(Dot(x, y, currentPlayer))

                // Переключение игрока
                currentPlayer = if (currentPlayer == "GREEN") "RED" else "GREEN"

                binding.dotsGrid.setDots(filledPos)
            }
        }

        GameData.GameModel.observe(this){
            gameModel = it
            setUI()
        }
    }
    fun setUI(){
        gameModel?.apply {

        }
    }

    fun startGame(){
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS,
                )
            )
        }
    }

    fun updateGameData(model : GameModel){
        GameData.saveGameModel(model)
    }



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.start_game_btn -> {
                binding.dotsGrid.visibility = View.VISIBLE
                binding.startGameBtn.visibility = View.GONE
                binding.gameTv.visibility = View.GONE
                binding.nameTv.visibility = View.GONE
                startGame()
            }
        }
    }

}

