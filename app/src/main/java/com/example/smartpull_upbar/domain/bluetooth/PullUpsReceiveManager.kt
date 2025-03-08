package com.example.smartpull_upbar.domain.bluetooth

import com.example.smartpull_upbar.domain.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface PullUpsReceiveManager {

    val data: MutableSharedFlow<Resource<PullUpsResult>>

    fun startConnect()

    fun reConnect()

    fun disConnect()

    fun closeConnect()
}