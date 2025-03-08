package com.example.smartpull_upbar.presentation.history_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable

fun HistoryScreen() {

    Column (modifier = Modifier.fillMaxSize() .background(Color.Transparent)
            ,
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
    { Text(text = "History Screen" ) }

}