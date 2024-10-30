package com.example.ontour.screen.Trader

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessActivity(
    modifier: Modifier = Modifier,
    screenWidth: Dp,
    screenHeight : Dp,
    navHostController: NavHostController,
    viewModelB :BusinessViewModel


) {

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }
    Column(
        verticalArrangement = Arrangement.Center ,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .windowInsetsPadding(WindowInsets.ime)
    ) {
        val expanded = remember { mutableStateOf(false) }
        val selectedBusinessType = remember { mutableStateOf("اختر نوع النشاط") }

        val systemUiController = rememberSystemUiController()
        systemUiController.setStatusBarColor(color = Color(0xff00B5E2))

        // List of business types
        val businessTypes = viewModelB.serviceCategories.collectAsState()


        // State for text inputs
        val businessName = remember { mutableStateOf("") }
        val description = remember { mutableStateOf("") }

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            colorFilter = ColorFilter.tint(Color(0xff00B5E2)), // Change to your desired color
            modifier = Modifier.size(180.dp) // Change to your desired size
        )



        // Provide RTL Layout Direction globally
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "يرجى ملء الحقول التالية لإكمال معلومات النشاط التجاري:",
                    fontSize = 30.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp
                )
                // Dropdown for Business Type
                Box(modifier = Modifier
                    .padding(end = 10.dp)
                    .align(Alignment.CenterHorizontally)){
                    OutlinedTextField(
                        value = selectedBusinessType.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("النشاط او نوعية الخدمة") },
                        trailingIcon = {
                            IconButton(onClick = { expanded.value= !expanded.value }) {
                                Icon(
                                    imageVector = if (expanded.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
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

                    // DropdownMenu
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier
                            .width(screenWidth * 90 / 100)
                            .background(Color.White)
                    ) {
                        businessTypes.value.forEach { businessType ->
                            DropdownMenuItem(
                                text = { Text(text = businessType.category_name, fontSize = 16.sp , color = Color.Black) },
                                onClick = {
                                    selectedBusinessType.value = businessType.category_name
                                    viewModelB.idS.value = businessType.id
                                    expanded.value = false  // Close dropdown
                                },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Business Name Input
                OutlinedTextField(
                    value = businessName.value,
                    onValueChange = { businessName.value = it },
                    label = { Text("الاسم التجاري") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
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

                Spacer(modifier = Modifier.height(16.dp))
                val numWord = remember {
                    mutableStateOf(0)
                }
                // Business Address Input
                OutlinedTextField(
                    value = description.value,
                    onValueChange = {
                        description.value = it
                    },
                    label = { Text("وصف المحل") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
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

                Spacer(modifier = Modifier.height(180.dp))

                // Next Button
                Button(
                    onClick = {
                        viewModelB.description.value = description.value
                        viewModelB.serviceName.value = businessName.value
                        navHostController.navigate(route = "uploadFile")
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
            }
        }
    }
}

@Preview
@Composable
fun preBuisness(modifier: Modifier = Modifier) {
//    BusinessActivity()
}