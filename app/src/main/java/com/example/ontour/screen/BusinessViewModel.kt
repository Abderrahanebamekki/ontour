package com.example.ontour.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.time.LocalTime

class BusinessViewModel() :ViewModel() {

    var _idP = MutableStateFlow(0)
    var idP = _idP.asStateFlow()

    var _responseData = MutableStateFlow<JSONObject>(JSONObject())
    var responseData = _responseData.asStateFlow()

    var _idGP = MutableStateFlow("")
    var idGP = _idGP.asStateFlow()

    val idS : MutableState<Int> = mutableStateOf(0)

    val _idSI = MutableStateFlow(0)
      val idSI = _idSI.asStateFlow()

    val serviceName :MutableState<String> = mutableStateOf("")
    val description :MutableState<String> = mutableStateOf("")

    private val _serviceCategories = MutableStateFlow<List<ServiceCategory>>(emptyList())
    val serviceCategories = _serviceCategories.asStateFlow()

     val selectedPoints = mutableStateOf<List<Point>>(emptyList())



    private val _products = MutableStateFlow<List<ProductD>>(emptyList())
    val products = _products.asStateFlow()

    /////////////////////////////////////////////////////////////////

    var name = mutableStateOf("")
        private set

    var surname = mutableStateOf("")
        private set

    var phoneNumber = mutableStateOf("")
        private set

    var email = mutableStateOf("")
        private set

    var longitude = mutableStateOf(0.0)
        private set

    var latitude = mutableStateOf(0.0)
        private set

    // Functions to update each mutable state
    fun updateName(newName: String) {
        name.value = newName
    }

    fun updateSurname(newSurname: String) {
        surname.value = newSurname
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        phoneNumber.value = newPhoneNumber
    }

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updateLongitude(newLongitude: Double) {
        longitude.value = newLongitude
    }

    fun updateLatitude(newLatitude: Double) {
        latitude.value = newLatitude
    }

