package com.example.ontour.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalTime

class FireBaseViewModel() : ViewModel() {

     val  _result1 = MutableStateFlow("")
    val result1 = _result1.asStateFlow()
    private val _result2 = MutableStateFlow("")
    val result2 = _result2.asStateFlow()
    private val _result3 = MutableStateFlow("")
    val result3 = _result3.asStateFlow()

    @SuppressLint("NewApi")
    suspend fun uploadFiles(
        file1: Uri,
        file2: Uri,
        file3: Uri,
        viewModel: BusinessViewModel
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uploadFileToFirebase(file1, "id_card${viewModel.email}.pdf")
                uploadFileToFirebase(file2, "registre_de_commerce${viewModel.email}.pdf")
                uploadFileToFirebase(file3, "autorization_of_register_of_commerce${viewModel.email}.pdf")
            }
        }

    }

    @SuppressLint("NewApi")
    private suspend fun uploadFileToFirebase(file: Uri, storagePath: String) {
        val storageReference = FirebaseStorage.getInstance().reference.child(storagePath)

        try {
            // Upload the file and await completion
            storageReference.putFile(file).await()

            // Retrieve the download URL
            _result1.emit(storageReference.downloadUrl.await().toString())
            Log.d("FirebaseUpload", "File uploaded successfully. Download URL: ${LocalTime.now()} //// ${_result1.value}")


        } catch (e: Exception) {
            Log.e("FirebaseUpload", "File upload failed: ${e.message}")
             // Return an empty string in case of failure
        }
    }
}