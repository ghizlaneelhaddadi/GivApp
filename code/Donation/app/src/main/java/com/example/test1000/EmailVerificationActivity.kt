package com.example.test1000

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval: Long = 5000

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val resendButton: Button = findViewById(R.id.resend_verification_button)


        resendButton.setOnClickListener {
            resendVerificationEmail()
        }

        if (user != null && !user.isEmailVerified) {
            handler.post(checkEmailVerificationRunnable)
        } else {
            navigateToLogin()
        }
    }

    private val checkEmailVerificationRunnable = object : Runnable {
        override fun run() {
            checkEmailVerificationStatus()
            handler.postDelayed(this, checkInterval)
        }
    }

    private fun checkEmailVerificationStatus() {
        val user = auth.currentUser
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (user.isEmailVerified) {
                    handler.removeCallbacks(checkEmailVerificationRunnable)
                    navigateToLogin()
                } else {
                }
            } else {
            }
        }
    }

    private fun resendVerificationEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                } else {
                }
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkEmailVerificationRunnable)
    }
}
