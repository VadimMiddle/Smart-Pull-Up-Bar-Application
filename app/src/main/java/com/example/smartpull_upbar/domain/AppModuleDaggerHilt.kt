package com.example.smartpull_upbar.domain

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.smartpull_upbar.domain.bluetooth.PullUpsBLEReceiveManager
import com.example.smartpull_upbar.domain.bluetooth.PullUpsReceiveManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModuleDaggerHilt {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter
    {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }

    @Provides
    @Singleton
    fun providePullUpsBLEReceiveManager(@ApplicationContext context: Context, bluetoothAdapter: BluetoothAdapter)
    : PullUpsReceiveManager
    {
      return PullUpsBLEReceiveManager(bluetoothAdapter, context)
    }
    @Provides
    @Singleton
    fun provideBLEManager(@ApplicationContext context: Context,  bluetoothAdapter: BluetoothAdapter): PullUpsBLEReceiveManager {
        return PullUpsBLEReceiveManager(bluetoothAdapter, context)
    }


}