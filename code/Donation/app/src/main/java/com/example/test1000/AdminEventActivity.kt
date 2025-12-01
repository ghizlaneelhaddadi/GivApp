package com.example.test1000

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test1000.adapter.EventAdapter
import com.example.test1000.data.Datasource
import com.example.test1000.databinding.ActivityAdminEventBinding
import com.example.test1000.databinding.ActivityEventBinding
import com.example.test1000.model.Events
import com.google.android.material.bottomsheet.BottomSheetDialog

class AdminEventActivity : AppCompatActivity(), EventClickListener {
    private lateinit var binding: ActivityAdminEventBinding
    private lateinit var listIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        binding = ActivityAdminEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Datasource().loadEvents { events ->
            setupRecyclerView(events)
        }
        binding.home.setOnClickListener {
            startActivity(Intent(this, AdminHomeActivity::class.java))
        }
        binding.Evenements.setOnClickListener {
        }
        binding.profil.setOnClickListener {
            startActivity(Intent(this, AdminProfilActivity::class.java))
        }
        binding.setting.setOnClickListener {
            startActivity(Intent(this, AdminParametreActivity::class.java))
        }
    }

    private fun setupRecyclerView(events: List<Events>) {
        val recyclerView = binding.recycleevent
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EventAdapter(this, events, this)
        recyclerView.setHasFixedSize(true)
    }

    override fun onItemClick(event: Events) {
       show()
    }
    @SuppressLint("MissingInflatedId")
    private fun show() {
        val bottomSheetView = layoutInflater.inflate(R.layout.buttom_sheet_events, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)

        val buttonsup = bottomSheetView.findViewById<Button>(R.id.supprimer)

        buttonsup.setOnClickListener {
        }

        dialog.show()
    }
}
