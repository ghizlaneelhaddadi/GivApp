package com.example.test1000

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.test1000.databinding.ActivityAddBinding
import com.example.test1000.model.societies
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.ajouter.setOnClickListener {
            val name = binding.idNom.text.toString()
            val categorie = binding.idCat.text.toString()
            val telephone = binding.idTel.text.toString()
            val adresse = binding.idAdresse.text.toString()
            val mail = binding.idEml.text.toString()
            val facebook = binding.idFace.text.toString()
            val instagram = binding.idInst.text.toString()
            val twitter = binding.idTwit.text.toString()
            val site = binding.idSite.text.toString()
            val goal = binding.idGoal.text.toString().toInt()
            val history = binding.idHist.text.toString()
            val description = binding.idDesc.text.toString()

            if (validateInputs(name, categorie, mail, telephone)){
                selectedImageUri?.let { imageUri ->
                    uploadImageToFirebase(imageUri) { imageUrl ->
                        ajouterInfos(
                            name, imageUrl, categorie, 0, goal, adresse, mail, telephone,
                            facebook, instagram, twitter, site, history, description)
                    }
                } ?: run {
                    Toast.makeText(this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show()
                }
            }
            binding.error.visibility = View.VISIBLE
            return@setOnClickListener


        }

        binding.image.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage() {
        val options = arrayOf<CharSequence>("Choisir depuis la galerie", "Annuler")
        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialogTheme)
        builder.setTitle("Ajouter une Photo")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Choisir depuis la galerie" -> {
                    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK)
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
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.image.setImageURI(selectedImageUri)
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, callback: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")
        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ajouterInfos(
        name: String, image: String, categorie: String, accumulateur: Int, goal: Int,
        adresse: String, mail: String, telephone: String, facebook: String, instagram: String,
        twitter: String, site: String, history: String, description: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val societiesRef = database.getReference("societies")

        val validCategories = listOf("Éducation", "Santé", "Vêtements", "Alimentation", "Animaux", "Environnement")
        if (categorie in validCategories) {
            val societyInfo = hashMapOf(
                "name" to name,
                "image" to image,
                "accumulateur" to accumulateur,
                "goal" to goal,
                "contact" to hashMapOf("address" to adresse, "email" to mail, "phone" to telephone),
                "social_media" to hashMapOf("facebook" to facebook, "instagram" to instagram, "twitter" to twitter),
                "history" to history,
                "description" to description,
                "website" to site
            )

            societiesRef.child(categorie).push().setValue(societyInfo)
                .addOnSuccessListener {
                    Toast.makeText(this, "Les informations sont ajoutées", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "probléme: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Catégorie non valide", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(name: String, categorie: String, mail: String, tel: String): Boolean {
        binding.errorNom.visibility = View.GONE
        binding.errorCategorie.visibility = View.GONE
        binding.errorMail.visibility = View.GONE
        binding.errorTel.visibility = View.GONE

        if (name.isEmpty()) {
            binding.errorNom.visibility = View.VISIBLE
            return false
        }
        if (categorie.isEmpty()) {
            binding.errorCategorie.visibility = View.VISIBLE
            return false
        }
        if (mail.isEmpty()) {
            binding.errorMail.visibility = View.VISIBLE
            return false
        }
        if (tel.isEmpty()){
            binding.errorTel.visibility = View.VISIBLE
            return false
        }
        return true
    }

    companion object {
        internal const val REQUEST_IMAGE_PICK = 1
    }
}
