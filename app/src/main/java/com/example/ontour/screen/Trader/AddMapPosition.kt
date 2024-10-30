package com.example.ontour.screen.Trader

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import com.example.ontour.R
import com.example.ontour.screen.BusinessViewModel
import com.example.ontour.screen.CustomFeature
import com.example.ontour.screen.FireBaseViewModel
import com.example.ontour.screen.Geometry
import com.mapbox.common.location.LocationError
import com.mapbox.common.location.LocationErrorCode
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

@Composable
fun AddMapPosition(modifier: Modifier = Modifier, screenWidth: Dp, screenHeight: Dp , navHostController : NavHostController , viewModelB : BusinessViewModel) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val pointAnnotationManager: MutableState<PointAnnotationManager?> = remember {
            mutableStateOf(null)
        }

        val relaunch = remember {
            mutableStateOf(false)
        }
        val point: MutableState<Point?> = remember {
            mutableStateOf(null)
        }
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
        val context = LocalContext.current
        val markerS = remember(context) {
            val drawable = context.getDrawable(R.drawable.marker_s)!!
            val originalBitmap = drawable.toBitmap()

            val desiredWidth = 100
            val desiredHeight = 100

            Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, false)
        }
        val places = remember {
            mutableStateOf<List<CustomFeature>>(emptyList())
        }
        val focusManager = LocalFocusManager.current


        val marker = remember(context) {
            val drawable = context.getDrawable(R.drawable.marker)!!
            val originalBitmap = drawable.toBitmap()

            val desiredWidth = 100
            val desiredHeight = 100

            Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, false)
        }
        val pointSearched = remember {
            mutableStateOf<Point?>(null)
        }
        val search = remember {
            mutableStateOf("")
        }
        val addPoint = remember {
            mutableStateOf<Point?>(null)
        }

        Box(modifier = modifier.padding(bottom = 20.dp)) {
            AndroidView(
                factory = {
                    MapView(it).also { mapView ->
                        mapView.mapboxMap.loadStyle(Style.STANDARD) {
                            val annotationApi = mapView.annotations
                            pointAnnotationManager.value = annotationApi.createPointAnnotationManager()
                            mapView.mapboxMap.addOnMapClickListener { latLng ->
                                val selectedPoint = Point.fromLngLat(latLng.longitude(), latLng.latitude())

                                addPoint.value = selectedPoint

                                pointAnnotationManager.value?.deleteAll()

                                val pointAnnotationOptions = PointAnnotationOptions()
                                    .withPoint(selectedPoint)
                                    .withIconImage(markerS)

                                pointAnnotationManager.value?.create(pointAnnotationOptions)
                                true
                            }
                        }
                    }
                },
                update = { mapView ->
                    pointAnnotationManager.let {
                        val pointAnnotationOptions = point.value?.let { it1 ->
                            PointAnnotationOptions()
                                .withPoint(it1)
                                .withIconImage(marker)
                        }

                        if (pointAnnotationOptions != null) {
                            it.value?.create(pointAnnotationOptions)
                            mapView.mapboxMap
                                .flyTo(CameraOptions.Builder().zoom(17.0).center(point.value).build())
                        }

                        val pointAnnotationOptions1 = pointSearched.value?.let { it1 ->
                            PointAnnotationOptions()
                                .withPoint(it1)
                                .withIconImage(marker)
                        }

                        if (pointAnnotationOptions1 != null) {
                            it.value?.create(pointAnnotationOptions1)
                            mapView.mapboxMap
                                .flyTo(CameraOptions.Builder().zoom(12.0).center(pointSearched.value).build())
                        }

                        val pointAnnotationOptions2 = addPoint.value?.let { it1 ->
                            PointAnnotationOptions()
                                .withPoint(it1)
                                .withIconImage(marker)
                        }

                        if (pointAnnotationOptions2 != null) {
                            it.value?.create(pointAnnotationOptions2)
                            mapView.mapboxMap
                                .flyTo(CameraOptions.Builder().zoom(12.0).center(addPoint.value).build())
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            val isAllScreen = remember {
                mutableStateOf(false)
            }

            Column(
                modifier = Modifier
                    .height(if (isAllScreen.value) screenHeight else screenHeight * 2 / 10)
                    .background(color = if (isAllScreen.value) Color(0xA4ACD3E2) else Color.Transparent)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp  ,top = 50.dp) // Padding adapted for RTL
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .padding(vertical = 30.dp)
            ) {
                OutlinedTextField(
                    value = search.value,
                    onValueChange = {
                        places.value = emptyList()
                        search.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White, shape = RoundedCornerShape(30.dp))
                        .onFocusChanged {
                            isAllScreen.value = it.hasFocus
                        },
                    placeholder = {
                        Text(
                            text = "البحث", // Arabic text for search
                            modifier = Modifier
                                .alpha(0.5f)
                                .align(Alignment.End),
                            color = Color.Gray,
                            textAlign = TextAlign.End // Align text for RTL
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        textAlign = TextAlign.End // Text alignment adjusted for RTL
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                isAllScreen.value = false
                                focusManager.clearFocus()
                                places.value = emptyList()
                                search.value = ""
                            },
                            modifier = Modifier.alpha(0.5f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "إغلاق", // Arabic text for Close
                                tint = Color(0xFF5AA2B8)
                            )
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            isAllScreen.value = false
                            focusManager.clearFocus()
                            places.value = emptyList()
                            search.value = ""
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        disabledBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(30.dp),
                )

                if (isAllScreen.value) {
                    places.value.forEachIndexed { index, item ->
                        Column(
                            modifier = Modifier.clickable {
                                pointSearched.value = Point.fromLngLat(
                                    places.value[index].geometry.coordinates[0],
                                    places.value[index].geometry.coordinates[1]
                                )
                                places.value = emptyList()
                                focusManager.clearFocus()
                                search.value = ""
                            }
                        ) {
                            Text(text = item.place_name, modifier = Modifier.padding(horizontal = 16.dp) , fontSize = 20.sp , fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }

            val viewModel = FireBaseViewModel()
            val x = viewModel._result1
            val y = viewModel.result2.collectAsState()
            val z = viewModel.result3.collectAsState()
            Button(
                onClick = {
                    if (addPoint.value!=null){
                        Log.d("triple", "UploadFile: ${x.value} ///// ${y.value} ///// ${z.value}")

                        viewModelB.updateLatitude(addPoint.value!!.latitude())
                        viewModelB.updateLongitude(addPoint.value!!.longitude())
                        viewModelB.postServiceData(x.value , y.value , z.value)
                        navHostController.navigate(route = "ThanksScreen")
                    }
                },
                modifier = Modifier
                    .width(screenWidth * 95/100)
                    .height(56.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff00B5E2)
                )
            ) {
                Text(
                    text = "التالي",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
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
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
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

        val client = remember { OkHttpClient() }
        var errorMessage = remember { mutableStateOf<String?>(null) }

        LaunchedEffect(key1 = search.value) {
            if (search.value != ""){
                withContext(Dispatchers.IO) {
                    try {
                        // Create the request to Mapbox Geocoding API
                        val url = "https://api.mapbox.com/geocoding/v5/mapbox.places/${search.value}.json?access_token=sk.eyJ1IjoiYWJkb3VibWsiLCJhIjoiY20xZmNjbW90MDNnNjJyc2E2Nnk0MjI3OCJ9.OvRZWJ05BYoP_omAV3jRrA"
                        val request = Request.Builder()
                            .url(url)
                            .build()

                        // Execute the request
                        val response: Response = client.newCall(request).execute()

                        // Check if the request was successful
                        if (response.isSuccessful) {
                            val body = response.body?.string() ?: ""
                            if (body != null) {
                                val json = JSONObject(body)
                                val featuresArray = json.getJSONArray("features")
                                if (featuresArray.length() > 0) {
                                    // Loop through the features array to get each feature
                                    for (i in 0 until featuresArray.length()) {
                                        val feature = featuresArray.getJSONObject(i)

                                        // Get the geometry object from the feature
                                        val geometry = feature.getJSONObject("geometry")

                                        val coordinatesArray = geometry.getJSONArray("coordinates")

                                        // Extract the longitude and latitude (assuming it's a Point)
                                        val longitude = coordinatesArray.getDouble(0)
                                        val latitude = coordinatesArray.getDouble(1)

                                        val customGeometry = Geometry(listOf(longitude , latitude))

                                        val place_name = feature.getString("place_name")

                                        val customFeature = CustomFeature(place_name = place_name, geometry = customGeometry)

                                        places.value += customFeature

                                        // Extract the longitude and latitude (assuming it's a Point)
//                                val longitude = coordinatesArray.getDouble(0)
//                                val latitude = coordinatesArray.getDouble(1)

                                        Log.d("Coordinates", "${places.value}")
                                    }
                                } else {
                                    Log.d("Coordinates", "No features found")
                                }

                            }
                        } else {
                            errorMessage.value = "Error: ${response.message}"
                        }
                    } catch (e: IOException) {
                        Log.e("SearchPlace", "Error during API call", e)
                        errorMessage.value = "Network error: ${e.message}"
                    }
                }
            }

        }
    }




}

//@Preview
//@Composable
//fun preMap(modifier: Modifier = Modifier) {
//
//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp.dp
//    val screenHeight = configuration.screenHeightDp.dp
//    AddMapPosition(Modifier,screenWidth , screenHeight)
//}