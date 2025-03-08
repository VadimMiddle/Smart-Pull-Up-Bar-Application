package com.example.smartpull_upbar.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartpull_upbar.presentation.history_screen.HistoryScreen
import com.example.smartpull_upbar.presentation.overview_screen.ProfileScreen
import com.example.smartpull_upbar.presentation.workout_screen.WorkoutPager
import com.example.smartpull_upbar.presentation.workout_screen.WorkoutViewModel
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.tab1
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.tab2
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.tab3
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.tabs

@Composable
@Preview
fun Navigation() {
    val viewModel = hiltViewModel<WorkoutViewModel>()
    val workoutIsStarted by viewModel.workoutStarted.collectAsState()

    val navigationController = rememberNavController()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    @Composable // if else для выбранной и невыбранной вкладки
    fun Tabs() {
        Row(modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray),
            horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom)
        {
            tabs.forEachIndexed { _, tabs ->
                Button(onClick = { navigationController.navigate(tabs.route) },
                       colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent))
                {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.padding(3.dp))
                    {
                        if (selectedTabIndex == tabs.index)
                        {
                            Image(painter = painterResource(id = tabs.filled), contentDescription = null,
                                    modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.size(7.dp))
                            Text(text = tabs.name, fontWeight = FontWeight.ExtraBold,
                                    color = Color.White)
                        }
                        else
                        {
                            Icon(painter = painterResource(tabs.outlined), contentDescription = null,
                                    modifier = Modifier.size(20.dp), Color.Gray)
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(text = tabs.name, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    @Composable // NavHost со всеми экранами
    fun MyScreens() {

        NavHost(navController = navigationController,
                startDestination = Screens.WorkoutScreen,
                modifier = Modifier.fillMaxWidth(1f) .fillMaxHeight(0.91f),
                         contentAlignment = Alignment.TopCenter)

        {
            composable<Screens.WorkoutScreen>
            {
                WorkoutPager(initializeConnection = {viewModel.initializeConnection()},
                        topMessage = viewModel.statusTopMessage, bottomMessage = viewModel.statusBottomMessage,
                        pullUps = viewModel.pullUps, startWorkout = { viewModel.startWorkout() },
                        workoutStarted = workoutIsStarted)
                selectedTabIndex = tab1.index
            }

            composable<Screens.HistoryScreen>
            {
                HistoryScreen()
                selectedTabIndex = tab2.index
            }

            composable<Screens.ProfileScreen>
            {
                ProfileScreen()
                selectedTabIndex = tab3.index
            }
        }
    }

    @Composable // Колонна из экранов (сверху) и вкладок (снизу)
    fun myScreens_and_Tabs() {
        Column(modifier = Modifier.fillMaxSize(),
               horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom)
        {
            MyScreens()
            Tabs()
        }
    }

    myScreens_and_Tabs()
}

