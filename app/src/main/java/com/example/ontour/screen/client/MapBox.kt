package com.example.ontour.screen.client

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import kotlin.math.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import com.example.ontour.R
import com.example.ontour.screen.BusinessViewModel
import com.example.ontour.screen.ProductD

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


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    point: Point = Point.fromLngLat(5.5555 , 5.3333),
    viewModelB : BusinessViewModel,
    navHostController: NavHostController
) {


    val x = remember { mutableStateOf(100) }
    val context = LocalContext.current



    val marker = remember(context) {
        val drawable = context.getDrawable(R.drawable.marker)!!
        val originalBitmap = drawable.toBitmap()

        val desiredWidth = x.value
        val desiredHeight = x.value

        Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, false)
    }

    val markerS = remember(context) {
        val drawable = context.getDrawable(R.drawable.marker_s)!!
        val originalBitmap = drawable.toBitmap()

        val desiredWidth = x.value
        val desiredHeight = x.value

        Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, false)
    }

    val pointAnnotationManager: MutableState<PointAnnotationManager?> = remember {
        mutableStateOf(null)
    }
    val clickedPoint: MutableState<Point?> = remember {
        mutableStateOf(null)
    }
    val routeGeoJson = remember { mutableStateOf<Feature?>(null) }


    LaunchedEffect(key1 = point , key2 = clickedPoint.value) {
        Log.d("hello2", "here .....................")
        Log.d("time0", "MapBox:${routeGeoJson.value} ")
        // Fetch the route and create a CustomFeature
        Log.d("getRouteFeature", "Home: ${point} ///  ${clickedPoint.value}")
        if (point!= null && clickedPoint.value !=null){
            val client = OkHttpClient()
            val url =
                "https://api.mapbox.com/directions/v5/mapbox/driving/${point.longitude()},${point.latitude()};${clickedPoint.value!!.longitude()},${clickedPoint.value!!.latitude()}?geometries=geojson&access_token=sk.eyJ1IjoiYWJkb3VibWsiLCJhIjoiY20xZmNjbW90MDNnNjJyc2E2Nnk0MjI3OCJ9.OvRZWJ05BYoP_omAV3jRrA"
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

    val service = viewModelB.service.collectAsState()



    val counter = remember {
        mutableStateOf(0)
    }
    val showDialog = remember { mutableStateOf(false) }
    val products = remember {
        mutableStateOf<ProductD>(ProductD(100 , "jefj"))
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
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

                // Handle other updates, such as marker placement

                // Place markers for all points in the list
                for (ser in service.value) {
                    Log.d("geographicalPoint", "MapBox: ${ser.geographicalPoint.longitude()} ${ser.geographicalPoint.latitude()}")
                    val point1 = ser.geographicalPoint
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point1)
                        .withIconImage(markerS)


                    pointAnnotationManager.value?.create(pointAnnotationOptions)
                    mapView.mapboxMap
                        .flyTo(CameraOptions.Builder().zoom(13.0).build())
                }

                pointAnnotationManager.let {
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(marker)

                    it.value?.create(pointAnnotationOptions)
                    mapView.mapboxMap.flyTo(
                        CameraOptions.Builder()
                            .zoom(13.0)
                            .center(point)
                            .build()
                    )
                }


                pointAnnotationManager.value?.addClickListener { annotation ->


                    Toast.makeText(context, "Marker clicked: ${annotation.point.coordinates()}", Toast.LENGTH_SHORT).show()

                    val longitude = annotation.point.longitude()
                    val latitude = annotation.point.latitude()
                    navHostController.navigate("StoreInfo/$longitude/$latitude")

                    true
                }

            },
            modifier = modifier
        )



    }


}








fun haversineDistance(point1: Point, point2: Point): Double {
    val R = 6371e3 // Earth's radius in meters

    val lat1 = point1.latitude() * PI / 180
    val lat2 = point2.latitude() * PI / 180
    val deltaLat = (point2.latitude() - point1.latitude()) * PI / 180
    val deltaLon = (point2.longitude() - point1.longitude()) * PI / 180

    val a = sin(deltaLat / 2).pow(2) +
            cos(lat1) * cos(lat2) *
            sin(deltaLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c // Distance in meters
}

fun removeRoute(style: Style , x: Int){
    style.removeStyleLayer("route-layer$x")
}

//                    mapView.mapboxMap.addOnMapClickListener { latLng ->
//                        val selectedPoint = Point.fromLngLat(latLng.longitude(), latLng.latitude())
//                        Log.d("point", "MapBox: $selectedPoint ")
//                        onPlaceSelected(selectedPoint) // Call the callback with the selected point
//
//                        // Place a marker at the selected location
//                        pointAnnotationManager.value?.apply {
//                            val pointAnnotationOptions = PointAnnotationOptions()
//                                .withPoint(selectedPoint)
//                                .withIconImage(markerS)
//                            create(pointAnnotationOptions)
//                        }
//                        true
//                    }




