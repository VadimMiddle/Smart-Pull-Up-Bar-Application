package com.example.smartpull_upbar.presentation.workout_screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

class PermissionHandler(context: Context) {
    private val bluetoothPermission = Manifest.permission.BLUETOOTH
    private val bluetoothScanPermission = Manifest.permission.BLUETOOTH_SCAN
    private val bluetoothConnectPermission = Manifest.permission.BLUETOOTH_CONNECT

    val permissions = arrayOf( bluetoothConnectPermission, bluetoothScanPermission  )

    private val bluetoothPermissionIsGranted : Boolean = context.checkSelfPermission(bluetoothPermission) == PackageManager.PERMISSION_GRANTED
    private val bluetoothScanPermissionIsGranted = context.checkSelfPermission(bluetoothScanPermission) == PackageManager.PERMISSION_GRANTED
    private val bluetoothConnectPermissionIsGranted = context.checkSelfPermission(bluetoothConnectPermission) == PackageManager.PERMISSION_GRANTED

    private var areGranted = bluetoothScanPermissionIsGranted && bluetoothConnectPermissionIsGranted
    var notGranted = !areGranted
}