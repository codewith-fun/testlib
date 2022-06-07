package com.codeblogs.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import investwell.client.activity.LoginActivity
import investwell.client.activity.SplashActivity
import investwell.client.activity.UserTypesActivity

class MainActivity : AppCompatActivity() {
    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.test)
        button!!.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("pass","1234")
            intent.putExtra("user","user")
            startActivity(intent)
        }
    }
}