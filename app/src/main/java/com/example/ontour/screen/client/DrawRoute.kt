package com.example.ontour.screen.client

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import com.example.ontour.R
import com.mapbox.common.location.LocationError
import com.mapbox.common.location.LocationErrorCode
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

@Composable
fun DrawRoute(modifier: Modifier = Modifier , pointD : Point , navController : NavHostController) {
    val point: MutableState<Point?> = remember {
        mutableStateOf(null)
    }
    val relaunch = remember {
        mutableStateOf(false)
    }

    val routeGeoJson = remember { mutableStateOf<Feature?>(null) }
    LaunchedEffect(key1 = point.value , key2 = pointD) {
        Log.d("hello2", "here .....................")
        Log.d("time0", "MapBox:${routeGeoJson.value} ")
        // Fetch the route and create a CustomFeature
        Log.d("getRouteFeature", "Home: ${point.value} ///  ${pointD}")
        if (point.value!= null && pointD !=null){
            val client = OkHttpClient()
            val url =
                "https://api.mapbox.com/directions/v5/mapbox/driving/${point.value!!.longitude()},${point.value!!.latitude()};${pointD!!.longitude()},${pointD!!.latitude()}?geometries=geojson&access_token=sk.eyJ1IjoiYWJkb3VibWsiLCJhIjoiY20xZmNjbW90MDNnNjJyc2E2Nnk0MjI3OCJ9.OvRZWJ05BYoP_omAV3jRrA"
            val request = Request.Builder().url(url).build()

            withContext(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        Log.d("getRouteFeature", "getRouteFeature: ..........................")
                        val json = JSONObject(response.body?.string() ?: "")
                        val routes = json.getJSONArray("routes")

                        if (routes.length() > 0) {
                            Log.d("getRouteFeature", "Home: iffffffffffffffffffffffff")
                            val route = routes.getJSONObject(0)
                            val geometry = LineString.fromJson(route.getJSONObject("geometry").toString())
                            val feature = Feature.fromGeometry(geometry)
                            routeGeoJson.value = feature
                            Log.d("getRouteFeature", "Home: ${routeGeoJson.value}")
                        } else {
                            Log.d("getRouteFeature", "Home: elselllllllllllllllllllll")
                        }
                    } else {
                        Log.d("getRouteFeature", "getRouteFeature: Response not successful.")
                    }
                } catch (e: IOException) {
                    Log.e("getRouteFeature", "getRouteFeature: Network request failed.", e)
                } catch (e: JSONException) {
                    Log.e("getRouteFeature", "getRouteFeature: JSON parsing failed.", e)
                }
            }
        }



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


    val pointAnnotationManager: MutableState<PointAnnotationManager?> = remember {
        mutableStateOf(null)
    }

    val counter = remember {
        mutableStateOf(0)
    }

    val context = LocalContext.current
    val marker = remember(context) {
        val drawable = context.getDrawable(R.drawable.marker)!!
        val originalBitmap = drawable.toBitmap()

        val desiredWidth = 100
        val desiredHeight = 100

        Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, false)
    }

    val markerS = remember(context) {
        val drawable = context.getDrawable(R.drawable.marker_s)!!
        val originalBitmap = drawable.toBitmap()

        val desiredWidth = 100
        val desiredHeight = 100

        Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, false)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(200.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = { /* Handle close action, e.g., navigate back */ }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = androidx.compose.ui.graphics.Color.Black ,
                    modifier = Modifier.size(70.dp)
                )
            }
        }
        AndroidView(
            factory = {
                MapView(it).also { mapView ->


                    Log.d("TAG", "MapBox: mapboxxxxxxxx")
                    mapView.mapboxMap.loadStyle(Style.STANDARD){
                        val annotationApi = mapView.annotations
                        pointAnnotationManager.value = annotationApi.createPointAnnotationManager()
                    }
                }
            },
            update = { mapView ->
                // Check if the routeGeoJson value is updated and not null
                val customFeature = routeGeoJson.value




                if (customFeature != null) {
                    Log.d("RouteFeature", "GeoJSON Feature: ${customFeature}")

                    // Update the style with the route source and layer
                    mapView.mapboxMap.getStyle { style ->
                        // Increment the counter before using it to generate new IDs
                        ++counter.value
                        val sourceId = "route-source${counter.value}"
                        val layerId = "route-layer${counter.value}"

                        // Check if the source already exists
                        if (style.styleSources.any { it.id == sourceId }) {
                            Log.e("Mapbox", "Source $sourceId already exists. Skipping addition.")
                        }

                        // Add GeoJSON source with a unique ID
                        style.addSource(geoJsonSource(sourceId) {
                            feature(customFeature)
                        })

                        // Add line layer with a unique ID that matches the source ID
                        style.addLayer(lineLayer(layerId, sourceId) {
                            lineColor("#000000")
                            lineWidth(5.0)
                        })

                        Log.d("RouteLayer", "Layer added successfully")
                    }


                }


                pointAnnotationManager.let {
                    val pointAnnotationOptions = pointD.let { it1 ->
                        PointAnnotationOptions()
                            .withPoint(it1)
                            .withIconImage(markerS)
                    }

                    it.value?.create(pointAnnotationOptions)
                    mapView.mapboxMap.flyTo(
                        CameraOptions.Builder()
                            .zoom(13.0)
                            .center(pointD)
                            .build()
                    )
                }



                pointAnnotationManager.let {
                    val pointAnnotationOptions = point.value?.let { it1 ->
                        PointAnnotationOptions()
                            .withPoint(it1)
                            .withIconImage(marker)
                    }

                    if (pointAnnotationOptions != null) {
                        it.value?.create(pointAnnotationOptions)
                    }
                    mapView.mapboxMap.flyTo(
                        CameraOptions.Builder()
                            .zoom(13.0)
                            .center(point.value)
                            .build()
                    )
                }




            },
            modifier = modifier
        )



    }
}