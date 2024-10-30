package com.example.ontour.screen.client

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ontour.R
import com.example.ontour.screen.BusinessViewModel
import com.example.ontour.screen.ServiceInfo
import com.mapbox.geojson.Point

@Composable
fun StoreInfoScreen(
    storeName: String = "متجري",
    isStoreOpen: Boolean = true,
    storeDescription: String = "هذا وصف موجز للمحل التجاري. يقدم المحل مجموعة متنوعة من المنتجات ذات الجودة العالية والخدمة الممتازة. يمكنك العثور على كل ما تحتاجه هنا، بدءًا من المنتجات الأساسية وحتى الكماليات. زيارتكم تسعدنا.",
    facebookLink: String = "https://facebook.com/store",
    instagramLink: String = "https://instagram.com/store",
    screenWidth : Dp = LocalConfiguration.current.screenWidthDp.dp,
    navController : NavHostController,
    point : Point,
    service : ServiceInfo
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val clipboardManager = LocalClipboardManager.current
        val context = LocalContext.current




        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.store),
                contentDescription = "صورة المحل",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = service.name, fontWeight = FontWeight.Bold, fontSize = 25.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (service.isOpen) "المحل مفتوح" else "المحل مغلق",
                style = MaterialTheme.typography.bodyLarge,
                color = if (service.isOpen) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = service.description,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontSize = 22.sp,
                modifier = Modifier.padding(20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(service.linkFacebook))
                        Toast.makeText(context, "تم نسخ رابط الفيسبوك", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(120.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Facebook,
                        contentDescription = "Facebook",
                        tint = Color.Blue,
                        modifier = Modifier.size(70.dp)
                    )
                }
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(service.linkInstagram))
                        Toast.makeText(context, "تم نسخ رابط الإنستغرام", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(120.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.instagram),
                        contentDescription = "Instagram",
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("draw_route/${point.longitude()}/${point.latitude()}")
            },
                modifier = Modifier
                    .width(screenWidth * 95 / 100),
                colors =ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xff00B5E2)
                )
            ) {
                Text(text = "ارسم الطريق")
            }
        }
    }
}



