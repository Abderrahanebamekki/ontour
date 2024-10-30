package com.example.ontour.screen.Trader

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ontour.R
import com.example.ontour.screen.BusinessViewModel

@Composable
fun ThanksScreen(
    navHostController: NavHostController , viewModelB: BusinessViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {



        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            colorFilter = ColorFilter.tint(Color(0xff00B5E2)), // Change to your desired color
            modifier = Modifier.size(230.dp) // Change to your desired size
        )

        Spacer(modifier = Modifier.height(100.dp))

        Box(
            modifier = Modifier
                .clickable {
                    viewModelB.sendPassword()
                    navHostController.navigate("singInTrader")
                }
        ){
            Image(
                painter = painterResource(id = R.drawable.thanks),
                contentDescription = "thanks",
                colorFilter = ColorFilter.tint(Color(0xff00B5E2)),// Change to your desired color
                modifier = Modifier.size(400.dp) // Change to your desired size
            )
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 60.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "     شكرا,",
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = Color.White,
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Bold

                )

                Text(
                    text = "سيتم معالجة ملفاتك لاحقا",
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            }




        }


    }



}

@Preview
@Composable
fun preThan(modifier: Modifier = Modifier) {
//    ThanksScreen()
}
