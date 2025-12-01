package com.example.test1000

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1000.adapter.ItemAdapter
import com.example.test1000.data.Datasource
import com.example.test1000.databinding.ActivityAdminHomeBinding
import com.example.test1000.databinding.ActivityHomeBinding
import com.example.test1000.model.societies
import com.google.android.material.bottomsheet.BottomSheetDialog

class AdminHomeActivity : AppCompatActivity() , SocietyItemClickListener{
        private lateinit var binding: ActivityAdminHomeBinding
        private lateinit var listIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
            setContentView(binding.root)
        binding.users.setOnClickListener {
            }
        binding.add.setOnClickListener {
            listIntent = Intent(this, AddActivity::class.java)
            startActivity(listIntent)
        }
        binding.vtous.setOnClickListener {
            binding.listSocByCat.visibility = View.GONE
            binding.recyclecat.visibility = View.VISIBLE
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
            binding.home.setOnClickListener { }
            binding.profil.setOnClickListener { launchprofile() }
            binding.setting.setOnClickListener { launchsettings() }
            binding.Evenements.setOnClickListener { launchevent() }
            binding.users.setOnClickListener { launchusers() }

            setupCategoryRecyclerView("Éducation", binding.ed)
            setupCategoryRecyclerView("Santé", binding.med)
            setupCategoryRecyclerView("Vêtements", binding.vet)
            setupCategoryRecyclerView("Alimentation", binding.nour)
            setupCategoryRecyclerView("Animaux", binding.anim)
            setupCategoryRecyclerView("Environnement", binding.env)
        }

    private fun launchusers() {
        listIntent = Intent(this, UsersActivity::class.java)
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

        private fun launchsettings() {
            listIntent = Intent(this, AdminParametreActivity::class.java)
            startActivity(listIntent)
        }

        private fun launchprofile() {
            listIntent = Intent(this, AdminProfilActivity::class.java)
            startActivity(listIntent)
        }
        private fun launchevent() {
            listIntent = Intent(this, AdminEventActivity::class.java)
            startActivity(listIntent)
        }

        private fun setupCategoryRecyclerView(
            categoryName: String,
            recyclerView: RecyclerView,
        ) {
            Datasource().loadSocieties(categoryName) { societes ->
                val adapter = ItemAdapter(this, societes, this)
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = adapter
                recyclerView.setHasFixedSize(true)
            }
        }


        override fun onItemClick(society: societies) {
           show()
        }
    private fun show() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_societe, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val buttonsup = bottomSheetView.findViewById<Button>(R.id.supprimer)

        buttonsup.setOnClickListener {
        }

        dialog.show()
    }
    }