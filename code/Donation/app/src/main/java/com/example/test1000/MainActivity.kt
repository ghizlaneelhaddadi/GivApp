package com.example.test1000

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        requestNotificationPermission()

        if (auth.currentUser != null) {
            checkUserRole(auth.currentUser!!.uid)
        } else {
            supportActionBar?.hide()
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, FirstActivity::class.java))
                finish()
            }, 5000)
        }
    }

    private fun checkUserRole(userId: String) {
        val docRef = db.collection("admins").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    when (role) {
                        "admin" -> {
                            val intent = Intent(this, AdminHomeActivity::class.java)
                            startActivity(intent)
                        }
                        "user" -> {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        else -> {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    baseContext, "Failed to check user role: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if ((grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted.
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied.
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
