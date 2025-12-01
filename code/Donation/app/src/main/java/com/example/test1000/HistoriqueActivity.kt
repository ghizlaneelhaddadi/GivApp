package com.example.test1000

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.test1000.databinding.ActivityHistoriqueBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class HistoriqueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoriqueBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val donationsList = ArrayList<Map<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoriqueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener { onBackPressed() }

        binding.donateNowButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        loadDonations()
    }

    private fun loadDonations() {
        val user = auth.currentUser
        val userId = user?.uid

        db.collection("donations")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener { documents ->
                donationsList.clear()
                for (document in documents) {
                    val donation = document.data
                    donation["id"] = document.id
                    donationsList.add(donation)
                }

                if (donationsList.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.listeDonations.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.listeDonations.visibility = View.VISIBLE

                    val adapter = DonationAdapter()
                    binding.listeDonations.adapter = adapter
                }
            }
            .addOnFailureListener {
                // Handle the error here if necessary
            }
    }

    inner class DonationAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return donationsList.size
        }

        override fun getItem(position: Int): Any {
            return donationsList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = convertView ?: LayoutInflater.from(this@HistoriqueActivity)
                .inflate(R.layout.item_donation, parent, false)
            val donation = donationsList[position]

            val donationText = view.findViewById<TextView>(R.id.donation_text)
            val deleteButton = view.findViewById<ImageButton>(R.id.delete_button)

            val society = donation["society_name"] as String
            val montant = donation["donation_amount"]
            val modePaiement = donation["payment_method"] as String
            val donationDate = donation["date"] as String

            val donationString = "$society\n" +
                    "Date de la donation: $donationDate\n" +
                    "Montant: $montant MAD\n" +
                    "Mode de paiement: $modePaiement\n"

            donationText.text = donationString

            deleteButton.setOnClickListener {
                val donationId = donation["id"] as String
                db.collection("donations").document(donationId)
                    .delete()
                    .addOnSuccessListener {
                        donationsList.removeAt(position)
                        notifyDataSetChanged()
                        if (donationsList.isEmpty()) {
                            binding.emptyStateLayout.visibility = View.VISIBLE
                            binding.listeDonations.visibility = View.GONE
                        }
                    }
            }

            return view
        }
    }
}
