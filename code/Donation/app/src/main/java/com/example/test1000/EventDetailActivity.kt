package com.example.test1000

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.test1000.databinding.ActivityEventDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventDetailActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityEventDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val eventDate = intent.getStringExtra("date")
        val eventname = intent.getStringExtra("title")
        val eventdet = intent.getStringExtra("details")
        val eventimage = intent.getStringExtra("img")
        binding.registerButton.setOnClickListener{
            enregistrer()
        }
        auth = FirebaseAuth.getInstance()
        binding.eventTitre.text = eventname
        Glide.with(this)
            .load(eventimage)
            .into(binding.imgsoc)
        binding.description.text = eventdet
        binding.backButton.setOnClickListener {
            finish()
        }
    }
    private fun showRegistrationBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_registration, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)

        val editTextName = bottomSheetView.findViewById<EditText>(R.id.editTextName)
        val buttonRegister = bottomSheetView.findViewById<Button>(R.id.buttonRegister)
        val error = bottomSheetView.findViewById<TextView>(R.id.error)
        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString()
            if (name.isEmpty()) {
                error.visibility = View.VISIBLE
            }
            else{
                saveevent(emptyMap(),name)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun saveevent(Registeration: Map<String, String>, registeredName: String) {
        val user = auth.currentUser
        if (user != null) {
            val register = mutableMapOf(
                "user_id" to user.uid,
                "eventName" to binding.eventTitre.text.toString(),
                "registeredName" to registeredName,
                "date" to getCurrentDate()
            )
            register.putAll(Registeration)
            db.collection("Register")
                .add(register)
                .addOnSuccessListener {
                    val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                    builder.setMessage("Votre enregistrement a été enregistrée avec succès.")
                    builder.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }
                    builder.show()
                }
                .addOnFailureListener { e ->
                    val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                    builder.setTitle("Erreur")
                    builder.setMessage("Une erreur s'est produite. Veuillez réessayer.")
                    builder.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun enregistrer() {
        showRegistrationBottomSheet()
    }

}