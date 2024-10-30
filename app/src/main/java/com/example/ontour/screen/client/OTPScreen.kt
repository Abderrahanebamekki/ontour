package com.example.ontour.screen.client

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ontour.R
import com.example.ontour.screen.OTPViewModel
import com.example.ontour.screen.rememberImeState
import kotlinx.coroutines.delay

@Composable
fun OTPInputScreen(
    screenWidth: Dp,
    screenHeight: Dp,
    navHostController: NavHostController,
    viewModel: OTPViewModel
) {
    val initialTime = 120000
    var timeLeft by remember { mutableStateOf(initialTime) }

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    // Timer logic
    LaunchedEffect(key1 = timeLeft) {
        while (timeLeft > 0) {
            delay(1000) // 1 second delay
            timeLeft -= 1000
        }
    }

    fun resetTimer() {
        timeLeft = initialTime
    }

    // FocusRequesters for each TextField
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    val focusRequester5 = remember { FocusRequester() }
    val focusRequester6 = remember { FocusRequester() }

    // OTP input states
    var otp1 by remember { mutableStateOf("") }
    var otp2 by remember { mutableStateOf("") }
    var otp3 by remember { mutableStateOf("") }
    var otp4 by remember { mutableStateOf("") }
    var otp5 by remember { mutableStateOf("") }
    var otp6 by remember { mutableStateOf("") }

    // Wrapping the content in RTL layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .background(Color.White)
                .verticalScroll(scrollState)
                .windowInsetsPadding(WindowInsets.ime),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                colorFilter = ColorFilter.tint(Color(0xff00B5E2)),
                modifier = Modifier.size(180.dp)
            )

            Text(
                text = "التحقق من رمز OTP",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "أدخل رمز التحقق الذي أرسلناه للتو إلى رقمك",
                color = Color.Black
            )

            // OTP Inputs
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                OtpTextField(otp6, { otp6 = it }, focusRequester6, focusRequester1, focusRequester5 ,true)
                OtpTextField(otp5, { otp5 = it }, focusRequester5, focusRequester6, focusRequester4)
                OtpTextField(otp4, { otp4 = it }, focusRequester4, focusRequester5, focusRequester3)
                OtpTextField(otp3, { otp3 = it }, focusRequester3, focusRequester4, focusRequester2)
                OtpTextField(otp2, { otp2 = it }, focusRequester2, focusRequester3, focusRequester1)
                OtpTextField(otp1, { otp1 = it }, focusRequester1, focusRequester2,null)
            }

            // Display the timer
            Text(
                text = "الوقت المتبقي: ${String.format("%02d:%02d", timeLeft / 60000, (timeLeft / 1000) % 60)}",
                fontSize = 20.sp,
                color = Color.Black
            )

            // Resend button
            TextButton(onClick = {
                resetTimer()
                viewModel.sendOtp("213541806217") // Call your viewModel method to resend the OTP
            }) {
                Text(text = "إعادة إرسال", color = Color(0xff00B5E2), fontSize = 25.sp)
            }

            Spacer(modifier = Modifier.height(screenHeight * 3 / 8))
            val success = remember {
                mutableStateOf(false)
            }
             LaunchedEffect(key1 = success.value) {
                 if (success.value){
                     navHostController.navigate("home")
                 }
             }
            // Continue button and error handling
            var error by remember { mutableStateOf("") }
            Button(
                onClick = {
                    val otp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6
                    if (otp.isNotEmpty()) {
                        viewModel.verifyPin(otp) { isSuccess ->
                            if (isSuccess) {
                                Log.d("isSuccess", "OTPInputScreen:$isSuccess ")
                                success.value = isSuccess
                            } else {
                                error = "الرمز خاطئ"
                                otp1 = ""
                                otp2 = ""
                                otp3 = ""
                                otp4 = ""
                                otp5 = ""
                                otp6 = ""
                            }
                        }
                    } else {
                        error = "يرجى إدخال الرمز"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xff00B5E2)
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "استمرار")
            }

            if (error.isNotEmpty()) {
                Toast.makeText(LocalContext.current, error, Toast.LENGTH_LONG).show()
                error = ""
            }
        }
    }
}


@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    nextFocusRequester: FocusRequester,
    prevFocusRequester: FocusRequester?,
    last: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length == 1) {
                onValueChange(it)
                if (!last) nextFocusRequester.requestFocus()
            } else {
                onValueChange(it)
            }
        },
        modifier = Modifier
            .width(55.dp)
            .height(55.dp)
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Backspace) {
                    if (value.isEmpty()) {
                        prevFocusRequester?.requestFocus() // Go to the previous field
                    }
                }
                false
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.Black,
            cursorColor = Color.Black,
            unfocusedLabelColor = Color.DarkGray,
            focusedLabelColor = Color.Black
        )
    )
}






