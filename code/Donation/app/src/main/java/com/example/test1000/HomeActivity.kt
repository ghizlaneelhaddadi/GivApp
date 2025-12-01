package com.example.test1000

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1000.adapter.ItemAdapter
import com.example.test1000.data.Datasource
import com.example.test1000.databinding.ActivityHomeBinding
import com.example.test1000.model.societies


class HomeActivity : AppCompatActivity(), SocietyItemClickListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var listIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.notification.setOnClickListener {
            val notificationIntent = Intent(this, NotificationActivity::class.java)
            startActivity(notificationIntent)
        }
        binding.favorisButton.setOnClickListener {
            val notificationIntent = Intent(this, FavoritesActivity::class.java)
            startActivity(notificationIntent)
        }
        binding.educ.setOnClickListener {
            binding.recyclecat.visibility = View.GONE
            val category = "Éducation"
            setupCategoryRecyclerView1(category, binding.listSocByCat)
            binding.listSocByCat.visibility = View.VISIBLE

        }
        binding.sante.setOnClickListener {
            binding.recyclecat.visibility = View.GONE
            val category = "Santé"
            setupCategoryRecyclerView1(category, binding.listSocByCat)
            binding.listSocByCat.visibility = View.VISIBLE

        }
        binding.ani.setOnClickListener {
            binding.recyclecat.visibility = View.GONE
            val category = "Animaux"
            setupCategoryRecyclerView1(category, binding.listSocByCat)
            binding.listSocByCat.visibility = View.VISIBLE


        }
        binding.vetement.setOnClickListener {
            binding.recyclecat.visibility = View.GONE
            val category = "Vêtements"
            setupCategoryRecyclerView1(category, binding.listSocByCat)
            binding.listSocByCat.visibility = View.VISIBLE

        }
        binding.food.setOnClickListener {
            binding.recyclecat.visibility = View.GONE
            val category = "Alimentation"
            setupCategoryRecyclerView1(category, binding.listSocByCat)
            binding.listSocByCat.visibility = View.VISIBLE


        }
        binding.envi.setOnClickListener {
            binding.recyclecat.visibility = View.GONE
            val category = "Environnement"
            setupCategoryRecyclerView1(category, binding.listSocByCat)
            binding.listSocByCat.visibility = View.VISIBLE


        }
        binding.vtous.setOnClickListener {
            binding.listSocByCat.visibility = View.GONE
            binding.recyclecat.visibility = View.VISIBLE
        }



        binding.home.setOnClickListener { }
        binding.profil.setOnClickListener { launchprofile() }
        binding.setting.setOnClickListener { launchsettings() }
        binding.Evenements.setOnClickListener { launchevent() }
        binding.localisation.setOnClickListener{launchloca()}
        binding.donationHistoryButton.setOnClickListener {
            val intent = Intent(this, HistoriqueActivity::class.java)
            startActivity(intent)
        }

        setupCategoryRecyclerView("Éducation", binding.ed, binding.vt1)
        setupCategoryRecyclerView("Santé", binding.med, binding.vt2)
        setupCategoryRecyclerView("Vêtements", binding.vet, binding.vt3)
        setupCategoryRecyclerView("Alimentation", binding.nour, binding.vt4)
        setupCategoryRecyclerView("Animaux", binding.anim, binding.vt5)
        setupCategoryRecyclerView("Environnement", binding.env, binding.vt6)
    }
    private fun launchloca(){
        listIntent = Intent(this, MapsActivity::class.java)
        startActivity(listIntent)

    }

    private fun launchsettings() {
        listIntent = Intent(this, ParametreActivity::class.java)
        startActivity(listIntent)
    }

    private fun launchprofile() {
        listIntent = Intent(this, ProfilActivity::class.java)
        startActivity(listIntent)
        finish()
    }

    private fun launchevent() {
        listIntent = Intent(this, EventActivity::class.java)
        startActivity(listIntent)
    }

    private fun setupCategoryRecyclerView(categoryName: String, recyclerView: RecyclerView, view: View) {
        Datasource().loadSocieties(categoryName) { societes ->
            val adapter = ItemAdapter(this, societes, this)
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
            view.setOnClickListener {
                launchvoirtouts(categoryName)
            }
        }
    }
    private fun launchvoirtouts(category: String) {
        listIntent = Intent(this, VoirtousActivity::class.java).apply {
            putExtra("category", category)
        }
        startActivity(listIntent)
    }

    private fun setupCategoryRecyclerView1(categoryName: String, recyclerView: RecyclerView) {
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
