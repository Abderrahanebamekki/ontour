//package com.example.ontour.screen
//
//import android.app.Activity
//import android.content.Context
//import android.util.Log
//import com.google.firebase.FirebaseException
//import com.google.firebase.FirebaseTooManyRequestsException
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
//import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
//import com.google.firebase.auth.PhoneAuthCredential
//import com.google.firebase.auth.PhoneAuthOptions
//import com.google.firebase.auth.PhoneAuthProvider
//import java.util.concurrent.TimeUnit
//
//class PhoneAuthManager(private val context: Context) {
//    private val auth = FirebaseAuth.getInstance()
//    private var storedVerificationId: String? = null
//    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
//
//    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            Log.d(TAG, "onVerificationCompleted:$credential")
//
//        }
//
//        override fun onVerificationFailed(e: FirebaseException) {
//            Log.w(TAG, "onVerificationFailed", e)
//
//            if (e is FirebaseAuthInvalidCredentialsException) {
//                // Invalid request
//            } else if (e is FirebaseTooManyRequestsException) {
//                // SMS quota exceeded
//            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
//                // reCAPTCHA verification attempted with null Activity
//            }
//        }
//
//        override fun onCodeSent(
//            verificationId: String,
//            token: PhoneAuthProvider.ForceResendingToken
//        ) {
//            Log.d(TAG, "onCodeSent:$verificationId")
//            storedVerificationId = verificationId
//            resendToken = token
//        }
//    }
//
//    fun startPhoneNumberVerification(phoneNumber: String) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNumber)
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(context as Activity)
//            .setCallbacks(callbacks)
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }
//
//    fun getStoredVerificationId(): String? = storedVerificationId
//
//    fun verifyOtpCode(otpCode: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
//        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otpCode)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(context as Activity) { task ->
//                if (task.isSuccessful) {
//                    onSuccess()
//                } else {
//                    onFailure(task.exception ?: Exception("Unknown error"))
//                }
//            }
//    }
//
//    companion object {
//        private const val TAG = "PhoneAuthManager"
//    }
//}
