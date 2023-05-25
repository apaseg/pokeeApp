package com.example.pokeeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class verifyOTP : AppCompatActivity() {

    private lateinit var mVerifyCodeBtn: Button
    private lateinit var otpEdit: EditText
    private var OTP: String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)


        mVerifyCodeBtn = findViewById(R.id.btnVerifyCode)
        otpEdit = findViewById(R.id.etOTP)

        firebaseAuth = FirebaseAuth.getInstance()

        OTP = intent.getStringExtra("auth")
        mVerifyCodeBtn.setOnClickListener {
            val verification_code = otpEdit.text.toString()
            if (!verification_code.isEmpty()) {
                val credential = PhoneAuthProvider.getCredential(OTP!!, verification_code)
                signIn(credential)
            } else {
                Toast.makeText(this@verifyOTP, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                sendToMain()
            } else {
                Toast.makeText(this@verifyOTP, "Verification Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            sendToMain()
        }
    }

    private fun sendToMain() {
        startActivity(Intent(this@verifyOTP, MainActivity::class.java))
        finish()
    }
}