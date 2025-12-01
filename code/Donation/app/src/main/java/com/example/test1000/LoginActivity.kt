package com.example.test1000

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test1000.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.registerbouton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.loginadmin.setOnClickListener {
            val email = binding.mail.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (!validateInputs(email, password)) {
                return@setOnClickListener
            }
            loginadmin(email,password)
        }

        binding.loginbouton.setOnClickListener {
            val email = binding.mail.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (!validateInputs(email, password)) {
                return@setOnClickListener
            }

            signInUser(email, password)
        }
    }
    private fun loginadmin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val userId = currentUser.uid
                        val db = FirebaseFirestore.getInstance()
                        val docRef = db.collection("admins").document(userId)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val intent = Intent(this, AdminHomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        baseContext, "Vous étes pas authorizé comme admin.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener {
                            }
                    }
                }
            }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        baseContext, "Authentification échouée. Veuillez vérifier vos identifiants.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        binding.error.visibility = View.GONE


        if (email.isEmpty() || password.isEmpty()) {
            binding.error.visibility = View.VISIBLE
            return false
        }
        return true
    }
    fun forgotPasswordClicked(view: View) {
        val email = binding.mail.text.toString().trim()
        if (email.isEmpty()) {
            binding.errorMail.visibility = View.VISIBLE
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext, "Un e-mail de réinitialisation de mot de passe a été envoyé à votre adresse e-mail.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext, "Échec de l'envoi de l'e-mail de réinitialisation de mot de passe. Veuillez vérifier votre adresse e-mail.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}
