package com.example.ontour.screen.client

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mapbox.common.location.LocationError
import com.mapbox.common.location.LocationErrorCode
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.example.ontour.R
import com.example.ontour.screen.BusinessViewModel
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.example.ontour.screen.client.MapBox


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(
    navController: NavHostController,
    screenWidth: Dp,
    screenHeight: Dp,
    viewModelB : BusinessViewModel
) {
    val point: MutableState<Point?> = remember {
        mutableStateOf(null)
    }
    val relaunch = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    val textItems = viewModelB.productName.collectAsState()


    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color(0xff00B5E2))

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }



    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (!permissions.values.all { it }) {
                //handle permission denied
            }
            else {
                relaunch.value = !relaunch.value
            }
        }
    )





    val search = remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE9E0E0))
            .padding(top = screenHeight / 19)
    ) {



        point.value?.let {
            MapBox(
                point = it,
                modifier = Modifier
                    .fillMaxSize(),
                viewModelB = viewModelB,
                navHostController = navController
            )
        }
        val isAllScreen = remember {
            mutableStateOf(false)
        }

        val listIds = remember {
            mutableStateOf<List<Int>>(listOf())
        }
        Column(
            modifier = Modifier
                .height(if (isAllScreen.value) screenHeight else screenHeight * 2 / 10)
                .background(color = if (isAllScreen.value) Color(0xA4ACD3E2) else Transparent)
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .padding(vertical = 30.dp)
        ) {
            OutlinedTextField(
                value = search.value,
                onValueChange = {
                    search.value = it
                    viewModelB.searchProducts(it)
                } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = White, shape = RoundedCornerShape(30.dp))
                    .onFocusChanged {
                        isAllScreen.value = it.hasFocus
                    },
                placeholder = {
                    Text(
                        text = "search",
                        modifier = Modifier
                            .alpha(12f)
                            .align(Alignment.End),
                        color = Gray,
                        textAlign = TextAlign.Right
                    )
                },
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    textAlign = TextAlign.Right
                ),
                singleLine = true ,
                leadingIcon = {
//                    0xFF5AA2B8
                    IconButton(
                        onClick = {
                            viewModelB.searchService(search.value)
                            search.value = ""
                            isAllScreen.value = false
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .alpha(12f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            tint = Color(0xFF5AA2B8)
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            search.value = ""
                              isAllScreen.value = false
                              focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .alpha(12f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Icon",
                            tint = Color(0xFF5AA2B8)
                        )
                    }
                },
                keyboardActions = KeyboardActions(
                    onSearch = {

                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = Black,
                    disabledBorderColor = Transparent,
                    focusedBorderColor = Transparent,
                    focusedTextColor = Color.Black,
                    ),
                shape = RoundedCornerShape(30.dp) ,
            )

            val isExpanded = remember {
                mutableStateOf(false)
            }

            if (isAllScreen.value){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    items(textItems.value) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(color = White, shape = RoundedCornerShape(20.dp))
                                .clickable {
                                    search.value = ""
                                    viewModelB.searchService(item)
                                    isAllScreen.value = false
                                    focusManager.clearFocus()
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left side: Texts
                            Column(
                                modifier = Modifier

                            ) {
                                Text(
                                    text = item,
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold ,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }
                        }
//                        AnimatedVisibility(visible = isExpanded.value) {
//                            getCocaColaItems().forEach {
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 8.dp),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(
//                                        text = it.name,
//                                        color = Color.Magenta,
//                                        fontSize = 16.sp,
//                                        fontWeight = FontWeight.Bold
//                                    )
//
//                                    Image(
//                                        painter = painterResource(id = it.imageRes),
//                                        contentDescription = it.name,
//                                        modifier = Modifier.size(40.dp)
//                                    )
//                                }
//                            }
//                        }
                    }
                }


            }
        }

    }



    LaunchedEffect(search.value) {
        if (search.value!=""){
            val client = OkHttpClient()
            val url ="https://ontour-50477509b2e9.herokuapp.com/api/search/?q=${search.value}"
            val request = Request.Builder().url(url).build()

            withContext(Dispatchers.IO){

            }
        }
    }



    LaunchedEffect(key1 = relaunch) {
        try {
            val locationService: LocationService = LocationServiceFactory.getOrCreate()
            // Check if location service is available
            if (!locationService.isAvailable()) {
                // Handle the case where the location service is not available
                // Show a dialog or a snack-bar to enable the location service
                return@LaunchedEffect
            }

            // Obtain the location provider (with default or custom parameters)
            val locationProviderResult = locationService.getDeviceLocationProvider()

            if (locationProviderResult.isError) {
                val error = locationProviderResult.error
                val locationError = error?.let { LocationError(error.code, it.message) }

                if (locationError != null) {
                    when (locationError.code) {
                        LocationErrorCode.NOT_READY -> {
                            // Handle the case where the service is not ready
                        }
                        LocationErrorCode.NOT_AVAILABLE -> {
                            // Handle the case where the requested location is not available
                        }
                        LocationErrorCode.ACCESS_DENIED -> {
                            permissionRequest.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                        LocationErrorCode.FAILED_TO_DETECT_LOCATION -> {
                            // Handle the case where location detection failed
                        }
                        LocationErrorCode.COMMUNICATION_FAILURE -> {
                            // Handle communication failure with platform service
                        }
                        LocationErrorCode.NOT_SUPPORTED -> {
                            // Handle the case where the feature is not supported by the device
                        }
                        LocationErrorCode.UNKNOWN -> {
                            // Handle unknown error
                        }
                        else -> {
                            // Handle other potential errors
                        }
                    }
                }
            } else {
                val locationProvider = locationProviderResult.value
                val location = locationProvider?.getLastLocation{
                    // Update the point with the retrieved location
                    if (it != null) {
                        point.value = Point.fromLngLat(it.longitude, it.latitude)
                    }
                } // Assuming such a method exists


            }
        } catch (e: Exception) {
            // Handle unexpected exceptions
            // Show an error message or log the exception
        }
    }



}

