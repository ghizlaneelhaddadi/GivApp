package com.example.test1000

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        val nextButton: TextView = findViewById(R.id.next)
        nextButton.setOnClickListener {
            goToSecondActivity(it)
        }
    }

    fun goToSecondActivity(view: View) {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
}
