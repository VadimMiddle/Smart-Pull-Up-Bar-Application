package com.example.smartpull_upbar.domain.bluetooth

data class PullUpsResult
  (
    var numberOfPullUps: Int,
    var currentSet: Int,
    val connectionState: ConnectionState
  )

