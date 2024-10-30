package com.example.ontour.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ViewModelActivity() : ViewModel(){
    val screen : MutableState<Activity> = mutableStateOf(Activity.Home)
}