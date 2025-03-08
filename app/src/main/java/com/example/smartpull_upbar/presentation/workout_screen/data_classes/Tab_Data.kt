package com.example.smartpull_upbar.presentation.workout_screen.data_classes

import com.example.smartpull_upbar.R
import com.example.smartpull_upbar.presentation.Screens

data class TabsInfo(var name: String, var outlined: Int, var filled: Int, var route: Screens, var index: Int)
var tab1 = TabsInfo("Workout", R.drawable.outlined_workout, R.drawable.filled_workout, Screens.WorkoutScreen, 0)
var tab2 = TabsInfo("History", R.drawable.outlined_history, R.drawable.filled_history, Screens.HistoryScreen, 1)
var tab3 = TabsInfo("Profile", R.drawable.outlined_profile, R.drawable.filled_profile, Screens.ProfileScreen, 2)

var tabs = listOf(tab1, tab2, tab3)

data class PagerTabsInfo(var name: String)
var tabControlPanel = PagerTabsInfo("Панель управления")
var tabCurrentWorkout = PagerTabsInfo("Текущая тренировка")

var pagerTabs = listOf(tabControlPanel, tabCurrentWorkout)