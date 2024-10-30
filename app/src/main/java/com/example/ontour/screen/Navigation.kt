package com.example.ontour.screen


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ontour.screen.Trader.AddMapPosition
import com.example.ontour.screen.Trader.BusinessActivity
import com.example.ontour.screen.Trader.ChooseBetweenSingInUp
import com.example.ontour.screen.Trader.HomeScreen
import com.example.ontour.screen.Trader.PersonInfo
import com.example.ontour.screen.Trader.SignInTrader
import com.example.ontour.screen.Trader.ThanksScreen
import com.example.ontour.screen.Trader.UploadFile
import com.example.ontour.screen.client.DrawRoute
import com.example.ontour.screen.client.Home
import com.example.ontour.screen.client.OTPInputScreen
import com.example.ontour.screen.client.Onboarding
import com.example.ontour.screen.client.SingIn
import com.example.ontour.screen.client.StoreInfoScreen
import com.mapbox.geojson.Point

import com.mapbox.maps.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(modifier: Modifier = Modifier , context: Context) {

    val navController = rememberNavController()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val viewModel = OTPViewModel()
    val viewModelB = BusinessViewModel()

    LaunchedEffect(key1 = true) {
        viewModel.createApplication()
    }



    NavHost(navController = navController, startDestination = "splash_screen") {

        composable(route = "splash_screen") {
            SplashScreen(navController = navController)
        }

        composable(
            route = "draw_route/{longitude}/{latitude}",
            arguments = listOf(
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("latitude") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val longitude = backStackEntry.arguments?.getFloat("longitude") ?: 0f
            val latitude = backStackEntry.arguments?.getFloat("latitude") ?: 0f
            val point = Point.fromLngLat(longitude.toDouble(), latitude.toDouble())
            DrawRoute(navController = navController , pointD = point)
        }

        composable(
            route = "StoreInfo/{longitude}/{latitude}",
            arguments = listOf(
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("latitude") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val longitude = backStackEntry.arguments?.getFloat("longitude") ?: 0f
            val latitude = backStackEntry.arguments?.getFloat("latitude") ?: 0f
            val point = Point.fromLngLat(longitude.toDouble(), latitude.toDouble())
            val services = viewModelB.service.collectAsState()
            services.value.forEach {
                Log.d("fromLnLa", "Navigation: ${it.geographicalPoint.longitude()}  == ${point.longitude()} && ${it.geographicalPoint.latitude()} == ${point.latitude()} ")
                if (it.geographicalPoint.longitude() == point.longitude() && it.geographicalPoint.latitude() == point.latitude()){
                    StoreInfoScreen(point = point, navController = navController , service = it)
                }
            }
        }


        composable(route = "onBoarding") {
            Onboarding(navController = navController, screenHeight = screenHeight, screenWidth = screenWidth)
        }

        composable(route = "home") {
            Home(screenWidth = screenWidth, screenHeight = screenHeight , viewModelB = viewModelB , navController = navController)
        }

        composable(route = "singIn") {
            SingIn(screenWidth = screenWidth, screenHeight = screenHeight, navHostController = navController , viewModel = viewModel)
        }

        composable(route = "verificationCode") {
            OTPInputScreen(screenWidth = screenWidth, screenHeight = screenHeight, navHostController = navController, viewModel = viewModel)
        }

        // Add navigation for new screens
        composable(route = "addMapPosition") {
            AddMapPosition(navHostController = navController, screenWidth = screenWidth, screenHeight = screenHeight, viewModelB = viewModelB)
        }

        composable(route = "businessActivity") {
            BusinessActivity(screenWidth = screenWidth, screenHeight = screenHeight , navHostController = navController, viewModelB = viewModelB)
        }

        composable(route = "homeTrader") {
            HomeScreen(screenWidth = screenWidth, screenHeight = screenHeight , viewModelB , context = context)
        }

        composable(route = "personInfo") {
            PersonInfo(navHostController = navController, screenWidth = screenWidth, screenHeight = screenHeight, viewModelB = viewModelB)
        }

        composable(route = "uploadFile") {
            UploadFile(screenWidth = screenWidth, screenHeight = screenHeight , navHostController = navController , viewModelB = viewModelB)
        }

        composable(route = "chooseBetweenSingInUp"){
            ChooseBetweenSingInUp( navHostController = navController)
        }

        composable(route = "ThanksScreen"){
            ThanksScreen( navHostController = navController ,viewModelB = viewModelB)
        }

        composable(route = "ChooseBetweenTraderClient"){
            ChooseBetweenTraderClient( navHostController = navController)
        }

        composable(route = "singInTrader"){
            SignInTrader(navHostController = navController , context = context , viewModelB)
        }

    }
}