    fun postMerchantData(
            firstName: String,
            lastName: String,
            email: String,
            phoneNumber: String,
            isActive: Boolean = true
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                val client = OkHttpClient().newBuilder().build()


                val body = """
                {
                    "first_name": "$firstName",
                    "last_name": "$lastName",
                    "email": "$email",
                    "phone_number": "$phoneNumber",
                    "is_active": $isActive,
                    "password": ""
                }
            """.trimIndent()

                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = body.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("https://ontour-50477509b2e9.herokuapp.com/api/merchants/")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                try {
                    // Execute the request synchronously
                    val response: Response = client.newCall(request).execute()

                    // Use the response
                    response.use {
                        if (it.isSuccessful) {
                            val responseBody = it.body?.string()
                            val json = responseBody.let {
                                JSONObject(it)
                            }

                            _responseData.value = json
                        } else {
                            Log.d("Error", "postMerchantData: ${it.code}")
                        }
                    }
                } catch (e: IOException) {
                    Log.d("Error", "postMerchantData: ${e.message}")
                }
            }
        }


    fun postServiceData(
        id_card:String ,
        autorization_of_register_of_commerce : String ,
        registre_de_commerce :String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("serviceData", "postServiceData: ")
            val client = OkHttpClient().newBuilder().build()

                val body = """
                {
    "link_facebook": "",
    "link_instagram": "",
    "picture_link": "https://example.com/another-picture.jpg",
    "service_name": "${serviceName.value}",
    "service_category": ${idS.value},
    "merchant": {
        "first_name": "${name.value}",
        "last_name": "${surname.value}",
        "email": "${email.value}",
        "phone_number": "${phoneNumber.value}",
        "is_active": ${false}
    },
    "geographical_points": {
        "latitude": ${latitude.value},
        "longitude": ${longitude.value}
    },
    "is_open": ${false},
    "description": "${description.value}",
    "id_card":"${id_card}",
    "registre_de_commerce": "${registre_de_commerce}",
    "autorization_of_register_of_commerce": "${autorization_of_register_of_commerce}",
    "uploaded_at": ${null}
}


       """.trimIndent()


                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = body.toRequestBody(mediaType)


                val request = Request.Builder()
                    .url("https://ontour-50477509b2e9.herokuapp.com/api/services/")
                    .post(requestBody)
                    .addHeader("Content-Type", "MultiPartParser")
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    response.use {
                        if (it.isSuccessful) {
                            val responseBody = it.body?.string()
                            val json = responseBody.let {
                                JSONObject(it)
                            }

                            _idP.value = json.getJSONObject("merchant").getInt("id")

                            Log.d("API_Success", "Service data posted successfully: ${_idP.value}")
                        } else {
                            Log.d("API_Error", "Error posting service data: ${it.code}")
                        }
                    }
                } catch (e: Exception) {
                    Log.d("API_Exception", "Exception: ${e.message}")
                }
            }

    }


     fun sendPassword(){
         viewModelScope.launch(Dispatchers.IO) {
             val client = OkHttpClient().newBuilder().build()


             val request = Request.Builder()
                 .url("https://ontour-50477509b2e9.herokuapp.com/api/verify-merchant/${_idP.value}/")
                 .get()
                 .build()

             try {
                 // Execute the request synchronously
                 val response: Response = client.newCall(request).execute()

                 // Use the response
                 response.use {
                     if (it.isSuccessful) {
                         val responseBody = it.body?.string()
                         Log.d("responseBody", "postMerchantData: ${it.code}")
                         val json = responseBody.let {
                             JSONObject(it)
                         }
                     } else {
                         Log.d("Error", "postMerchantData: ${it.code}")
                     }
                 }
             } catch (e: IOException) {
                 Log.d("Error", "postMerchantData: ${e.message}")
             }
         }
     }

    fun fetchServiceCategories() {
        viewModelScope.launch (Dispatchers.IO){
            val client = OkHttpClient().newBuilder().build()
            val request = Request.Builder()
                .url("https://ontour-50477509b2e9.herokuapp.com/api/service-categories/")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody.let {
                        val jsonArray = JSONArray(it)
                        val serviceCategoryList = mutableListOf<ServiceCategory>()

                        // Loop through the JSONArray to create a list of ServiceCategory
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val serviceCategory = ServiceCategory(
                                id = jsonObject.getInt("id"),
                                category_name = jsonObject.getString("category_name")
                            )
                            Log.d("category", "fetchServiceCategories: $serviceCategory")
                            serviceCategoryList.add(serviceCategory)
                        }

                        _serviceCategories.emit(serviceCategoryList)
                    }



                } else {
                    Log.e("API_ERROR", "Error: ${response.code} - ${response.message}")
                    _serviceCategories.value = emptyList() // Handle error case
                }
            } catch (e: IOException) {
                Log.e("API_ERROR", "Exception: ${e.message}")
                _serviceCategories.value = emptyList() // Handle exception case
            }

        }
    }

    fun addGeographicalPoint(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient().newBuilder().build()

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = """
        {
            "latitude": $latitude,
            "longitude": $longitude
        }
        """.trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://ontour-50477509b2e9.herokuapp.com/api/geographical-points/")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            try {
                val response = client.newCall(request).execute()
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        Log.d("GeographicalPoint", "Success: $responseBody")

                        // Parse the response body to extract the ID
                        responseBody?.let {
                            val jsonObject = JSONObject(it)
                            val id = jsonObject.optString("id")
                            _idGP.emit(id)
                            Log.d("GeographicalPoint", "Geographical Point ID: $id")
                        }
                    } else {
                        Log.d("API_Error", "Error code: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                Log.d("API_Exception", "Exception: ${e.message}")
            }
        }
    }

