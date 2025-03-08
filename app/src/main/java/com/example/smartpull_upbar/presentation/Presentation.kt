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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartpull_upbar.presentation.history_screen.HistoryScreen
import com.example.smartpull_upbar.presentation.overview_screen.ProfileScreen
import com.example.smartpull_upbar.presentation.workout_screen.WorkoutViewModel
import com.example.smartpull_upbar.presentation.workout_screen.composables.WorkoutPager
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.tabs

@Composable // Колонна из экранов (сверху) и вкладок (снизу)
fun MyScreens_and_Tabs()
{
    val viewModel = hiltViewModel<WorkoutViewModel>()
    var selectedTabIndex = viewModel.selectedTabIndex

    val navigationController = rememberNavController()

    val workoutIsStarted by viewModel.workoutStarted.collectAsState()

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom)
    {
       MyScreens(navController = navigationController, viewModel = viewModel, workoutIsStarted = workoutIsStarted)
       Tabs(navController = navigationController,
               onTabSelected = { index -> selectedTabIndex = index },
               selectedTabIndex = selectedTabIndex)
    }
 }

@Composable // NavHost со всеми экранами
fun MyScreens(navController: NavHostController,
              viewModel: WorkoutViewModel,
              workoutIsStarted: Boolean)
{
    NavHost(navController = navController,
            startDestination = Screens.WorkoutScreen,
            modifier = Modifier.fillMaxWidth(1f) .fillMaxHeight(0.91f),
            contentAlignment = Alignment.TopCenter)

    {
        composable<Screens.WorkoutScreen>
        {
            WorkoutPager(
                    initializeConnection = { viewModel.initializeConnection() },
                    topMessage = viewModel.statusTopMessage, bottomMessage = viewModel.statusBottomMessage,
                    startWorkout = { viewModel.startWorkout() },
                    workoutStarted = workoutIsStarted,
                    viewModel = viewModel)
            viewModel.selectedTabIndex = 0
        }

        composable<Screens.HistoryScreen>
        {
            HistoryScreen()
            viewModel.selectedTabIndex = 1
        }

        composable<Screens.ProfileScreen>
        {
            ProfileScreen()
            viewModel.selectedTabIndex = 2
        }
    }
}

@Composable // if else для выбранной и невыбранной вкладки
fun Tabs(navController: NavController,
         onTabSelected: (Int) -> Unit,
         selectedTabIndex: Int)
{
    
    Row(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Transparent),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom)
    {
        tabs.forEachIndexed { index, tab ->
            Button(modifier = Modifier.fillMaxHeight(1f).weight(1f),
                   shape = RoundedCornerShape(0),
                   onClick = {
                navController.navigate(tab.route)
                {
                    navController.graph.startDestinationRoute?.let { startRoute ->
                        popUpTo(startRoute) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                onTabSelected(index)
            },
                   colors = ButtonDefaults.buttonColors(
                   containerColor = if (selectedTabIndex == tab.index) {Color.Black}
            else {Color.DarkGray}
                   ))
            {
                Column(modifier = Modifier.background(color = Color.Transparent).align(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom,
                        )
                {
                    if (selectedTabIndex == tab.index)
                    {
                        Image(painter = painterResource(id = tab.filled), contentDescription = null,
                                modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.size(7.dp))
                        Text(text = tab.name, fontWeight = FontWeight.ExtraBold,
                                color = Color.White)
                    }
                    else
                    {
                        Icon(painter = painterResource(tab.outlined), contentDescription = null,
                                modifier = Modifier.size(20.dp), Color.Gray)
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(text = tab.name, color = Color.Gray)
                    }
                }
            }
        }
    }
}
