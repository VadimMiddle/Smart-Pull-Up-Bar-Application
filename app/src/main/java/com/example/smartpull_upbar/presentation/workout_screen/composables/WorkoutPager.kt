package com.example.smartpull_upbar.presentation.workout_screen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.smartpull_upbar.presentation.workout_screen.WorkoutViewModel
import com.example.smartpull_upbar.presentation.workout_screen.pagerTabs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// PAGER
//-----------------------------------------------------------------
@Composable
fun WorkoutPager(initializeConnection:() -> Unit,
                 topMessage: String, bottomMessage: String?,
                 startWorkout: () -> Unit, workoutStarted: Boolean,
                 viewModel: WorkoutViewModel)
{
    val pagerState = rememberPagerState {if (workoutStarted) 2 else 1}
    val animationScope = rememberCoroutineScope()

    HorizontalPager(state = pagerState) { page ->
        if (workoutStarted) {
            when (page) {
                0 -> WorkoutScreen(initializeConnection, topMessage, bottomMessage, startWorkout)
                1 -> CurrentWorkoutScreen(viewModel)
            }
        }
        else
            when (page) {
                0 -> WorkoutScreen(initializeConnection, topMessage, bottomMessage, startWorkout)
            }
    }

    Column(
            Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f), verticalArrangement = Arrangement.Top, ) {
        if (workoutStarted) {
            TabsForPager(pagerState= pagerState, animationScope = animationScope)
        }
    }

}

@Composable
fun TabsForPager(pagerState: PagerState, animationScope: CoroutineScope)
{
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPage)
    {
        selectedTabIndex = pagerState.currentPage
    }

    Row(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)) {
        pagerTabs.forEachIndexed { index, tab ->

            Button( onClick = {selectedTabIndex = index
                animationScope.launch {  pagerState.animateScrollToPage(index) } },
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (selectedTabIndex == index) tab.selectedColor
                            else tab.unselectedColor, RoundedCornerShape(0)), colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTabIndex == index) tab.selectedColor else tab.unselectedColor,
                    contentColor = Color.White  ) )
            {
                Text(   text = tab.name,
                        textAlign = if (tab.index == 0) {
                            TextAlign.Left} else TextAlign.Right ,
                        style = TextStyle(fontSize = TextUnit(13f, TextUnitType.Sp))
                )
            }
        }
    }
}