package com.example.smartpull_upbar.presentation.workout_screen

import androidx.compose.ui.graphics.Color
import com.example.smartpull_upbar.ui.theme.myBlack
import com.example.smartpull_upbar.ui.theme.selectedColor

data class PagerTabs(var name: String, val selectedColor: Color,
                     var unselectedColor: Color, var index: Int)

var controlPanelTab = PagerTabs(
        "Панель управления",
        selectedColor = selectedColor,
        unselectedColor = myBlack,
        index = 0)

var currentWorkoutTab = PagerTabs(
        "Текущая тренировка",
        selectedColor = selectedColor,
        unselectedColor = myBlack,
        index = 1)

var pagerTabs = listOf(controlPanelTab, currentWorkoutTab)