//    suspend fun getGeograficalPoint(){
//        val client = OkHttpClient()
//        val url = "https://ontour-50477509b2e9.herokuapp.com/api/geographical-points/"
//        val request = Request.Builder().url(url).build()
//
//        withContext(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                if (response.isSuccessful) {
//                    val responseBody = response.body?.string()
//                    if (responseBody != null) {
//                        val jsonArray = JSONArray(responseBody)
//                        val pointList = mutableListOf<Point>()
//                        for (i in 0 until jsonArray.length()) {
//                            val jsonObject = jsonArray.getJSONObject(i)
//                            val attitude = jsonObject.getDouble("latitude")
//                            val longitude = jsonObject.getDouble("longitude")
//
//                            val point = Point.fromLngLat(longitude, attitude)
//                            pointList.add(point)
//                        }
//
//                        _selectedPoints.value = pointList
//
//                    } else {
//                        Log.d("point", "Home: Problem with response body.")
//                    }
//                } else {
//                    Log.d("point", "Home: Response not successful.")
//                }
//            } catch (e: IOException) {
//                Log.e("point", "Home: Network request failed.", e)
//            } catch (e: JSONException) {
//                Log.e("point", "Home: JSON parsing failed.", e)
//            }
//        }
//    }

    val _service = MutableStateFlow<List<ServiceInfo>>(emptyList())
    val service = _service.asStateFlow()

    val _productName = MutableStateFlow<List<String>>(emptyList())
    var productName = _productName.asStateFlow()


     fun searchProducts(query: String) {
        val client = OkHttpClient()
        val url = "https://ontour-50477509b2e9.herokuapp.com/api/search/?product_name=$query"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Content-Type", "application/json")
            .build()

        viewModelScope.launch (Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonArray = JSONArray(responseBody)
                        val names = mutableListOf<String>()
                        val services = mutableListOf<ServiceInfo>()

                        for (i in 0 until jsonArray.length()) {
                            val productObject = jsonArray.getJSONObject(i)

                            // Get the product name and add it to the names list
                            val productName = productObject.getString("product_name")
                            names.add(productName)

                            // Log the product name
                            Log.d("productName", "searchProducts: $productName")

                            // Iterate through each service under the product
                            val servicesArray = productObject.getJSONArray("services")
                            for (j in 0 until servicesArray.length()) {
                                val serviceObject = servicesArray.getJSONObject(j)

                                // Parse the geographical point
                                val geographicalPointObject = serviceObject.getJSONObject("geographical_point")

                                val point = Point.fromLngLat( geographicalPointObject.getDouble("longitude") ,geographicalPointObject.getDouble("latitude") )

                                // Create a ServiceInfo object for each service
                                val serviceInfo = ServiceInfo(
                                    name = serviceObject.getString("name"),
                                    geographicalPoint = point,
                                    linkFacebook = serviceObject.getString("link_facebook"),
                                    linkInstagram = serviceObject.getString("link_instagram"),
                                    pictureLink = serviceObject.getString("picture_link"),
                                    isOpen = serviceObject.getBoolean("is_open"),
                                    description = serviceObject.getString("description")
                                )

                                // Add the service to the services list
                                services.add(serviceInfo)
                            }
                        }

// Update the LiveData or state variables
                        _productName.value = names


                    } else {
                        Log.d("point", "Home: Problem with response body.")
                    }
                } else {
                    Log.d("point", "Home: Response not successful.")
                }
            } catch (e: IOException) {
                Log.e("point", "Home: Network request failed.", e)
            } catch (e: JSONException) {
                Log.e("point", "Home: JSON parsing failed.", e)
            }
        }


    }

    fun searchService(query: String) {
        val client = OkHttpClient()
        val url = "https://ontour-50477509b2e9.herokuapp.com/api/search/?product_name=$query"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Content-Type", "application/json")
            .build()

        viewModelScope.launch (Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonArray = JSONArray(responseBody)
                        val names = mutableListOf<String>()
                        val services = mutableListOf<ServiceInfo>()

                        for (i in 0 until jsonArray.length()) {
                            val productObject = jsonArray.getJSONObject(i)

                            // Get the product name and add it to the names list
                            val productName = productObject.getString("product_name")
                            names.add(productName)

                            // Log the product name
                            Log.d("productName", "searchProducts: $productName")

                            // Iterate through each service under the product
                            val servicesArray = productObject.getJSONArray("services")
                            for (j in 0 until servicesArray.length()) {
                                val serviceObject = servicesArray.getJSONObject(j)

                                // Parse the geographical point
                                val geographicalPointObject = serviceObject.getJSONObject("geographical_point")

                                val point = Point.fromLngLat( geographicalPointObject.getDouble("longitude") ,geographicalPointObject.getDouble("latitude") )

                                // Create a ServiceInfo object for each service
                                val serviceInfo = ServiceInfo(
                                    name = serviceObject.getString("name"),
                                    geographicalPoint = point,
                                    linkFacebook = serviceObject.getString("link_facebook"),
                                    linkInstagram = serviceObject.getString("link_instagram"),
                                    pictureLink = serviceObject.getString("picture_link"),
                                    isOpen = serviceObject.getBoolean("is_open"),
                                    description = serviceObject.getString("description")
                                )

                                // Add the service to the services list
                                services.add(serviceInfo)
                            }
                        }

// Update the LiveData or state variables
                        _service.value = services


                    } else {
                        Log.d("point", "Home: Problem with response body.")
                    }
                } else {
                    Log.d("point", "Home: Response not successful.")
                }
            } catch (e: IOException) {
                Log.e("point", "Home: Network request failed.", e)
            } catch (e: JSONException) {
                Log.e("point", "Home: JSON parsing failed.", e)
            }
        }


    }

    fun postProductData(
        serviceId: Int,
        productId: Int,
        quantity: Int,
        price: Double
    ) {
        val client = OkHttpClient()

        viewModelScope.launch (Dispatchers.IO){
            try {


                Log.d("postProdut", "postProductData: $serviceId // $productId // $quantity  // $price")

                // Manually create the JSON string for the request body
                val body = """
                {
                    "service_id": $serviceId,
                    "general_product_ids": $productId,
                    "quantity": $quantity,
                    "price": $price
                }
            """.trimIndent()

                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = body.toRequestBody(mediaType)


                val request = Request.Builder()
                    .url("https://ontour-50477509b2e9.herokuapp.com/api/spb/")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                // Create the request with JSON body and correct content type
                // Execute the request
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("API_SUCCESS", "Response: $responseBody")
                } else {
                    val errorBody = response.body?.string()
                    Log.e("API_ERROR", "Error: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}")
            }
        }
    }



    fun getProductByCategories(
        id : Int
    ){
        val client = OkHttpClient()
        val url = "https://ontour-50477509b2e9.herokuapp.com/api/scp/$id/"
        val request = Request.Builder().url(url).build()

        Log.d("hellooooooooooooo", "getProductByCategories:  ")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val json = responseBody.let {
                            JSONObject(it)
                        }
                        val productList = mutableListOf<ProductD>()
                        for (i in 0 until json.getJSONArray("general_products").length()) {
                            val jsonObject = json.getJSONArray("general_products").getJSONObject(i)



                            val product = ProductD(jsonObject.getInt("id") , jsonObject.getString("product_name"))
                            productList.add(product)
                        }

                        _products.value = productList
                        Log.d("product", "getProductByCategories: ${_products.value[0].product_name} ")

                    } else {
                        Log.d("point", "Home: Problem with response body.")
                    }
                } else {
                    Log.d("point", "Home: Response not successful.")
                }
            } catch (e: IOException) {
                Log.e("point", "Home: Network request failed.", e)
            } catch (e: JSONException) {
                Log.e("point", "Home: JSON parsing failed.", e)
            }
        }
    }

    fun updateService(
        isStoreOpen :Boolean ,
    name : String ,
    surname: String  ,
    personalEmail: String  ,
    socialMediaLinks1: String  ,
    socialMediaLinks: String ,
    storeMessage: String ,
    businessName: String ,
    serviceCategory: Int,
        phoneNumber: String,
        longitude: Double,
        latitude: Double ,
        idS : Int
    ){

        val client = OkHttpClient()
        val url = "https://ontour-50477509b2e9.herokuapp.com/api/services/$idS/"
        viewModelScope.launch(Dispatchers.IO){

            // Create JSON object with the data to send
            val json = JSONObject().apply {
                put("link_facebook", "$socialMediaLinks")
                put("link_instagram", "$socialMediaLinks1")
                put("picture_link", "https://example.com/another-picture.jpg")
                put("service_name", "$businessName")
                put("service_category", serviceCategory)
                put("merchant", JSONObject().apply {
                    put("first_name", "$name")
                    put("last_name", "$surname")
                    put("email", "$personalEmail")
                    put("phone_number", "$phoneNumber")
                    put("is_active", true)
                })
                put("geographical_points", JSONObject().apply {
                    put("latitude", latitude)
                    put("longitude", longitude)
                })
                put("is_open", isStoreOpen)
                put("description", "$storeMessage")
                put("id_card", "")
                put("registre_de_commerce", "")
                put("autorization_of_register_of_commerce", "")
                put("uploaded_at", "2024-10-18T15:28:06.719440Z")
            }

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .put(body)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // Handle success response
                    println("Service data updated successfully.")
                } else {
                    // Handle error response
                    Log.d("errorData", "updateService: Failed to update service data: ${response.message}")
                }
            } catch (e: IOException) {
                // Handle exception
                println("Error occurred: ${e.message}")
            }
        }
    }


    fun getServiceData(
        id: Int
    ) {
        val client = OkHttpClient()
        viewModelScope.launch(Dispatchers.IO){
            val url = "https://ontour-50477509b2e9.herokuapp.com/api/services/$id/"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // Successfully retrieved the service data
                    val responseData = response.body?.string()

                    val json = responseData.let {
                        JSONObject(it)
                    }

                    _responseData.value = json
                    println("Service Data: $responseData")
                } else {
                    // Handle the error response
                    println("Failed to retrieve service data: ${response.code}")
                }
            } catch (e: IOException) {
                // Handle exception
                println("Error occurred: ${e.message}")
            }
        }
    }

    @SuppressLint("NewApi")
    fun signInUser(
        email: String,
        password: String,
        onError: (Boolean , String) -> Unit
    ) {
        val client = OkHttpClient()

        Log.d("singin", "signInUser: ")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Create JSON body
                val jsonObject = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }
                val jsonBody = jsonObject.toString()
                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = jsonBody.toRequestBody(mediaType)

                // Build the request
                val request = Request.Builder()
                    .url("https://ontour-50477509b2e9.herokuapp.com/api/signin/")
                    .post(requestBody)
                    .build()

                // Execute the request
                val response = client.newCall(request).execute()

                // Handle response on the main thread
                withContext(Dispatchers.IO) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (response.isSuccessful && responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            if (jsonResponse.has("message")) {
                                // Success case
                                val message = jsonResponse.getString("message")
                                val userEmail = jsonResponse.getString("user_email")
                                _idSI.value = jsonResponse.getInt("service_id")
                                Log.d("sucyss", "signInUser: ${LocalTime.now()}  ${_idSI.value}")
                                onError(true , "")
                            }
                        } else if (responseBody != null) {
                            // Check for invalid email or password error
                            Log.d("sucyss76", "signInUser:ttttttttttttttt ")
                            val jsonResponse = JSONObject(responseBody)
                            if (jsonResponse.has("non_field_errors")) {
                                val errorMessage = jsonResponse.getJSONArray("non_field_errors").getString(0)
                                onError(false ,"") // Show error message from the response
                            } else {
                                onError(false ,"")
                            }
                        } else {
                            onError(false , "")
                        }

                    } else {
                        Log.d("sucyss76" , "signInUser:hhhhhhhhhhhhh ")
                        onError(false , "ايميل أو كلمة السر خاطئة")
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions (like network errors)
                withContext(Dispatchers.Main) {
                    onError(false , "")
                }
            }
        }
    }
    val _productS = MutableStateFlow<List<ProductS>>(emptyList())
    val productS = _productS.asStateFlow()

    fun getProductsForService(serviceId: Int) {
        val client = OkHttpClient()

        // Define the URL with the serviceId in the path
        val url = "https://ontour-50477509b2e9.herokuapp.com/api/service/$serviceId/products/"

        viewModelScope.launch(Dispatchers.IO){
            try {
                val request = Request.Builder()
                    .url(url)
                    .get()  // HTTP GET request
                    .build()

                // Execute the request and capture the response
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    // If the request was successful, get the response body as a string
                    val responseBody = response.body?.string()

                    // Initialize an empty list to store products
                    val products = mutableListOf<ProductS>()

                    // Parse the response body into a JSONArray
                    val jsonArray = JSONArray(responseBody)

                    // Iterate through each item in the JSONArray using a for loop
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        // Manually create a Product object from each JSONObject
                        val product = ProductS(
                            product_name = jsonObject.getString("product_name"),
                            product_category = jsonObject.getInt("product_category"),
                            quantity = jsonObject.getInt("quantity"),
                            price = jsonObject.getDouble("price")
                        )

                        // Add the product to the list
                        products.add(product)

                        // Log each product's details
                        Log.d("ProductInfo", "Product Name: ${product.product_name}")
                        Log.d("ProductInfo", "Category: ${product.product_category}")
                        Log.d("ProductInfo", "Quantity: ${product.quantity}")
                        Log.d("ProductInfo", "Price: ${product.price}")
                    }

                    _productS.value = products

                } else {
                    // Log or handle the error if the request fails
                    val errorBody = response.body?.string()
                    Log.e("API_ERROR", "Error: $errorBody")
                }
            } catch (e: Exception) {
                // Handle exceptions during the API call
                Log.e("API_ERROR", "Exception: ${e.message}")
            }
        }
    }
}
