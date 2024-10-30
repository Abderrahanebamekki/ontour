package com.example.ontour.screen.Trader

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ontour.R
import com.example.ontour.screen.BusinessViewModel
import com.example.ontour.screen.rememberImeState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PersonInfo(
    modifier: Modifier = Modifier,
    screenWidth: Dp,
    screenHeight : Dp,
    navHostController: NavHostController,
    viewModelB : BusinessViewModel
    ) {


    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color(0xff00B5E2))

    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModelB.fetchServiceCategories()
    }

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    // RTL Layout for the whole screen
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(scrollState)
                .windowInsetsPadding(WindowInsets.ime),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                colorFilter = ColorFilter.tint(Color(0xff00B5E2)), // Change to your desired color
                modifier = Modifier.size(180.dp) // Change to your desired size
            )

            Text(
                text = "من فضلك أدخل المعلومات التالية لإكمال التسجيل",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Right ,// Align text to the right in RTL,
                lineHeight = 40.sp
            )

            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("الاسم") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

            // Input Field for Surname
            OutlinedTextField(
                value = surname.value,
                onValueChange = { surname.value = it },
                label = { Text("اللقب") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

            // Input Field for Phone Number
            OutlinedTextField(
                value = phoneNumber.value,
                onValueChange = {
                    phoneNumber.value = it },
                label = { Text("رقم الهاتف الشخصي") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

            // Input Field for Email
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("الايميل الشخصي") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

            // Spacer
            Spacer(modifier = Modifier.height(16.dp))
            val error = remember {
                mutableStateOf("")
            }
            // Next Button
            Button(
                onClick = {
                    if (name.value.isNotEmpty() && surname.value.isNotEmpty() && phoneNumber.value.isNotEmpty() && email.value.isNotEmpty()){
                        viewModelB.updateName(name.value)
                        viewModelB.updateSurname(surname.value)
                        viewModelB.updatePhoneNumber(phoneNumber.value)
                        viewModelB.updateEmail(email.value)


                        navHostController.navigate(route = "businessActivity")
                    }else{
                        error.value = "fill all data"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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
            if (error.value.isNotEmpty()){
                Toast.makeText(LocalContext.current , error.value , Toast.LENGTH_LONG).show()
                error.value = ""
            }
        }
    }
}

@Preview
@Composable
fun InfoPer(modifier: Modifier = Modifier) {
//    PersonInfo()
}