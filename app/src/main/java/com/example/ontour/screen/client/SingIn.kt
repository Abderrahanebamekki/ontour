package com.example.ontour.screen.client

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ontour.R
import com.example.ontour.screen.OTPViewModel
import com.example.ontour.screen.rememberImeState
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun SingIn(screenWidth: Dp, screenHeight: Dp, navHostController: NavHostController , viewModel: OTPViewModel) {
    // Set status bar color
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color(0xff00B5E2))

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 10.dp)
                .verticalScroll(scrollState)
                .windowInsetsPadding(WindowInsets.ime),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val singInNumber = remember { mutableStateOf("") }

            val imeState = rememberImeState()
            val scrollState = rememberScrollState()

            LaunchedEffect(key1 = imeState.value) {
                if (imeState.value) {
                    scrollState.scrollTo(scrollState.maxValue)
                }
            }

            val id = viewModel.applicationIdL.collectAsState()
            LaunchedEffect(key1 = viewModel.applicationIdL.collectAsState().value) {
                if (id.value.isNotEmpty()){
                    viewModel.createMessageTemplate()
                }
            }

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "الشعار",
                colorFilter = ColorFilter.tint(Color(0xff00B5E2)), // Adjusted color for logo
                modifier = Modifier.size(180.dp) // Adjust size as needed
            )

            Column {
                Text(
                    text = "تسجيل الدخول",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
                Text(
                    text = "أدخل رقم هاتفك لمتابعة التسجيل",
                    color = Color.Black,
                    fontWeight = FontWeight.W500,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = singInNumber.value,
                    onValueChange = { singInNumber.value = it },
                    label = { Text("رقم الهاتف") },
                    placeholder = { Text("أدخل رقم الهاتف") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        unfocusedLabelColor = Color.DarkGray,
                        focusedLabelColor = Color.Black
                    )
                )
                val error = remember {
                    mutableStateOf("")
                }
                Spacer(modifier = Modifier.height(screenHeight * 3 / 6))

                Button(
                    onClick = {

                        if(singInNumber.value.isNotEmpty()){
                            val alCode = "213"
                            val phoneNum = singInNumber.value.toInt()
                            val phoneNumS = alCode + phoneNum.toString()
                            Log.d("phoneNumber", "SingIn: $phoneNumS ")
//                            viewModel.createMessageTemplate()
                            viewModel.sendOtp(phoneNumS)
                            navHostController.navigate(route = "verificationCode")
                        }else{
                            error.value = "enter you phone please"
                        }


                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color(0xff00B5E2)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = "متابعة")
                }
                if (error.value.isNotEmpty()){
                    Toast.makeText(LocalContext.current , error.value, Toast.LENGTH_LONG ).show()
                }

            }
        }
    }
}
