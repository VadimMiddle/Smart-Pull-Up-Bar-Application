package com.example.smartpull_upbar.domain.bluetooth

interface ConnectionState
 {
    object Connected: ConnectionState
    object Disconnected: ConnectionState

    object Uninitialized: ConnectionState
    object CurrentlyInitializing: ConnectionState
 }