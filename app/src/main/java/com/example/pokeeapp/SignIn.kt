package com.example.pokeeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SignIn : AppCompatActivity() {

    private lateinit var mSendOTPBtn: Button
    private lateinit var processText: TextView
    private lateinit var countryCodeEdit: EditText
    private lateinit var phoneNumberEdit: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mSendOTPBtn = findViewById(R.id.btnSendCode)
        countryCodeEdit = findViewById(R.id.etNumber)
        phoneNumberEdit = findViewById(R.id.etPhoneNumber)
        processText = findViewById(R.id.tvProcessText)

        auth = FirebaseAuth.getInstance()

        mSendOTPBtn.setOnClickListener {
            val country_code = countryCodeEdit.text.toString()
            val phone = phoneNumberEdit.text.toString()
            val phoneNumber = "+$country_code$phone"
            if (!country_code.isEmpty() || !phone.isEmpty()) {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this@SignIn)
                    .setCallbacks(mCallBacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            } else {
                processText.text = "Please Enter Country Code and Phone Number"
                processText.setTextColor(Color.RED)
                processText.visibility = View.VISIBLE
            }
        }

        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signIn(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                processText.text = e.message
                processText.setTextColor(Color.RED)
                processText.visibility = View.VISIBLE
            }

            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)

                // Sometime the code is not detected automatically
                // So the user has to manually enter the code
                processText.text = "OTP has been Sent"
                processText.visibility = View.VISIBLE
                Handler().postDelayed({
                    val otpIntent = Intent(this@SignIn, verifyOTP::class.java)
                    otpIntent.putExtra("auth", s)
                    startActivity(otpIntent)
                }, 10000.toLong())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            sendToMain()
        }
    }

    private fun sendToMain() {
        val mainIntent = Intent(this@SignIn, setInfo::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun signIn(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendToMain()
            } else {
                processText.text = task.exception?.message
                processText.setTextColor(Color.RED)
                processText.visibility = View.VISIBLE
            }
        }
    }
}