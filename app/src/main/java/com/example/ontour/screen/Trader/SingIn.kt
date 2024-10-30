package com.example.ontour.screen.Trader

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import com.example.ontour.screen.BusinessViewModel
import com.example.ontour.screen.getToken
import com.example.ontour.screen.rememberImeState
import com.example.ontour.screen.saveToken

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignInTrader(
    navHostController: NavHostController,
    context: Context ,
    viewModel: BusinessViewModel
) {
    // Remember state for phone number and password inputs
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    // Using CompositionLocalProvider to apply RTL for Arabic layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(scrollState)
                .windowInsetsPadding(WindowInsets.ime),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title for Sign-In
                Text(
                    text = "تسجيل الدخول",
                    style = TextStyle(fontSize = 24.sp, color = Color.Black)
                )

                // Phone Number Input Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("ايميل") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        unfocusedLabelColor = Color.DarkGray,
                        focusedLabelColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // Password Input Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        unfocusedLabelColor = Color.DarkGray,
                        focusedLabelColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                val success = remember {
                    mutableStateOf(false)
                }
                val idS = viewModel.idSI.collectAsState()

                val m = remember {
                    mutableStateOf("")
                }

                LaunchedEffect(key1 = success.value , key2 = idS.value) {
                    if (success.value){
                        Log.d("hereeeeeeeeee", "SignInTrader: ")
                        saveToken(context , idS.value)
                        Log.d("hereeeeeeeeee", "SignInTrader: ${getToken(context)}")
                        navHostController.navigate("homeTrader")
                    }

                }

                if (m.value.isNotEmpty()) {
                    Toast.makeText(LocalContext.current , m.value , Toast.LENGTH_LONG).show()
                    m.value = ""
                }

                // Sign-In Button
                Button(
                    onClick = {
                        if (password.isNotEmpty() && phoneNumber.isNotEmpty()){
                            viewModel.signInUser(phoneNumber , password ){it , it1 ->
                                m.value = it1
                                if (it){
                                    Log.d("singINBO", "SignInTrader: $it")
                                    success.value = it
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff00B5E2)
                    )
                ) {
                    Text(text = "تسجيل الدخول")
                }
            }
        }
    }
}