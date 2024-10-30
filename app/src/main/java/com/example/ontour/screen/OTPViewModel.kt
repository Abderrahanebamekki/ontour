package com.example.ontour.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.math.log
import kotlin.properties.Delegates

class OTPViewModel():ViewModel() {
    // Private mutable state flow for internal use
    private val _pinId = MutableStateFlow("")

    // Expose as a read-only flow using asStateFlow to prevent external modification
    val pinId: StateFlow<String> = _pinId.asStateFlow()

    private val _applicationIdL = MutableStateFlow("")
    val applicationIdL: StateFlow<String> = _applicationIdL

    private val _messageId = MutableStateFlow("")
    val messageId: StateFlow<String> = _messageId


    suspend fun createApplication() {
        withContext(Dispatchers.IO){
            val client = OkHttpClient()

            // Set the media type as JSON
            val mediaType = "application/json".toMediaTypeOrNull()

            // JSON body for the request
            val body = """
            {
                "name": "2fa test application",
                "enabled": true,
                "configuration": {
                    "pinAttempts": 10,
                    "allowMultiplePinVerifications": true,
                    "pinTimeToLive": "15m",
                    "verifyPinLimit": "1/3s",
                    "sendPinPerApplicationLimit": "100/1d",
                    "sendPinPerPhoneNumberLimit": "10/1d"
                }
            }
            """.trimIndent()
                .toRequestBody(mediaType)



            // Request to the Infobip API
            val request = Request.Builder()
                .url("https://8kkxx9.api.infobip.com/2fa/2/applications")
                .post(body)
                .addHeader("Authorization", "App d19b9b22768875590b2d137763c6a498-f0704420-a8a5-468d-b9d3-5898f761cbd4")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            try {
                val response = client.newCall(request).execute() // Synchronous call inside coroutine
                response.use {
                    if (!response.isSuccessful) {
                        Log.d("API_ERROR", "createApplication: request not successful - ${it.message}")
                    } else {
                        val responseBody = response.body?.string()
                        Log.d("API_SUCCESS", "createApplication: response - $responseBody")

                        // Parse and extract the Application ID from the response
                        // Assuming the response contains the application ID field like `applicationId`
                        val json = responseBody?.let { JSONObject(it) }
                        val applicationId = json?.optString("applicationId")
                        if (applicationId != null && applicationId.isNotEmpty()) {
                            _applicationIdL.emit(applicationId)

                            Log.d("APP_ID", "createApplication:  ${_applicationIdL.value}")
                        } else {
                            Log.d("API_ERROR", "Application ID not found in the response")
                        }
                    }
                }
            } catch (e: IOException) {
                Log.d("API_EXCEPTION", "createApplication: exception occurred - $e")
            }
        }
    }


    suspend fun createMessageTemplate() {
        withContext(Dispatchers.IO) {

            val client = OkHttpClient().newBuilder().build()

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = """
        {
            "pinType": "NUMERIC",
            "messageText": "رمز التحقق {{pin}}",
            "pinLength": 6,
            "senderId": "ServiceSMS"
        }
        """.trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://8kkxx9.api.infobip.com/2fa/2/applications/${_applicationIdL.value}/messages")
                .post(body)
                .addHeader("Authorization", "App d19b9b22768875590b2d137763c6a498-f0704420-a8a5-468d-b9d3-5898f761cbd4")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            Log.d("enterToTem", "createMessageTemplate: Request prepared")

            try {
                val response = client.newCall(request).execute()
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        val json = responseBody?.let { JSONObject(it) }
                        val messageId = json?.optString("messageId")

                        if (messageId != null && messageId.isNotEmpty()) {
                            _messageId.emit(messageId)

                            Log.d("messageID", "createApplication:  ${_messageId.value}")
                        } else {
                            Log.d("API_ERROR", "Application ID not found in the response")
                        }

                        Log.d("messageId", "createMessageTemplate: Success! $responseBody ")
                    } else {
                        Log.d("API_Error","API error code: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                Log.d("API_Error", "Exception: ${e.message}")
            }
        }
    }






    fun sendOtp(phoneNumber: String){
        viewModelScope.launch (Dispatchers.IO){
            val client = OkHttpClient()


            val mediaType = "application/json".toMediaTypeOrNull()
            val body = """
    {
        "applicationId": "${_applicationIdL.value}",
        "messageId": "${_messageId.value}",
        "from": "447491163443",
        "to": "$phoneNumber"
    }
    """.trimIndent()
                .toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://8kkxx9.api.infobip.com/2fa/2/pin")
                .post(body)
                .addHeader("Authorization", "App d19b9b22768875590b2d137763c6a498-f0704420-a8a5-468d-b9d3-5898f761cbd4")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            try {
                val response = client.newCall(request).execute() // Synchronous call inside coroutine
                response.use {
                    if (!response.isSuccessful) {
                        Log.d("problem", "sendOtp: not successful")
                    } else {
                        val responseBody = response.body?.string()
                        Log.d("successPhone", "sendOtp: $responseBody ")
                        val json = responseBody?.let { it1 -> JSONObject(it1) }

                        // Extract pinId from the response
                        val pinId = json?.optString("pinId")
                        if (pinId != null) {
                            if (pinId.isNotEmpty()) {
                                Log.d("success", "sendOtp: $pinId ")
                                _pinId.emit(pinId)
                            } else {

                            }
                        } else {
                            Log.d("problem", "sendOtp: pinId = null")
                        }
                    }
                }
            } catch (e: IOException) {
                Log.d("problems", "sendOtp: ${e.message}")
            }
        }
    }

    fun verifyPin(pinCode: String , onResult:(Boolean)->Unit) {
        Log.d("verify", "verifyPin: ")
        // Initialize OkHttpClient
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient().newBuilder().build()


            val mediaType = "application/json".toMediaTypeOrNull()
            val body = """
    {
        "pin": "${pinCode}"
    }
    """.trimIndent()
                .toRequestBody(mediaType)

            // Build the request
            val request = Request.Builder()
                .url("https://8kkxx9.api.infobip.com/2fa/2/pin/${_pinId.value}/verify")
                .post(body)
                .addHeader("Authorization", "App d19b9b22768875590b2d137763c6a498-f0704420-a8a5-468d-b9d3-5898f761cbd4")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            // Execute the request within the viewModelScope
            try {
                val response = client.newCall(request).execute()
                // Use the response body as needed
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        val json = responseBody?.let { JSONObject(it) }
                        val verified = json?.optString("verified")

                        if (verified != null) {
                            onResult(verified.toBoolean())

                            Log.d("messageID", "createApplication:  ${_messageId.value}")
                        } else {
                            Log.d("API_ERROR", "Application ID not found in the response")
                        }
                        Log.d("APISuccess", "Response: $responseBody")
                    } else {
                        // Log the error or handle it accordingly
                        Log.d("APIError", "API error code: ${it.code}")
                    }
                }
            } catch (e: Exception) {
                // Log the exception or handle it accordingly
                Log.d("API_Error", "Exception: ${e.message}")
            }
        }
    }
}