package com.example.ontour.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ontour.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier,navController: NavHostController) {
    LaunchedEffect(Unit){
        delay(500)
        navController.navigate(route="onBoarding")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00637C),
                        Color(0xFF00B5E2)
                    ),
                )
            )
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            colorFilter = ColorFilter.tint(Color.White), // Change to your desired color
            modifier = Modifier.size(250.dp) // Change to your desired size
        )

    }
}