package com.example.ontour.screen.Trader

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.ontour.screen.Activity
import com.example.ontour.screen.BusinessViewModel
import com.example.ontour.screen.Product
import com.example.ontour.screen.ProductS
import com.example.ontour.screen.ViewModelActivity
import com.example.ontour.screen.getToken
import com.example.ontour.screen.rememberImeState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalTime
import kotlin.math.log

@SuppressLint("NewApi", "StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    screenWidth: Dp ,
    screenHeight: Dp,
    viewModelB: BusinessViewModel,
    context: Context
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color(0xff00B5E2))
    val viewModelActivity = ViewModelActivity()
    Log.d("hereeeeeeeeee", "Home:${LocalTime.now()}${viewModelB._idSI.value} ")
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LaunchedEffect(Unit) {
            viewModelB.getServiceData(
                getToken(context)
            )
        }

        LaunchedEffect(Unit) {
            viewModelB.getProductsForService(getToken(context))
        }
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            bottomBar = {
                BottomNavigationBar(viewModelActivity)
            }
        ) {
            Box(modifier = Modifier
                .padding(it)
                .background(color = Color.White) , ){
                when( viewModelActivity.screen.value) {
                    is Activity.Home -> {
                        ProductScreen(screenWidth = screenWidth , screenHeight = screenHeight , viewModelB = viewModelB)
                    }
                    is Activity.Profile -> {
                        ProfileScreen(viewModelB , getToken(context))
                    }
                    else->{
                        AddProductScreen(screenWidth , viewModelB , context = context)
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductScreen(
    screenWidth: Dp ,
    viewModelB: BusinessViewModel,
    context: Context
) {
    val addProduct = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = addProduct.value) {
        viewModelB.getProductsForService(getToken(context))
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        // Sample product list
        val products = viewModelB.products.collectAsState()
        val responseData = viewModelB.responseData.collectAsState()

        val idS = remember {
            mutableStateOf(getToken(context = context))
        }

        val idP = remember {
            mutableStateOf(0)
        }

        LaunchedEffect(key1 = responseData.value) {
                val id = responseData.value.getInt("service_category")
                viewModelB.getProductByCategories(id)


        }

        // Variables to hold the selected product, price, and quantity
        val selectedProduct = remember { mutableStateOf("") }
        val price = remember { mutableStateOf("") }
        val quantity = remember { mutableStateOf("") }
        val expanded = remember { mutableStateOf(false) } // For dropdown menu

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // اضف منتج الى محلك
            Text(
                text = "اضف منتج الى محلك",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black ,
                fontWeight = FontWeight.Bold ,
                fontSize = 40.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopStart)
            ) {
                OutlinedTextField(
                    value = selectedProduct.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("اختر منتج ") },
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

                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    modifier = Modifier
                        .width(screenWidth * 90 / 100)
                        .background(Color.White)
                ) {
                    products.value.forEach { product ->
                        DropdownMenuItem(
                            text = { Text(product.product_name, textAlign = TextAlign.Center , color = Color.Black) },
                            onClick = {
                                selectedProduct.value = product.product_name
                                idP.value = product.id
                                expanded.value = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input field for price
            OutlinedTextField(
                value = price.value,
                onValueChange = { price.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "السعر", textAlign = TextAlign.Right) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

            // Input field for quantity
            OutlinedTextField(
                value = quantity.value,
                onValueChange = { quantity.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "الكمية", textAlign = TextAlign.Right) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

            Spacer(modifier = Modifier.height(24.dp))

            // Button to add the product
            Button(
                onClick = {
                    if(selectedProduct.value.isNotEmpty() && price.value.isNotEmpty() && quantity.value.isNotEmpty()){
                        val p = price.value.toDouble()
                        val q= quantity.value.toInt()

                        viewModelB.postProductData(idS.value , idP.value , q , p)
                        addProduct.value = true
                        price.value = ""
                        selectedProduct.value = ""
                        quantity.value = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White ,
                    containerColor = Color(0xff00B5E2)
                )

            ) {
                Text(text = "إضافة المنتج", textAlign = TextAlign.Center)
            }
        }
    }
}


@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    screenWidth: Dp,
    screenHeight: Dp ,
    viewModelB: BusinessViewModel
) {
    val search = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {  // Ensure RTL layout
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {

            val products = viewModelB.productS.collectAsState()
            // Product list section
            LazyColumn {
                itemsIndexed(products.value) { _, product ->
                    ProductCard(product = product)
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(viewModelActivity: ViewModelActivity) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(
                color = Color(0xff00B5E2),
                shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)
            ),
        containerColor = Color(0xff00B5E2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            IconButton(onClick = {
                viewModelActivity.screen.value = Activity.Profile
            },
                modifier = Modifier.size(100.dp)) {
                Icon(
                    imageVector = Icons.Default.Person, // Replace with your profile icon
                    contentDescription = "الملف الشخصي",
                    tint = Color.White
                )
            }

            IconButton(onClick = {
                viewModelActivity.screen.value = Activity.Home
            },
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home, // Replace with your home icon
                    contentDescription = "الصفحة الرئيسية",
                    tint = Color.White
                )
            }

            IconButton(onClick = {
                viewModelActivity.screen.value = Activity.Add
            },
                modifier = Modifier.size(100.dp)) {
                Icon(
                    imageVector = Icons.Default.AddCircle, // Replace with your add icon
                    contentDescription = "إضافة",
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
fun ProductCard(product: ProductS) {
    val showDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(100.dp)
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xff00B5E2))
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color(0xff00B5E2))
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.product_name,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.White
                )
                Text(
                    text = "السعر: ${product.price} ريال",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.White
                )
                Text(
                    text = "الكمية: ${product.quantity}",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )
            }

            // Modify Icon between the image and product details
//            IconButton(
//                onClick = { showDialog.value = true },
//                modifier = Modifier.padding(start = 8.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Edit,
//                    contentDescription = "Edit Product",
//                    tint = Color(0xFF5AA2B8) // Custom color for the icon
//                )
//            }
        }
    }

//    // Show dialog when the modify icon is clicked
//    if (showDialog.value) {
//        ModifyDialog(
//            product = product,
//            onDismiss = { showDialog.value = false },
//            onSave = { newPrice, newQuantity ->
//                // Update product data logic here
//                showDialog.value = false
//            }
//        )
//    }
}


@Composable
fun ModifyDialog(product: ProductS, onDismiss: () -> Unit, onSave: (Double, Int) -> Unit) {
    val newPrice = remember { mutableStateOf(product.price.toString()) }
    val newQuantity = remember { mutableStateOf(product.quantity.toString()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "تعديل المنتج", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newPrice.value,
                    onValueChange = { newPrice.value = it },
                    label = { Text("السعر الجديد") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newQuantity.value,
                    onValueChange = { newQuantity.value = it },
                    label = { Text("الكمية الجديدة") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = onDismiss) {
                        Text("إلغاء")
                    }
                    Button(onClick = {
                        onSave(newPrice.value.toDouble(), newQuantity.value.toInt())
                    }) {
                        Text("حفظ")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModelB: BusinessViewModel , idS : Int) {
    val responseData = viewModelB.responseData.collectAsState()

    var isEditMode = remember { mutableStateOf(false) }
    var name = remember { mutableStateOf(responseData.value.getJSONObject("merchant").getString("first_name")) }
    var surname = remember { mutableStateOf(responseData.value.getJSONObject("merchant").getString("last_name")) }
    var personalPhoneNumber = remember { mutableStateOf(responseData.value.getJSONObject("merchant").getString("phone_number")) }
    var personalEmail = remember { mutableStateOf(responseData.value.getJSONObject("merchant").getString("email")) }
    var businessName = remember { mutableStateOf(responseData.value.getString("service_name")) }
    var storeMessage = remember { mutableStateOf(responseData.value.getString("description")) }
    var socialMediaLinks = remember { mutableStateOf(responseData.value.getString("link_facebook")) }
    var socialMediaLinks1 = remember { mutableStateOf(responseData.value.getString("link_instagram")) }
    var storeImageUri = remember { mutableStateOf<Uri?>(null) }
    val isStoreOpen = remember {
        mutableStateOf(responseData.value.getBoolean("is_open"))

    }

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        storeImageUri.value = uri
    }

    val editable = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = editable.value) {
        if (editable.value){
            viewModelB.getServiceData(idS)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        item {
            Text(
                text = "المعلومات الشخصية",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black ,
                fontWeight = FontWeight.Bold ,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Personal information inputs
            OutlinedTextField(
                value = name.value,
                onValueChange = { if (isEditMode.value) name.value = it },
                label = { Text("الاسم") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditMode.value,
                colors = OutlinedTextFieldDefaults.colors(
                    // Change the color based on isEditMode
                    focusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    cursorColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    disabledTextColor = Color.DarkGray ,
                    disabledBorderColor = Color.DarkGray ,
                    disabledLabelColor = Color.DarkGray
                )
            )

            OutlinedTextField(
                value = surname.value,
                onValueChange = { if (isEditMode.value) surname.value = it },
                label = { Text("اللقب") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditMode.value,
                colors = OutlinedTextFieldDefaults.colors(
                    // Change the color based on isEditMode
                    focusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    cursorColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    disabledTextColor = Color.DarkGray ,
                    disabledBorderColor = Color.DarkGray ,
                    disabledLabelColor = Color.DarkGray
                )
            )

            OutlinedTextField(
                value = personalPhoneNumber.value,
                onValueChange = { if (isEditMode.value) personalPhoneNumber.value = it },
                label = { Text("رقم الهاتف الشخصي") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditMode.value,
                colors = OutlinedTextFieldDefaults.colors(
                    // Change the color based on isEditMode
                    focusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    cursorColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    disabledTextColor = Color.DarkGray ,
                    disabledBorderColor = Color.DarkGray ,
                    disabledLabelColor = Color.DarkGray
                )
            )

            OutlinedTextField(
                value = personalEmail.value,
                onValueChange = { if (isEditMode.value) personalEmail.value = it },
                label = { Text("الايميل الشخصي") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditMode.value,
                colors = OutlinedTextFieldDefaults.colors(
                    // Change the color based on isEditMode
                    focusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    cursorColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    disabledTextColor = Color.DarkGray ,
                    disabledBorderColor = Color.DarkGray ,
                    disabledLabelColor = Color.DarkGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "الاسم التجاري",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black ,
                fontWeight = FontWeight.Bold ,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Business information inputs
            OutlinedTextField(
                value = businessName.value,
                onValueChange = { if (isEditMode.value) businessName.value = it },
                label = { Text("الاسم التجاري") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditMode.value,
                colors = OutlinedTextFieldDefaults.colors(
                    // Change the color based on isEditMode
                    focusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    cursorColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    disabledTextColor = Color.DarkGray ,
                    disabledBorderColor = Color.DarkGray ,
                    disabledLabelColor = Color.DarkGray
                )
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Social Media Links
            OutlinedTextField(
                value = socialMediaLinks.value,
                onValueChange = { if (isEditMode.value) socialMediaLinks.value = it },
                label = { Text("رابط فايسبوك") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditMode.value,
                colors = OutlinedTextFieldDefaults.colors(
                    // Change the color based on isEditMode
                    focusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    cursorColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    disabledTextColor = Color.DarkGray ,
                    disabledBorderColor = Color.DarkGray ,
                    disabledLabelColor = Color.DarkGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = socialMediaLinks1.value,
                onValueChange = { if (isEditMode.value) socialMediaLinks1.value = it },
                label = { Text("رابط انستغرام") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditMode.value,
                colors = OutlinedTextFieldDefaults.colors(
                    // Change the color based on isEditMode
                    focusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    cursorColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    disabledTextColor = Color.DarkGray ,
                    disabledBorderColor = Color.DarkGray ,
                    disabledLabelColor = Color.DarkGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Store image upload
            Text(
                text = "صورة واجهة المحل التجاري",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black ,
                fontWeight = FontWeight.Bold ,
                fontSize = 30.sp
            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(150.dp)
//                    .background(Color.Gray),
//                contentAlignment = Alignment.Center
//            ) {
//                storeImageUri.value?.let { uri ->
//                    Image(
//                        painter = rememberAsyncImagePainter(uri),
//                        contentDescription = "Store Image",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                } ?: Text("اضغط هنا لتحميل صورة المحل")
//
//                // Image picker when user clicks in edit mode
//                if (isEditMode.value) {
//                    Box(
//                        modifier = Modifier
//                            .matchParentSize()
//                            .clickable {
//                                imageLauncher.launch("image/*")
//                            }
//                            .background(Color(0x80000000))
//                    )
//                }
//            }

            Spacer(modifier = Modifier.height(16.dp))

            // Store Message input
            OutlinedTextField(
                value = storeMessage.value,
                onValueChange = { if (isEditMode.value) storeMessage.value = it },
                label = { Text("رسالة المحل التجاري المقدمة للزبون (لا تتعدى 100 كلمة)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                enabled = isEditMode.value,
                colors = OutlinedTextFieldDefaults.colors(
                    // Change the color based on isEditMode
                    focusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedBorderColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    cursorColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    focusedLabelColor = if (isEditMode.value) Color.Black else Color.Red,
                    unfocusedTextColor = if (isEditMode.value) Color.Black else Color.Red,
                    disabledTextColor = Color.DarkGray ,
                    disabledBorderColor = Color.DarkGray ,
                    disabledLabelColor = Color.DarkGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons for Modify and Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { isEditMode.value = !isEditMode.value } ,
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل" , tint = Color.DarkGray)
                }

                Button(
                    onClick = {


                        if (isEditMode.value) {
                            viewModelB.updateService(
                                isStoreOpen.value ,
                                name.value ,
                                surname.value ,
                                "a" + personalEmail.value ,
                                socialMediaLinks1.value ,
                                socialMediaLinks.value ,
                                storeMessage.value ,
                                businessName.value ,
                                responseData.value.getInt("service_category"),
                                personalPhoneNumber.value ,
                                responseData.value.getJSONObject("geographical_points").getDouble("longitude"),
                                responseData.value.getJSONObject("geographical_points").getDouble("latitude") ,
                                idS
                            )

                            viewModelB.updateService(
                                isStoreOpen.value ,
                                name.value ,
                                surname.value ,
                                personalEmail.value ,
                                socialMediaLinks1.value ,
                                socialMediaLinks.value ,
                                storeMessage.value ,
                                businessName.value ,
                                responseData.value.getInt("service_category"),
                                personalPhoneNumber.value ,
                                responseData.value.getJSONObject("geographical_points").getDouble("longitude"),
                                responseData.value.getJSONObject("geographical_points").getDouble("latitude") ,
                                idS
                            )

                            editable.value = true

                            // Save action
                            Toast.makeText(context, "تم حفظ التعديلات", Toast.LENGTH_SHORT).show()
                            isEditMode.value = false
                        }
                    },
                    enabled = isEditMode.value ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff00B5E2)
                    )
                ) {
                    Text(text = "حفظ")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Store status switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isStoreOpen.value) "المحل مفتوح" else "المحل مغلق",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isStoreOpen.value) Color.Green else Color.Red
                )
                Switch(
                    checked = isStoreOpen.value,
                    onCheckedChange = {
                        isStoreOpen.value = it

                        viewModelB.updateService(
                            isStoreOpen.value ,
                            name.value ,
                            surname.value ,
                            "a" + personalEmail.value ,
                            socialMediaLinks1.value ,
                            socialMediaLinks.value ,
                            storeMessage.value ,
                            businessName.value ,
                            responseData.value.getInt("service_category"),
                            personalPhoneNumber.value ,
                            responseData.value.getJSONObject("geographical_points").getDouble("longitude"),
                            responseData.value.getJSONObject("geographical_points").getDouble("latitude") ,
                            idS
                        )

                        viewModelB.updateService(
                            isStoreOpen.value,
                            name.value,
                            surname.value,
                            personalEmail.value,
                            socialMediaLinks1.value,
                            socialMediaLinks.value,
                            storeMessage.value,
                            businessName.value,
                            responseData.value.getInt("service_category"),
                            personalPhoneNumber.value,
                            responseData.value.getJSONObject("geographical_points")
                                .getDouble("longitude"),
                            responseData.value.getJSONObject("geographical_points")
                                .getDouble("latitude"),
                            idS
                        )

                        editable.value = true
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xff00B5E2),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }

    }
}


@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditable: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        enabled = isEditable,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.Black,
            disabledBorderColor = Color.Gray
        )
    )
}



//@Composable
//fun preHome(modifier: Modifier = Modifier) {
//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp.dp
//    val screenHeight = configuration.screenHeightDp.dp
//    HomeScreen(screenWidth , screenHeight)
//}
