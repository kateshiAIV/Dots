package com.example.dots

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dots.GameStatus.*
import com.example.dots.databinding.ActivityGameBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GameActivity : AppCompatActivity(),View.OnClickListener{

    lateinit var binding: ActivityGameBinding


    private var gameModel : GameModel? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)


        GameData.fetchGameModel()

//        binding.btn0.setOnClickListener(this)
//        binding.btn1.setOnClickListener(this)
//        binding.btn2.setOnClickListener(this)
//        binding.btn3.setOnClickListener(this)
//        binding.btn4.setOnClickListener(this)
//        binding.btn5.setOnClickListener(this)
//        binding.btn6.setOnClickListener(this)
//        binding.btn7.setOnClickListener(this)
//        binding.btn8.setOnClickListener(this)

        binding.startGameBtn.setOnClickListener{
            Toast.makeText(applicationContext, "StartClicked", Toast.LENGTH_SHORT).show()
            startGame()

        }


        binding.dotsGridView.onDotClickListener = { x, y ->


            gameModel?.apply {
                if(gameStatus!= INPROGRESS){
                    Toast.makeText(applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                    return@apply
                }

                if(gameId!="-1" && currentPlayer != GameData.myID  ){
                    Toast.makeText(applicationContext, "Not your turn", Toast.LENGTH_SHORT).show()
                    return@apply
                }
                val alreadyFilled = filledPos.any { it.x == x && it.y == y}
                if (alreadyFilled) {
                    Toast.makeText(applicationContext, "This cell is already filled", Toast.LENGTH_SHORT).show()
                    return@apply
                }





                // Добавляем точку от текущего игрока
                filledPos.add(Dot(x, y, currentPlayer))
                currentPlayer = if(currentPlayer == "RED") "GREEN" else "RED"

                checkCapturedDots(this)

                // Обновляем dotsGridView
                binding.dotsGridView.setDots(filledPos)

                checkForWinner()
                updateGameData(this)



            }



        }



        GameData.gameModel.observe(this){
            gameModel = it
            setUI()
            binding.dotsGridView.setDots(it.filledPos)
        }

    }


    fun checkCapturedDots(model: GameModel) {
        val currentColor = if (model.currentPlayer == "GREEN") "RED" else "GREEN"
        val enemyDots = model.filledPos.filter { it.color != currentColor }
        val allDots = model.filledPos.toSet()

        val visited = mutableSetOf<Dot>()
        val capturedDots = mutableListOf<Dot>()

        for (dot in enemyDots) {
            if (dot in visited) continue

            val region = mutableSetOf<Dot>()
            val isOpen = floodFill(dot, enemyDots.toSet(), allDots, visited, region)

            if (!isOpen) {
                capturedDots.addAll(region)
            }
        }

        for (dot in model.filledPos){
            if(capturedDots.contains(dot)){
                dot.color = if(dot.color == "RED") "GREEN" else "RED"
            }

        }

        if (currentColor == "RED") {
            model.RedPoints += capturedDots.size
        } else {
            model.GreenPoints += capturedDots.size
        }
    }



    private fun floodFill(
        start: Dot,
        enemyDots: Set<Dot>,
        allDots: Set<Dot>,
        visited: MutableSet<Dot>,
        region: MutableSet<Dot>
    ): Boolean {
        val directions = listOf(
            Pair(0, -1), Pair(1, 0), Pair(0, 1), Pair(-1, 0)
        )

        val stack = ArrayDeque<Dot>()
        stack.add(start)

        var touchesEmptySpace = false

        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            if (current in visited) continue

            visited.add(current)
            region.add(current)

            for ((dx, dy) in directions) {
                val nx = current.x + dx
                val ny = current.y + dy
                val neighbor = Dot(nx, ny, "")

                val filledNeighbor = allDots.find { it.x == nx && it.y == ny }

                if (filledNeighbor == null) {
                    // Пустая клетка — значит, группа может "вырваться"
                    touchesEmptySpace = true
                } else if (enemyDots.contains(filledNeighbor) && filledNeighbor !in visited) {
                    stack.add(filledNeighbor)
                }
            }
        }

        return touchesEmptySpace
    }


    fun setUI(){
        gameModel?.apply {

            binding.startGameBtn.visibility = View.VISIBLE


            binding.gameStatusText.text =
                when(gameStatus){
                    GameStatus.CREATED -> {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        "Game ID: " + gameId
                    }
                    GameStatus.JOINED -> {
                        "Click on start game"
                    }
                    GameStatus.INPROGRESS ->{
                        binding.startGameBtn.visibility = View.INVISIBLE
                        binding.redScore.text = RedPoints.toString()
                        binding.greenScore.text = GreenPoints.toString()
                        when(GameData.myID){
                            currentPlayer -> "Your turn"
                            else -> currentPlayer + " turn"
                        }


                    }
                    GameStatus.FINISHED ->{
                        if(winner.isNotEmpty()) {
                            when(GameData.myID){
                                winner -> "You won"
                                else -> winner + " won"
                            }
                        }
                        else "draw"
                    }
                }
        }
    }

    override fun onClick(v: View?) {

    }

    fun startGame(){
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus =  GameStatus.INPROGRESS
                )
            )
        }
    }

    fun updateGameData(model: GameModel){
        GameData.saveGameModel(model)
    }


        fun isSurrounded(dot: Dot, allDots: Set<Dot>, byColor: String): Boolean {
        val directions = listOf(
            Pair(0, -1), // вверх
            Pair(1, 0),  // вправо
            Pair(0, 1),  // вниз
            Pair(-1, 0)  // влево
        )

        for ((dx, dy) in directions) {
            val neighbor = Dot(dot.x + dx, dot.y + dy, "")
            val found = allDots.find { it.x == neighbor.x && it.y == neighbor.y }
            if (found == null || found.color != byColor) {
                return false // есть свобода или точка союзника
            }
        }

        return true // со всех сторон окружён
    }



    private fun dfs(dot: Dot, group: List<Dot>, visited: MutableSet<Dot>, region: MutableSet<Dot>) {
        val stack = ArrayDeque<Dot>()
        stack.add(dot)

        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            if (current in visited) continue
            visited.add(current)
            region.add(current)

            for (neighbor in group) {
                if (neighbor !in visited && isAdjacentWithDiagonals(current, neighbor)) {
                    stack.add(neighbor)
                }
            }
        }
    }

    private fun isAdjacentWithDiagonals(a: Dot, b: Dot): Boolean {
        val dx = Math.abs(a.x - b.x)
        val dy = Math.abs(a.y - b.y)
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0)
    }

    private fun isEnclosed(region: Set<Dot>, borderDots: List<Dot>): Boolean {
        val directions = listOf(
            Pair(0, -1), Pair(1, 0), Pair(0, 1), Pair(-1, 0)
        )

        for (dot in region) {
            for ((dx, dy) in directions) {
                val nx = dot.x + dx
                val ny = dot.y + dy

                // Соседняя точка не входит в регион
                if (region.none { it.x == nx && it.y == ny }) {
                    // Сосед должен быть в borderDots — то есть быть точкой другого цвета
                    val borderFound = borderDots.any { it.x == nx && it.y == ny }
                    if (!borderFound) {
                        return false // окружение нарушено (сосед — не враг)
                    }
                }
            }
        }

        return true // все соседи — вражеские точки
    }

    fun checkForWinner(){

        gameModel?.apply {
            if (stepsLeft==0){
                gameStatus = GameStatus.FINISHED
                if (GreenPoints > RedPoints) winner = "GREEN"
                else if (GreenPoints < RedPoints ) winner = "RED"
            }
            stepsLeft -= 1
            updateGameData(this)

        }
    }

}

