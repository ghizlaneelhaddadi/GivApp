package com.example.test1000

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1000.adapter.ItemAdapter
import com.example.test1000.data.Datasource
import com.example.test1000.databinding.ActivityVoirtousBinding
import com.example.test1000.model.societies

class VoirtousActivity : AppCompatActivity(), SocietyItemClickListener {
    private lateinit var binding: ActivityVoirtousBinding
    private lateinit var listIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVoirtousBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val category = intent.getStringExtra("category") ?: ""
        setupCategoryRecyclerView(category, binding.listSocByCat)
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupCategoryRecyclerView(categoryName: String, recyclerView: RecyclerView) {
        Datasource().loadSocieties(categoryName) { societiesList ->
            val adapter = ItemAdapter(this, societiesList, this)
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
        }
    }

    override fun onItemClick(society: societies) {
        val societyName = society.name
        val img = society.image
        val desc = society.description
        val category = society.category
        val accumulator = society.accumulator
        val goalsociety = society.goalsociety
        val adr = society.address
        val mail = society.email
        val phone = society.phone
        val facebook = society.facebook
        val instagram= society.instagram
        val twitter= society.twitter
        val site = society.site

        listIntent = Intent(this, DetailSocietyActivity::class.java).apply {
            putExtra("nom", societyName)
            putExtra("img", img)
            putExtra("desc", desc)
            putExtra("acc", accumulator)
            putExtra("goal", goalsociety)
            putExtra("category", category)
            putExtra("address", adr)
            putExtra("email", mail)
            putExtra("phone", phone)
            putExtra("facebook", facebook)
            putExtra("instagram", instagram)
            putExtra("twitter", twitter)
            putExtra("site", site)
        }
        startActivity(listIntent)
    }

}
