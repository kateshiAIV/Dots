package com.example.dots

import kotlin.random.Random

data class Dot(val x: Int, val y: Int, val color: String)

enum class GameStatus{
    CREATED,
    JOINED,
    INPROGRESS,
    FINISHED
}

data class GameModel(
    var gameId: String = "-1",
    var filledPos: MutableList<Dot> = mutableListOf(),
    var winner: String = "",
    var gameStatus: GameStatus = GameStatus.CREATED,
    var currentPlayer: String = (arrayListOf("GREEN" , "RED"))[Random.nextInt(2)]
)


