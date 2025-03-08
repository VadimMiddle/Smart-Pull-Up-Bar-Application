package com.example.smartpull_upbar.presentation.workout_screen

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.sharp.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartpull_upbar.R
import com.example.smartpull_upbar.ui.theme.SmartPullupBarTheme
import com.example.smartpull_upbar.ui.theme.myBlack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun WorkoutPager(initializeConnection:() -> Unit,
                 topMessage: String, bottomMessage: String?, pullUps: Int,
                 startWorkout: () -> Unit, workoutStarted: Boolean)
{
    val pagerState = rememberPagerState {if (workoutStarted) 2 else 1}
    val animationScope = rememberCoroutineScope()

    HorizontalPager(state = pagerState) { page ->
        if (workoutStarted) {
            when (page) {
                0 -> WorkoutScreen(initializeConnection, topMessage, bottomMessage, pullUps, startWorkout)
                1 -> CurrentWorkoutScreen()
            }
        }
        else
            when (page) {
            0 -> WorkoutScreen(initializeConnection, topMessage, bottomMessage, pullUps, startWorkout)
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

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Row(modifier = Modifier.fillMaxWidth().height(60.dp)) {
        pagerTabs.forEachIndexed { index, tab ->

            Button( onClick = {selectedTabIndex = index
                    animationScope.launch {  pagerState.animateScrollToPage(index) } },
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (selectedTabIndex == index) tab.selectedColor
                            else tab.unselectedColor, RoundedCornerShape(0)    ), colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTabIndex == index) tab.selectedColor else tab.unselectedColor,
                    contentColor = Color.White  ) )
            {
                Text(   text = tab.name,
                        textAlign = if (tab.index == 0) {TextAlign.Left} else TextAlign.Right ,
                        style = TextStyle(fontSize = TextUnit(13f, TextUnitType.Sp))
                )
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun WorkoutScreen (initializeConnection:() -> Unit,
                  topMessage: String, bottomMessage: String?, pullUps: Int,
                  startWorkout: () -> Unit )
{
    val lifecycleOwner = LocalLifecycleOwner.current;
    val context = LocalContext.current

    val myPermissions = PermissionHandler(context)
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothEnabler = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                initializeConnection()
            })  // start connect

    val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                bluetoothEnabler.launch(enableBluetoothIntent)
            })  // enable bluetooth

    Box(modifier = Modifier.fillMaxSize())
    {
        Image(painter = painterResource(R.drawable.background), null,
                modifier = Modifier.align(Alignment.TopCenter))

        Row(modifier = Modifier.align(Alignment.TopCenter))
        {
            Image(painter = painterResource(R.drawable.cloud1), null)
            Spacer(modifier = Modifier.size(100.dp))
            Image(painter = painterResource(R.drawable.cloud2), null)
        } // Ряд облаков

        Column(modifier = Modifier.align(Alignment.TopCenter))
        {
            Spacer(modifier = Modifier.size(50.dp))
            Text(text = "${pullUps}", color = Color.Black, fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(100f, TextUnitType.Sp))
        } // Ряд кнопок

        Box(contentAlignment = Alignment.Center, modifier = Modifier.align(Alignment.BottomCenter))
        {
            Image(painter = painterResource(R.drawable.bar_itself), null)
            Image(painter = painterResource(R.drawable.bushes), null,
                    modifier = Modifier.align(Alignment.BottomCenter))
            Column(modifier = Modifier.align(Alignment.BottomCenter))
            {
                ConnectToDeviceButton(showBluetooth = {
                    (permissionLauncher.launch(myPermissions.permissions))
                }, topMessage = topMessage, bottomMessage = bottomMessage)
                Spacer(modifier = Modifier.size(25.dp))
            }
        } // Картинки + кнопка

        Column(modifier = Modifier
                .align(Alignment.Center)
                .height(140.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally)
        {
            StartWorkoutButton(startWorkout = startWorkout)
            Text(text = "Начать", color = Color.Black, fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally))
        } // Колонна кнопок
    }
}

@Composable
fun StartWorkoutButton(startWorkout: () -> Unit)
{
    IconButton(onClick = { startWorkout() },
            modifier = Modifier
                    .size(30.dp)
                    .scale(2.5f),
            colors = IconButtonColors(myBlack, myBlack, myBlack, myBlack))
    {
        Icon(imageVector = Icons.Filled.Add, null,
                tint = Color.White)
    }
}

@Composable
fun ConnectToDeviceButton(topMessage: String, bottomMessage: String?, showBluetooth: () -> Unit)
{
    Column(modifier = Modifier.fillMaxWidth(0.9f), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick =  showBluetooth, modifier = Modifier.fillMaxWidth(0.99f) ,
                colors = ButtonColors(Color.Blue, Color.Blue, Color.Blue, Color.Blue),
                border = BorderStroke(4.dp, Color.White),
        ) {

            Row(
                    modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(60.dp)
                            .clip(shape = RoundedCornerShape(24.dp))
                            .background(Color.Blue),
                    verticalAlignment = Alignment.CenterVertically,
            )
            {
                Spacer(modifier = Modifier.size(3.dp))
                Icon(Icons.Sharp.Bluetooth, tint = Color.White, contentDescription = null, modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.size(9.dp))
                VerticalDivider(color = Color.White, thickness = 4.dp, modifier = Modifier.fillMaxHeight())
                Spacer(modifier = Modifier.size(23.dp))
                Column(modifier = Modifier.fillMaxHeight(0.9f),
                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Text(text = topMessage, color = Color.White,
                            style = TextStyle(fontSize = TextUnit(15f, TextUnitType.Sp)))

                    bottomMessage?.let {
                        Text(text = it, color = Color.White,
                                style = TextStyle(fontSize = TextUnit(15f, TextUnitType.Sp)))
                    }

                }
            }
        }
    }
}

@Composable
fun CurrentWorkoutScreen()
{
    Box(modifier = Modifier
            .fillMaxSize(1f)
            .background(myBlack), contentAlignment = Alignment.BottomCenter)
    {
        Column(modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .fillMaxWidth(0.65f),
                verticalArrangement = Arrangement.Center)
        {
            Text(text = "Тут пока пусто...", fontWeight = FontWeight.SemiBold,
                    style = TextStyle(fontSize = TextUnit(20f, TextUnitType.Sp)))
            Image(painter = painterResource(R.drawable.empty), null,
                    modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = "...вы можете начать тренировку", fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Right,
                    style = TextStyle(fontSize = TextUnit(20f, TextUnitType.Sp)),
                    modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Column()
        {
            AddExerciseButton()
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
 }

@Composable
fun AddExerciseButton()
{
    Column(modifier = Modifier.fillMaxWidth(0.9f),
           horizontalAlignment = Alignment.CenterHorizontally)
    {
        Button( onClick = {  }, modifier = Modifier.fillMaxWidth(),
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

@Composable
fun PreviewWorkoutScreen()
{
    SmartPullupBarTheme {
        WorkoutScreen(initializeConnection = {}, "", "", 0, {  })
    }
}

@Composable
@Preview
fun pickAnExerciseScreen() {
Box(Modifier.fillMaxHeight(0.8f)) {

}
}





