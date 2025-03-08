package com.example.smartpull_upbar.presentation.workout_screen.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpull_upbar.R
import com.example.smartpull_upbar.presentation.workout_screen.WorkoutViewModel
import com.example.smartpull_upbar.ui.theme.myBlack
import kotlin.time.Duration

// CURRENT WORKOUT SCREEN
@Composable
fun CurrentWorkoutScreen(viewModel: WorkoutViewModel)
{
    val showExercisePicker by viewModel.showExercisePicker.collectAsState()
    val exerciseIsSelected by viewModel.exerciseIsSelected.collectAsState()

    Box(modifier = Modifier.fillMaxSize(1f).background(Color.DarkGray),
            contentAlignment = Alignment.BottomCenter)
    {
        if (exerciseIsSelected)
        {
            Column(modifier = Modifier.fillMaxSize())
            {
               Spacer(modifier = Modifier.size(70.dp))
               ActiveWorkout(viewModel = viewModel, modifier = Modifier.align(Alignment.Start))
            }
        }

        else
        {
            EmptyWorkout()
        }

        Column()
        {
            AddExerciseButton(showList = { viewModel.showExercisePicker() })
            Spacer(modifier = Modifier.size(20.dp))
        }
    }

    if (showExercisePicker)
    {
        ExercisePicker(dontShow = { viewModel.hideExercisePicker() }, modifier = Modifier.fillMaxHeight(0.9f), viewModel)
    }
}

//-----------------------------------------------------------------
// EMPTY
@Composable
fun EmptyWorkout()
{

    Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.65f), verticalArrangement = Arrangement.Center)
    {
        Text(text = "Тут пока пусто...", fontWeight = FontWeight.SemiBold,
                style = TextStyle(fontSize = TextUnit(20f, TextUnitType.Sp), color = myBlack))
        Image(painter = painterResource(R.drawable.empty), null,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp))
        Text(text = "...вы можете начать тренировку", fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Right,
                style = TextStyle(fontSize = TextUnit(20f, TextUnitType.Sp), color = myBlack),
                modifier = Modifier.align(Alignment.CenterHorizontally))
    } // колонна пока пусто
}

//-----------------------------------------------------------------
// ACTIVE
@Composable
fun ActiveWorkout(viewModel: WorkoutViewModel, modifier: Modifier = Modifier)
{
    val setsData by viewModel.setsData.collectAsState()
    val selectedExercise by viewModel.selectedExercise.collectAsState()

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally)
    {

        ExerciseTitle(exerciseName = selectedExercise)
        Spacer(modifier = Modifier.size(9.dp))
        ExerciseHeader()
        Spacer(modifier = Modifier.size(6.dp))
        setsData.forEach { set ->
        if (set.currentSet == 0) {
        } else {
            ExerciseData(set.currentSet, reps = set.reps)
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
      }
}

@Composable
fun ExerciseTitle(exerciseName: String?)
{
    Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray).height(25.dp),
            verticalAlignment = Alignment.CenterVertically)
    {
        if (exerciseName != null) {
            Text(modifier = Modifier.fillMaxWidth(),
                    text = exerciseName, textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp)
        }
    }
}

@Composable
fun ExerciseHeader(sets: String = "Подходы",
                   reps: String = "Повторения",
                   duration: String = "Длительность")
{
    Row(modifier = Modifier.fillMaxWidth(0.91F).background(Color.Transparent).height(25.dp), )
    {
        Text(modifier = Modifier.weight(0.9f), textAlign = TextAlign.Left,
                fontWeight = FontWeight.ExtraBold, fontSize = 16.sp,
                color = Color.White, text = sets)

        Text(modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, text = duration)

        Text(modifier = Modifier.weight(1f), textAlign = TextAlign.Right,
                color = Color.White, fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp, text = reps)
    }
}

@Composable
fun ExerciseData(sets: Int, reps: Int)
{
    Row(modifier = Modifier.fillMaxWidth(0.95f).background(color = Color.Blue,
            shape = RoundedCornerShape(30)))
    {
        Text(modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold, fontSize = 16.sp,
                color = Color.White, text = "$sets")

        Text(modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, text = "12:32")

        Text(modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                color = Color.White, fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp, text = "$reps")
    }
}

@Composable
fun ResultOfExercise(totalSets: Int, totalReps: Int, totalDuration: Duration)
{
    Row() {
        Text(text = "⭐ $totalSets ", color = Color.Blue, fontWeight = FontWeight.ExtraBold)
        Text(text = "подхода, ", color = Color.White, fontWeight = FontWeight.SemiBold)

        Text(text = "$totalDuration ", color = Color.Blue, fontWeight = FontWeight.ExtraBold)
        Text(text = "тренировок, ", color = Color.White, fontWeight = FontWeight.SemiBold)

        Text(text = "$totalReps ", color = Color.Blue, fontWeight = FontWeight.ExtraBold)
        Text(text = "повторений", color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AddExerciseButton(showList: () -> Unit)
{
    Column(modifier = Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally)
    {
        Button( onClick = showList, modifier = Modifier.fillMaxWidth(),
                colors = ButtonColors(Color.Blue, Color.Blue, Color.Blue, Color.Blue),
                border = BorderStroke(4.dp, Color.White) )
        {
            Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(60.dp)
                    .clip(shape = RoundedCornerShape(24.dp))
                    .background(Color.Blue),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Left)
            {
                Icon(Icons.Filled.AddCircle, null,
                        tint = Color.White, modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.size(15.dp))
                Text(text = "Добавить упражнение", color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(22f, TextUnitType.Sp) )
            }
        }
    }
}