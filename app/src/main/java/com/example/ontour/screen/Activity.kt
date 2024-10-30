package com.example.ontour.screen

sealed class Activity(route : String){
    object Home:Activity("Home")
    object Add:Activity("Add")
    object Profile:Activity("Profile")
}