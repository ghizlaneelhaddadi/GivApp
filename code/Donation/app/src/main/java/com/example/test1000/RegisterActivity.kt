package com.example.test1000

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test1000.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var listIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.connectNow.setOnClickListener {
            listIntent = Intent(this, LoginActivity::class.java)
            startActivity(listIntent)
            finish()
        }
        binding.regbouton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = binding.emailreg.text.toString().trim()
        val phone = binding.telreg.text.toString().trim()
        val password = binding.passreg.text.toString().trim()
        val username = binding.namereg.text.toString().trim()

        if (!validateInputs(username, email, phone, password)) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    // Enregistrer les informations de l'utilisateur dans Realtime Database
                    val userData = HashMap<String, Any>()
                    userData["username"] = username
                    userData["email"] = email
                    userData["phone"] = phone

                    // Ajouter les données de l'utilisateur dans la base de données
                    userId?.let {
                        database.child("users").child(it).setValue(userData)
                            .addOnCompleteListener { databaseTask ->
                                if (databaseTask.isSuccessful) {
                                    user.sendEmailVerification()
                                        .addOnCompleteListener { emailTask ->
                                            if (emailTask.isSuccessful) {
                                                val intent = Intent(this, EmailVerificationActivity::class.java)
                                                startActivity(intent)
                                            } else {
                                                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(this, "Failed to register user in database.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Inscription échouée. Veuillez réessayer plus tard.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateInputs(username: String, email: String, phone: String, password: String): Boolean {
        binding.error.visibility = View.GONE
        binding.errorMdp.visibility = View.GONE
        binding.errorMail.visibility = View.GONE
        binding.errorTel.visibility = View.GONE

        if (username.isEmpty()||email.isEmpty()||phone.isEmpty()||password.isEmpty()) {
            binding.error.visibility = View.VISIBLE
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()&& email.isNotEmpty()) {
            binding.errorMail.visibility = View.VISIBLE
            return false
        }

        if (!Patterns.PHONE.matcher(phone).matches()&& phone.isNotEmpty()) {
            binding.errorTel.visibility = View.VISIBLE
            return false
        }

        if (password.length < 6 && password.isNotEmpty()) {
            binding.errorMdp.visibility = View.VISIBLE
            return false
        }
        return true
    }
}
