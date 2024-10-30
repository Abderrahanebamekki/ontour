package com.example.ontour.screen.Trader

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ontour.screen.BusinessViewModel
import com.example.ontour.screen.FireBaseViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun UploadFile(
    screenWidth: Dp,
    screenHeight : Dp,
    navHostController: NavHostController,
    viewModelB : BusinessViewModel
) {
    // For file uploads
    val pdfFile1 = remember { mutableStateOf<Uri?>(null) }
    val pdfFile2 = remember { mutableStateOf<Uri?>(null) }
    val pdfFile3 = remember { mutableStateOf<Uri?>(null) }




    // Focus manager to handle keyboard dismissal
    val focusManager = LocalFocusManager.current

    // File pickers for each button
    val launcher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            pdfFile1.value = uri
        }
    )
    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> pdfFile2.value = uri }
    )
    val launcher3 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> pdfFile3.value = uri }
    )

    // RTL Layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .clickable {
                    focusManager.clearFocus()
                }
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 100.dp, horizontal = 16.dp)
            ) {
                // Title
                Text(
                    text = "تحميل 3 ملفات PDF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )

                // Instructions
                Text(
                    text = "يرجى تحميل كل ملف PDF على حدة:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Button 1 for first PDF
                Button(
                    onClick = {
                        launcher1.launch(arrayOf("application/pdf"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = " بطاقة التعريف الوطني")
                }

                // Display selected file name for Button 1
                if (pdfFile1.value != null) {
                    Text(
                        text = "بطاقة التعريف الوطني: ${pdfFile1.value?.path?.split("/")?.last()}",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Button 2 for second PDF
                Button(
                    onClick = {
                        launcher2.launch(arrayOf("application/pdf"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = " السجل التجاري")
                }

                // Display selected file name for Button 2
                if (pdfFile2.value != null) {
                    Text(
                        text = " السجل التجاري ${pdfFile2.value?.path?.split("/")?.last()}",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Button 3 for third PDF
                Button(
                    onClick = {
                        launcher3.launch(arrayOf("application/pdf"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "ترخيص ممارسة النشاط التجاري")
                }

                // Display selected file name for Button 3
                if (pdfFile3.value != null) {
                    Text(
                        text = "ترخيص ممارسة النشاط التجاري ${pdfFile3.value?.path?.split("/")?.last()}",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Clear focus when clicked outside input fields

                val error = remember {
                    mutableStateOf("")
                }
                Spacer(modifier = Modifier.height(screenHeight /5))
                val viewModel = FireBaseViewModel()

                LaunchedEffect(key1 = pdfFile1.value , key2 = pdfFile2.value , key3 = pdfFile3.value) {
                    if (pdfFile1.value != null && pdfFile2.value != null && pdfFile3.value != null){
                        viewModel.uploadFiles(pdfFile1.value!! , pdfFile2.value!! , pdfFile3.value!! ,viewModelB)

                    }

                }

                val isRepeating = remember { mutableStateOf(false) }

                val x = viewModel.result1.collectAsState()
                val y = viewModel.result2.collectAsState()
                val z = viewModel.result3.collectAsState()

                Button(
                    onClick = {
                        if (pdfFile1.value != null && pdfFile2.value != null && pdfFile3.value != null){


                            navHostController.navigate(route = "addMapPosition")
                        }else{
                            error.value = "اضف جميع ملفات"
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff00B5E2)
                    ),
                    enabled = !isRepeating.value
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
}









@Preview
@Composable
fun prevUPFIle(modifier: Modifier = Modifier) {
//    UploadFile()
}
