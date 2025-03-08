package com.example.smartpull_upbar

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.smartpull_upbar.presentation.MyScreens_and_Tabs
import com.example.smartpull_upbar.ui.theme.SmartPullupBarTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartPullupBarTheme {
            MyScreens_and_Tabs()
            }
        }
    }
}
















