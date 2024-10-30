package com.example.ontour.screen.Trader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ChooseBetweenSingInUp(modifier: Modifier = Modifier , navHostController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color(0xff00B5E2))

    val shouldShowDialog = remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Inscription Button
            Button(
                onClick = {
                          shouldShowDialog.value = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff00B5E2)
                )
            ) {
                Text(
                    text = "التسجيل",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Spacer between buttons
            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                          navHostController.navigate("singInTrader")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff00B5E2)
                )
            ) {
                Text(
                    text = "تسجيل الدخول",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (shouldShowDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        shouldShowDialog.value = false
                    },
                    title = {
                        Text(
                            text = "الشروط",
                            textAlign = TextAlign.Right // Align text to the right
                        )
                    },
                    text = {
                        Text(
                            text = "1- عزيزي التاجر (مقدم الخدمة)، يتوجب عليك وضع جميع المعلومات المطلوبة الشخصية والقانونية بشكل دقيق وصحيح.\n" +
                                    "2- التسجيل بواسطة السجل التجاري إجباري.\n" +
                                    "3- يجب أن تكون معلومات الهوية الشخصية نفسها المدونة في الوثائق القانونية.\n" +
                                    "4- أي تهاون أو تلاعب في المعلومات المطلوبة سيعيق عملية التسجيل.",
                            textAlign = TextAlign.Right // Align text to the right
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            shouldShowDialog.value = false
                            navHostController.navigate(route = "personInfo")
                        }) {
                            Text("موافق")
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun pr(modifier: Modifier = Modifier) {
//     ChooseBetweenSingInUp()
}