package com.example.smartpull_upbar.presentation.workout_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpull_upbar.domain.Resource
import com.example.smartpull_upbar.domain.bluetooth.ConnectionState
import com.example.smartpull_upbar.domain.bluetooth.PullUpsBLEReceiveManager
import com.example.smartpull_upbar.presentation.workout_screen.data_classes.ExerciseData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("SpellCheckingInspection")
@HiltViewModel
class WorkoutViewModel
@Inject constructor(private val bleManager: PullUpsBLEReceiveManager)
    : ViewModel() {


    // Статус соединения:
    var statusTopMessage by mutableStateOf("Нажмите для подключения")
        private set
    var statusBottomMessage by mutableStateOf<String?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)
        private set

    // Для навигации
    var selectedTabIndex by mutableIntStateOf(0)

    // Для панели управления:
    private val _workoutStarted = MutableStateFlow(false)
    val workoutStarted: StateFlow<Boolean> = _workoutStarted.asStateFlow()

    fun startWorkout() {
        _workoutStarted.value = true
    }

    // Для текущей тренировки:
    // Замена EmptyWorkout на ActiveWorkout:
    private val _exerciseIsSelected = MutableStateFlow(false)
    var exerciseIsSelected = _exerciseIsSelected.asStateFlow()

    // Pop Up / Exercise Picker:
    private val _showExercisePicker = MutableStateFlow(false)
    val showExercisePicker = _showExercisePicker.asStateFlow()

    fun showExercisePicker()
    {
        _showExercisePicker.value = true
    }
    fun hideExercisePicker()
    {
        _showExercisePicker.value = false
    }

    // Exercise Data

    data class SetData (
            val currentSet: Int,
            val duration: String = "12m 32s",
            val reps: Int
    )

    data class SelectedExerciseData (
            val exerciseName: String,
            val exerciseData: List<SetData> // List of sets for the exercise
    )

    private val _selectedExercise = MutableStateFlow<String?>(null)
    var selectedExercise = _selectedExercise.asStateFlow()

    private val _exerciseData = MutableStateFlow<List<SelectedExerciseData>>(emptyList())
    val exerciseData: StateFlow<List<SelectedExerciseData>> = _exerciseData.asStateFlow()

    fun selectExercise(exercise: ExerciseData) {
        _selectedExercise.value = exercise.name
        _exerciseIsSelected.value = true

        bleManager.writeNewExerciseStarted(true)
    }

    // What we stuff those instances with
    var currentRepCount by mutableIntStateOf(0)
        private set
    var maxRepCount by mutableIntStateOf(0)
        private set

    // What we stuff those instances with
    var currentSet by mutableIntStateOf(1)
        private set

    // List of data class SetData instances
    private val _setsData = MutableStateFlow<List<SetData>>(emptyList())
    val setsData = _setsData.asStateFlow()

    private fun updateSetsData(setNumber: Int, reps: Int) {
        // Update the rep counts
        maxRepCount = 0
        currentRepCount = reps
        if (currentRepCount > maxRepCount)
        {
            maxRepCount = currentRepCount
        }

        // Set number
        currentSet = setNumber

        // Create a new SetData instance
        val newSetData = SetData(
                currentSet = currentSet,
                reps = maxRepCount
        )

            val updatedSetsData = _setsData.value.toMutableList()
            val existingSetIndex = updatedSetsData.indexOfFirst {
                it.currentSet == setNumber
            }

            if (existingSetIndex != -1) {
                // Update existing SetData if the new reps count is higher
                if (reps > updatedSetsData[existingSetIndex].reps) {
                    updatedSetsData[existingSetIndex] = newSetData
                }
            } else {
                // Add new SetData if it doesn't exist
                updatedSetsData.add(newSetData)
            }
            _setsData.value = updatedSetsData

        }


    private fun subscribeToChanges() {
         viewModelScope.launch {
             bleManager.data.collect { result ->
                 when(result)
                 {
                     is Resource.Success -> {
                         updateSetsData(setNumber = result.data.currentSet,
                                 reps = result.data.numberOfPullUps,
                                 )

                         statusTopMessage = result.topSuccessMessage
                         statusBottomMessage = result.bottomSuccessMessage
                         connectionState = result.data.connectionState
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
         bleManager.startConnect()
     }

     fun reconnect() {
         bleManager.reConnect()
     }
     fun disconnect(){
         bleManager.disConnect()
     }

     override fun onCleared() {
         super.onCleared()
         bleManager.closeConnect()
     }
 }



























//     private val _needToShowDialog = MutableStateFlow(false)
//     val needToShowDialog = _needToShowDialog.asStateFlow()
//
//     private val _needToGoSettings = MutableStateFlow(false)
//     val needToGoSettings = _needToShowDialog.asStateFlow()
//
//     fun updateNeedToShowDialog(show: Boolean) {
//         _needToShowDialog.update { show }
//     }
//     fun updateNeedToGoSettings(goSettings: Boolean) {
//         _needToShowDialog.update { goSettings }
//     }
