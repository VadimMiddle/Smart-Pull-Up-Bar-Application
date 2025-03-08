package com.example.smartpull_upbar.presentation.workout_screen.data_classes

data class ExerciseCategory(val name: String, val exerciseItems: List<ExerciseData> )

var standardCategory = ExerciseCategory(
        name = "Стандартные",
        exerciseItems = listOf(standardPullUp, reverseGripPullUp)
)

var assistedCategory = ExerciseCategory(
        name = "С поддержкой",
        exerciseItems = listOf(assistedStandardPullUp, assistedReversePullUp)
)

var myCategories = listOf<ExerciseCategory>(standardCategory, assistedCategory)