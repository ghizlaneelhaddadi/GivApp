package com.example.test1000

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.test1000.databinding.ActivityParametreBinding
import com.google.firebase.auth.FirebaseAuth

class ParametreActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityParametreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParametreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val darkModeSwitch: Switch = findViewById(R.id.darkModeSwitch)
        darkModeSwitch.isChecked = sharedPreferences.getBoolean("dark_mode", false)

        binding.titleText.text = "Paramètres"



        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.home.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.Evenements.setOnClickListener {
            startActivity(Intent(this, EventActivity::class.java))
        }

        binding.profil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        binding.localisation.setOnClickListener {
            startActivity(Intent(this,MapsActivity::class.java))

        }


        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.notificationButton.setOnClickListener {
            startActivity(Intent(this, SettingsNotifActivity::class.java))
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialogTheme)
        builder.setTitle("Déconnexion")
        builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")

        builder.setPositiveButton("Oui") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Non") { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }
}
