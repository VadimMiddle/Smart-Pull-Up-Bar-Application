package com.example.smartpull_upbar.presentation

import android.media.Image

data class exercise(var name: String, var bestRepetition: String,
        var bestRepCount: Int, var image: Image)

var standardPullUp = exercise(
        name = "Подтягивание (обычный хват)",
        bestRepetition = "Лучшее повторение: ",
        bestRepCount = 0,
        image = Image(R)
)
