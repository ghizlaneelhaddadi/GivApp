package com.example.test1000
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.test1000.databinding.ActivityDonationBinding
import com.example.test1000.model.societies
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DonationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonationBinding
    private val db = FirebaseFirestore.getInstance()
    private var donationAmount = 0
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cat = intent.getStringExtra("cat")
        val societyName = intent.getStringExtra("nom")
        val societyImg = intent.getStringExtra("image")

        binding.nom.text = societyName
        Glide.with(this)
            .load(societyImg)
            .into(binding.image)
        binding.nom.text = societyName
        auth = FirebaseAuth.getInstance()

        setupPaymentMethods()
        binding.slider.addOnChangeListener { slider, value, fromUser ->
            donationAmount = value.toInt()
            binding.mon.text = "$donationAmount"+" DH"
            displayMessageBasedOnCategoryAndAmount(cat, donationAmount)
        }
        if (cat != null) {
            setupButtons(cat)
        }
        displayMessageBasedOnCategory(cat)
    }

    private fun displayMessageBasedOnCategory(category: String?) {
        val message = when (category) {
            "Éducation" -> "Votre donation va aider les étudiants à acheter des livres."
            "Environnement" -> "Votre donation va contribuer à la protection de l'environnement."
            "Animaux" -> "Votre donation va aider à la protection des animaux."
            "Alimentation" -> "Votre donation va fournir de la nourriture aux personnes dans le besoin."
            "Vêtements" -> "Votre donation va fournir des vêtements aux personnes nécessiteuses."
            else -> "Votre donation va soutenir les soins de santé pour les nécessiteux."
        }

        binding.message.text = message
    }

    private fun displayMessageBasedOnCategoryAndAmount(category: String?, amount: Int) {
        val specificMessage = when (category) {
            "Éducation" -> "Votre aide va acheter ${amount / 10} livres."
            "Santé" -> "Votre aide va financer ${amount / 100} soins médicaux."
            "Environnement" -> "Votre aide va planter ${amount / 50} arbres."
            "Animaux" -> "Votre aide va protéger ${amount / 20} animaux."
            "Alimentation" -> "Votre aide va fournir ${amount / 5} repas."
            "Vêtements" -> "Votre aide va acheter ${amount / 15} vêtements."
            else -> "Votre aide est inestimable."
        }

        binding.message2.text = specificMessage
    }

    private fun setupPaymentMethods() {
        binding.paymentMethods.setOnCheckedChangeListener { _, checkedId ->
            binding.layoutCreditCard.visibility = if (checkedId == R.id.rb_credit_card) View.VISIBLE else View.GONE
            binding.layoutPaypal.visibility = if (checkedId == R.id.rb_paypal) View.VISIBLE else View.GONE
            binding.googlePay.visibility = if (checkedId == R.id.rb_google_pay) View.VISIBLE else View.GONE
        }
    }


    private fun setupButtons(category: String) {
        binding.cartePay.setOnClickListener {
            val cardNum = binding.cardnum.text.toString()
            val expireDate = binding.expiredate.text.toString()
            val securityCode = binding.securitycode.text.toString()

            if (validateFields(cardNum, expireDate, securityCode)) {
                saveDonation("Credit Card", mapOf(
                    "card_number" to cardNum,
                    "expire_date" to expireDate,
                    "security_code" to securityCode
                ), category)
            }
        }

        binding.paypalPayButton.setOnClickListener {
            val paypalEmail = binding.paypalEmail.text.toString()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(paypalEmail).matches() || paypalEmail.isEmpty()) {
                binding.errorMail.visibility = View.VISIBLE
            } else {
                saveDonation(
                    "PayPal", mapOf(
                        "paypal_email" to paypalEmail
                    ),category
                )
            }
        }

        binding.googlePayButton.setOnClickListener {
            saveDonation("Google Pay", emptyMap(),category)
        }
    }

    private fun validateFields(cardNum: String, expireDate: String, securityCode: String): Boolean {
        var isValid = true

        binding.errorAllFields.visibility = View.GONE
        binding.errorCardnum.visibility = View.GONE
        binding.errorExpiredate.visibility = View.GONE
        binding.errorSecuritycode.visibility = View.GONE

        if (cardNum.isEmpty() || expireDate.isEmpty() || securityCode.isEmpty()) {
            binding.errorAllFields.visibility = View.VISIBLE
            isValid = false
        }

        if (!cardNum.isEmpty() && (!cardNum.matches("\\d{16}".toRegex()))) {
            binding.errorCardnum.visibility = View.VISIBLE
            isValid = false
        }

        if (!expireDate.isEmpty() && (!expireDate.matches("(0[1-9]|1[0-2])/\\d{2}".toRegex()))) {
            binding.errorExpiredate.visibility = View.VISIBLE
            isValid = false
        }

        if (!securityCode.isEmpty() && (!securityCode.matches("\\d{3}".toRegex()))) {
            binding.errorSecuritycode.visibility = View.VISIBLE
            isValid = false
        }

        return isValid
    }

    private fun saveDonation(paymentMethod: String, additionalData: Map<String, String>, category :String) {
        val user = auth.currentUser
        if (user != null) {
            val donationData = mutableMapOf(
                "user_id" to user.uid,
                "society_name" to binding.nom.text.toString(),
                "donation_amount" to binding.mon.text.toString().replace(" DH", "").toInt(),
                "payment_method" to paymentMethod,
                "date" to getCurrentDate()
            )
            donationData.putAll(additionalData)
            db.collection("donations")
                .add(donationData)
                .addOnSuccessListener { documentReference ->
                    updateAccumulator(category,binding.nom.text.toString(), donationAmount)
                    val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                    builder.setTitle("Merci pour votre donation")
                    builder.setMessage("Votre donation a été enregistrée avec succès.")
                    builder.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                        finish()  // Finish the activity
                    }
                    builder.show()
                }.addOnFailureListener {
                    // Handle any errors here
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
    private fun updateAccumulator(category: String, societyName: String, donationAmount: Int) {
        val realtimeDatabase = FirebaseDatabase.getInstance().reference
        val societiesRef = realtimeDatabase.child("societies").child(category)
        societiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (societySnapshot in snapshot.children) {
                    val name = societySnapshot.child("name").getValue(String::class.java) ?: ""
                    if (name.equals(societyName)) {
                        val accumulator = societySnapshot.child("accumulator").getValue(Int::class.java) ?: 0
                        var totalDonationAmount = accumulator + donationAmount
                        societySnapshot.ref.child("accumulator").setValue(totalDonationAmount)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer l'erreur d'annulation ici
            }
        })
    }

}
