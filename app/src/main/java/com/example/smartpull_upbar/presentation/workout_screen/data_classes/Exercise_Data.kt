package com.example.smartpull_upbar.presentation.workout_screen.data_classes

import com.example.smartpull_upbar.R

data class ExerciseData(var name: String, var bestRepetition: String,
                        var bestRepCount: Int, var image: Int)

var standardPullUp = ExerciseData(
        name = "Подтягивание (обычный хват)",
        bestRepetition = "Лучшее повторение: ",
        bestRepCount = 0,
        image = R.drawable.pullup )

var reverseGripPullUp = ExerciseData(
        name = "Подтягивание (обратный хват)",
        bestRepetition = "Лучшее повторение: ",
        bestRepCount = 0,
        image = R.drawable.reverse )

var assistedStandardPullUp = ExerciseData(
        name = "Подтягивание (обычный хват)",
        bestRepetition = "Лучшее повторение: ",
        bestRepCount = 0,
        image = R.drawable.assisted_standard )

var assistedReversePullUp = ExerciseData(
        name = "Подтягивание (обратный хват)",
        bestRepetition = "Лучшее повторение: ",
        bestRepCount = 0,
        image = R.drawable.assisted_reverse )

var exercises = listOf(standardPullUp, reverseGripPullUp, assistedStandardPullUp, assistedReversePullUp)

