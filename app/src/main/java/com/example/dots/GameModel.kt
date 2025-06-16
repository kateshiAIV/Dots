package com.example.dots

import kotlin.random.Random

data class Dot(
    var x: Int = 0,
    var y: Int = 0,
    var color: String = ""
) {
    constructor() : this(0, 0, "") // <- ОБЯЗАТЕЛЕН для Firebase
}


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
    var currentPlayer: String = (arrayListOf("RED" , "GREEN"))[Random.nextInt(2)],
    var stepsLeft: Int = 50,
    var GreenPoints: Int =0,
    var RedPoints: Int =0
)


