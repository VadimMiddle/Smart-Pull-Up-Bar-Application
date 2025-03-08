package com.example.smartpull_upbar.presentation

import kotlinx.serialization.Serializable

interface Screens {

@Serializable
data object WorkoutScreen: Screens

@Serializable
data object HistoryScreen: Screens

@Serializable
data object ProfileScreen: Screens
}