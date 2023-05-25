package com.example.pokeeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnsignIn: Button= findViewById(R.id.btnSignIn)

        btnsignIn.setOnClickListener {
            val intent = Intent(applicationContext,SignIn::class.java)
            startActivity(intent)
        }
    }
}