package com.example.test1000

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.test1000.databinding.ActivityEditprofilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class EditProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditprofilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var currentUser: FirebaseUser? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditprofilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        database = FirebaseDatabase.getInstance().reference

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.image.setOnClickListener {
            chooseImage()
        }
        binding.annuler.setOnClickListener {

        }
        binding.modifier.setOnClickListener {
            val username = binding.idNom.text.toString()
            val email = binding.idEml.text.toString()
            val phone = binding.idTel.text.toString()
            val newPassword = binding.newpass.text.toString()
            val confirmation = binding.newpasscon.text.toString()
            val nationality = binding.idNat.text.toString()
            val adresse = binding.idAdresse.text.toString()
            val status = binding.idStatus.text.toString()
            val facebook = binding.idFace.text.toString()
            val instagram = binding.idInst.text.toString()
            val twitter = binding.idTwit.text.toString()

            if (validateInputs(username, email, phone, newPassword, confirmation)) {
                selectedImageUri?.let { imageUri ->
                    uploadImageToFirebase(imageUri) { imageUrl ->
                        showConfirmationDialog(
                            username,
                            email,
                            phone,
                            newPassword,
                            adresse,
                            nationality,
                            imageUri,
                            status
                        )
                    }
                } ?: run {
                    Toast.makeText(this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.error2.visibility = View.VISIBLE
            }
        }
    }

    private fun chooseImage() {
        val options = arrayOf<CharSequence>("Choisir depuis la galerie", "Annuler")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ajouter une Photo")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Choisir depuis la galerie" -> {
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, AddActivity.REQUEST_IMAGE_PICK)
                }

                "Annuler" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddActivity.REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.image.setImageURI(selectedImageUri)
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, callback: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")

        val uploadTask = imagesRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(downloadUri.toString())
            } else {
                callback(null)
            }
        }
    }

    private fun validateInputs(
        username: String,
        email: String,
        phone: String,
        newPassword: String,
        confirmation: String
    ): Boolean {
        return username.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && newPassword == confirmation
    }

    private fun showConfirmationDialog(
        username: String, email: String, phone: String, newPassword: String, adresse: String,
        nationality: String,
        image: Uri,
        status: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmer la modification")
        builder.setMessage("Êtes-vous sûr de vouloir modifier votre profil ?")

        builder.setPositiveButton("Oui") { dialog, _ ->
            dialog.dismiss()
            updateUserProfile(
                username,
                email,
                phone,
                newPassword,
                adresse,
                nationality,
                image,
                status
            )
        }

        builder.setNegativeButton("Non") { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }

    private fun updateUserProfile(
        username: String, email: String, phone: String, newPassword: String,
        adresse: String,
        nationality: String,
        image: Uri,
        status: String
    ) {
        currentUser?.let { user ->
            val userId = user.uid
            val userUpdates = hashMapOf<String, Any>(
                "username" to username,
                "email" to email,
                "phone" to phone,
                "adresse" to adresse,
                "nationality" to nationality,
                "status" to status
            )

            val emailUpdateTask = if (email != user.email) {
                user.updateEmail(email)
            } else {
                null
            }

            val passwordUpdateTask = if (newPassword.isNotEmpty()) {
                user.updatePassword(newPassword)
            } else {
                null
            }

            selectedImageUri?.let { newImageUri ->
                uploadImageToFirebase(newImageUri) { imageUrl ->
                    imageUrl?.let {
                        userUpdates["image"] = it

                        val previousImageUrl = user.photoUrl?.toString()
                        if (!previousImageUrl.isNullOrEmpty()) {
                            val previousImageRef =
                                FirebaseStorage.getInstance().getReferenceFromUrl(previousImageUrl)
                            previousImageRef.delete().addOnSuccessListener {
                            }.addOnFailureListener { exception ->
                                Log.e("DeleteImage", "Failed to delete previous image", exception)
                            }
                        }

                        database.child("users").child(userId).updateChildren(userUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Profil mis à jour avec succès",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Échec de la mise à jour du profil",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } ?: run {
                        Toast.makeText(this, "Échec du téléversement de la nouvelle image", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } ?: run {
                database.child("users").child(userId).updateChildren(userUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this, "Échec de la mise à jour du profil", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }

            emailUpdateTask?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email mis à jour avec succès", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "Échec de la mise à jour de l'email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            passwordUpdateTask?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Mot de passe mis à jour avec succès",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Échec de la mise à jour du mot de passe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } ?: run {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
        }
    }

}
