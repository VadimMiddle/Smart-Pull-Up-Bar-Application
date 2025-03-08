package com.example.smartpull_upbar.presentation.workout_screen.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowDropDownCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.ExerciseCategory
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.ExerciseData
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.myCategories
import com.example.smartpull_upbar.ui.theme.myBlack


// POP UP Activity:
@Composable
fun Exercise(exercise: ExerciseData, viewModel: WorkoutViewModel)
{
    Row(modifier = Modifier
            .fillMaxWidth(0.99f)
            .height(100.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(35))
            .clickable
            {
                viewModel.selectExercise(exercise)
                viewModel.hideExercisePicker()
                           },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically)
    {
        Spacer(modifier = Modifier.size(10.dp))
        Image(painter = painterResource(exercise.image), null,
                modifier = Modifier.align(Alignment.Bottom))
        Spacer(modifier = Modifier.size(10.dp))

        Column(  modifier = Modifier.fillMaxHeight(0.99f).fillMaxWidth(1f)
                .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.SpaceEvenly,

                )
        {

            Text(text = exercise.name, fontSize = 15.sp, modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.ExtraBold )

            Text(text = exercise.bestRepetition + exercise.bestRepCount,
                    fontSize = 15.sp  )

            Spacer(modifier = Modifier.size(2.dp))
            Box (modifier = Modifier.clickable{}.fillMaxWidth(0.4f),
                    contentAlignment = Alignment.Center,
                    content = {
                        Image(painter = painterResource(R.drawable.desc), null)
                        Row(modifier = Modifier.fillMaxWidth(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                        ) {
                            Text(text = "Описание", style = TextStyle(Color.White,
                                    fontSize = TextUnit(15f, TextUnitType.Sp)))
                            Icon(Icons.Sharp.ArrowDropDownCircle, null,
                                    tint = Color.White, modifier = Modifier.size(20.dp)
                            )
                        }
                    } )
        }
    }
}

@Composable
fun ExerciseCategoryHeader(name: String, modifier: Modifier = Modifier)
{
    Text(
            text = name, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
            color = Color.LightGray,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategorizedExercisesLazyColumn(
        categories: List<ExerciseCategory>,
        modifier: Modifier = Modifier,
        viewModel: WorkoutViewModel
)
{
    LazyColumn(modifier.fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally) {
        // Iterate over categories and add sticky headers
        categories.forEach { category ->
            // Add a sticky header for the current category
            stickyHeader {
                ExerciseCategoryHeader(category.name)
            }

            // Add items for the current category
            items(category.exerciseItems) { exercise ->
                Exercise(exercise = exercise, viewModel = viewModel) // Pass the exercise to the ExerciseItem composable
            }
        }
    }
}

@Composable
fun MyLazyColumn(viewModel: WorkoutViewModel)
{
    CategorizedExercisesLazyColumn(categories = myCategories, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePicker(dontShow: () -> Unit, modifier: Modifier, viewModel: WorkoutViewModel)
{
    val sheetState = rememberModalBottomSheetState(true)

    ModalBottomSheet(onDismissRequest = dontShow, sheetState = sheetState,
            content = {MyLazyColumn(viewModel = viewModel)},
            contentColor = myBlack)
}
//-----------------------------------------------------------------