suspend fun getRouteFeature(start: Point, end: Point, dataId: String):Feature? {
    val client = OkHttpClient()
    val url =
        "https://api.mapbox.com/directions/v5/mapbox/driving/${start.longitude()},${start.latitude()};${end.longitude()},${end.latitude()}?geometries=geojson&access_token=sk.eyJ1IjoiYWJkb3VibWsiLCJhIjoiY20wMmg0N2NjMDEwYTJ2cjEycmV6dDNxZiJ9.ImfBNR2qolsiuLH5VAjf0A"
    val request = Request.Builder().url(url).build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("rot", "getRouteFeature: ..........................")
                val json = JSONObject(response.body?.string() ?: "")
                val routes = json.getJSONArray("routes")

                if (routes.length() > 0) {
                    val route = routes.getJSONObject(0)
                    val geometry = LineString.fromJson(route.getJSONObject("geometry").toString())
                    val feature = Feature.fromGeometry(geometry)
                    return@withContext feature
                }
            } else {
                Log.d("rot", "getRouteFeature: Response not successful.")
            }
        } catch (e: IOException) {
            Log.e("rot", "getRouteFeature: Network request failed.", e)
        } catch (e: JSONException) {
            Log.e("rot", "getRouteFeature: JSON parsing failed.", e)
        }
        null
    }
}

data class CocaColaItem(val name: String, val imageRes: Int)

fun getCocaColaItems(): List<CocaColaItem> {
    return listOf(
        CocaColaItem("CANETTE", R.drawable.map), // Replace with your image resource
        CocaColaItem("1L PLASTIC", R.drawable.map), // Replace with your image resource
        CocaColaItem("2L PLASTIC", R.drawable.map), // Replace with your image resource
        CocaColaItem("1L GLASS", R.drawable.map) // Replace with your image resource
    )
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview
//@Composable
//fun prev(modifier: Modifier = Modifier) {
//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp.dp
//    val screenHeight = configuration.screenHeightDp.dp
//    Home(screenWidth , screenHeight)
//}

/*
[6.201608336006899, 36.71510266808431]
[6.201509082046812, 36.71534985454126]
[6.201458030165583, 36.71568260436824]
[6.202658737200892, 36.71401045692214]
[6.202135313108329, 36.713203041867885]
[6.202844216209769, 36.71204877748576]

* */



