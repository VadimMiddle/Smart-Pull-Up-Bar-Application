package com.example.smartpull_upbar.presentation.workout_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpull_upbar.domain.Resource
import com.example.smartpull_upbar.domain.bluetooth.ConnectionState
import com.example.smartpull_upbar.domain.bluetooth.PullUpsReceiveManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel
@Inject constructor(private val pullUpsReceiveManager: PullUpsReceiveManager)
    : ViewModel()
 {

    var statusTopMessage by mutableStateOf("Нажмите для подключения")
        private set

     var statusBottomMessage by mutableStateOf<String?>(null)
         private set

    var errorMessage by mutableStateOf<String?>(null)
     private set

    var pullUps by mutableIntStateOf(0)
      private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)
     private set

     private val _workoutStarted = MutableStateFlow(false)
     val workoutStarted: StateFlow<Boolean> = _workoutStarted.asStateFlow()

     fun startWorkout() {
         _workoutStarted.value = true
     }

     private val _needToShowDialog = MutableStateFlow(false)
     val needToShowDialog = _needToShowDialog.asStateFlow()

     private val _needToGoSettings = MutableStateFlow(false)
     val needToGoSettings = _needToShowDialog.asStateFlow()

     fun updateNeedToShowDialog(show: Boolean) {
         _needToShowDialog.update { show }
     }
     fun updateNeedToGoSettings(goSettings: Boolean) {
         _needToShowDialog.update { goSettings }
     }


     private fun subscribeToChanges() {
        viewModelScope.launch {
            pullUpsReceiveManager.data.collect { result ->
                when(result)
                {
                    is Resource.Success -> {
                        statusTopMessage = result.topSuccessMessage
                        statusBottomMessage = result.bottomSuccessMessage
                        connectionState = result.data.connectionState
                        pullUps = result.data.numberOfPullUps
                    }

                    is Resource.Loading -> {
                        statusTopMessage = result.topLoadingMessage
                        statusBottomMessage = result.bottomLoadingMessage
                        connectionState = ConnectionState.CurrentlyInitializing
                    }

                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }
                }
            }
        }
    }

    fun initializeConnection(){
         errorMessage = null
         subscribeToChanges()
         pullUpsReceiveManager.startConnect()
     }

    fun reconnect() {
        pullUpsReceiveManager.reConnect()
    }
    fun disconnect(){
         pullUpsReceiveManager.disConnect()
     }

    override fun onCleared() {
        super.onCleared()
        pullUpsReceiveManager.closeConnect()
    }

 }

