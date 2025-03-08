package com.example.smartpull_upbar.presentation.workout_screen.composables

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.sharp.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.smartpull_upbar.R
import com.example.smartpull_upbar.presentation.workout_screen.PermissionHandler
import com.example.smartpull_upbar.ui.theme.myBlack

// WORKOUT SCREEN
//-----------------------------------------------------------------
@SuppressLint("SuspiciousIndentation")
@Composable
fun WorkoutScreen (initializeConnection:() -> Unit,
                   topMessage: String, bottomMessage: String?,
                   startWorkout: () -> Unit )
{
//    val lifecycleOwner = LocalLifecycleOwner.current
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
        } // Картинки + кнопка ConnectToDeviceButton

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
        Button(onClick = showBluetooth, modifier = Modifier.fillMaxWidth(0.99f) ,
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
//-----------------------------------------------------------------