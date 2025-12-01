package com.example.test1000

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.test1000.databinding.ActivityAdminProfileBinding
import com.example.test1000.databinding.ActivityProfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException

class AdminProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var currentUser: FirebaseUser? = null
    private lateinit var userImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        userImage = binding.userImage


        binding.editProfileIcon.setOnClickListener {
            startActivity(Intent(this, EditProfilAdminActivity::class.java))
        }
        binding.home.setOnClickListener {
            startActivity(Intent(this, AdminHomeActivity::class.java))
        }
        binding.Evenements.setOnClickListener {
            startActivity(Intent(this, AdminEventActivity::class.java))
        }
        binding.profil.setOnClickListener {
        }
        binding.nationality.visibility = View.GONE
        binding.status.visibility = View.GONE
        binding.adresse.visibility = View.GONE



        binding.setting.setOnClickListener {
            startActivity(Intent(this, AdminParametreActivity::class.java))
        }

        if (currentUser != null) {
            loadUserProfile(currentUser!!.uid)
        } else {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
        }
    }
    private fun loadUserProfile(userId: String) {
        val userRef = database.child("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val username = dataSnapshot.child("username").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)
                    val phone = dataSnapshot.child("phone").getValue(String::class.java)
                    val profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String::class.java)
                    val nationality = dataSnapshot.child("nationality").getValue(String::class.java)
                    val status = dataSnapshot.child("status").getValue(String::class.java)
                    val adress = dataSnapshot.child("adresse").getValue(String::class.java)
                    if (nationality != null) {
                        binding.idnat.visibility = View.VISIBLE
                        binding.nationality.text = nationality
                    }
                    if (adress != null) {
                        binding.idad.visibility = View.VISIBLE
                        binding.adresse.text = adress
                    }
                    if (status != null) {
                        binding.idst.visibility = View.VISIBLE
                        binding.status.text=status
                    }

                    binding.textViewUsername.text = username
                    binding.textViewEmail.text = email
                    binding.textViewPhone.text = phone

                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this@AdminProfilActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(userImage)
                    }
                } else {
                    Toast.makeText(this@AdminProfilActivity, "Aucune donnée trouvée pour cet utilisateur", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@AdminProfilActivity, "Erreur de chargement des données: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
