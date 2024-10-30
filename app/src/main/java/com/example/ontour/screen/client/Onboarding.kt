package com.example.ontour.screen.client

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.navigation.compose.rememberNavController
import com.example.ontour.R
import com.example.ontour.screen.AdInfo
import com.example.ontour.screen.NumberedCircle

@Composable
fun Onboarding(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    screenWidth: Dp,
    screenHeight: Dp
) {
    // CompositionLocalProvider to apply RTL layout direction for Arabic
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.background(Color.White)) {
            val number = remember { mutableStateOf(1) }
            val listAdInfo = listOf(
                AdInfo(
                    title = "إيجاد ما تحتاجه!",
                    description = "استخدم ميزة البحث للعثور على المنتجات التي تحتاجها بسرعة. فقط أدخل اسم المنتج، وسنظهر لك أفضل الخيارات القريبة منك.",
                    lineHeight = 40.sp
                ),
                AdInfo(
                    title = "اكتشاف الخدمات القريبة!",
                    description = "ابحث عن خدمات مثل محطات الوقود، المحلات التجارية والمزيد بسهولة. كل شيء قريب منك!",
                    lineHeight = 60.sp
                ),
                AdInfo(
                    title = "المتجر المفضل!",
                    description = "يمكنك بسهولة العثور على متاجرك المفضلة. فقط أدخل اسم المتجر واستمتع بخيارات قريبة منك.",
                    lineHeight = 70.sp
                )
            )

            Column {
                Image(
                    painter = painterResource(id = R.drawable.map),
                    contentDescription = "خريطة",
                    modifier = Modifier.size(screenWidth, screenHeight * 5 / 8),
                    contentScale = ContentScale.FillBounds
                )

                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF00637C), Color(0xFF00B5E2))
                            ),
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                        .fillMaxHeight()
                        .padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display Arabic text with RTL support
                    Text(
                        text = listAdInfo[number.value - 1].title,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = listAdInfo[number.value - 1].description,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = listAdInfo[number.value - 1].lineHeight,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button to proceed to next onboarding or Sign-In screen
                    IconButton(
                        onClick = {
                            if (number.value >= 3) {
                                Log.d("Onboarding", "Navigating to Sign In")
                                navController.navigate(route = "ChooseBetweenTraderClient")
                            } else {
                                number.value += 1
                            }
                        },
                        modifier = Modifier
                            .padding(start = screenWidth * 5 / 6)
                            .fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_keyboard_double_arrow_left_24),
                            contentDescription = "السهم",
                            tint = Color.Magenta,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            // Numbered Circle to indicate page
            NumberedCircle(
                number = number.value,
                modifier = Modifier.padding(start = screenWidth / 3, top = screenHeight * 3 / 7)
            )
        }
    }
}

@Preview
@Composable
fun PreviewOnboarding() {
    val navController = rememberNavController()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    Onboarding(navController = navController, screenHeight = screenHeight, screenWidth = screenWidth)
}
