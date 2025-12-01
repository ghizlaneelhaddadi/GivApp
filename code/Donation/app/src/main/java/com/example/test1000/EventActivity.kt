package com.example.test1000

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test1000.adapter.EventAdapter
import com.example.test1000.data.Datasource
import com.example.test1000.databinding.ActivityEventBinding
import com.example.test1000.model.Events

class EventActivity : AppCompatActivity(), EventClickListener {
    private lateinit var binding: ActivityEventBinding
    private lateinit var listIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load events from Firebase and set up the RecyclerView
        Datasource().loadEvents { events ->
            setupRecyclerView(events)
        }

        binding.home.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.localisation.setOnClickListener {
            startActivity(Intent(this,MapsActivity::class.java))

        }

        binding.profil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        binding.setting.setOnClickListener {
            startActivity(Intent(this, ParametreActivity::class.java))
        }
    }

    private fun setupRecyclerView(events: List<Events>) {
        val recyclerView = binding.recycleevent
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EventAdapter(this, events, this)
        recyclerView.setHasFixedSize(true)
    }

    override fun onItemClick(event: Events) {
        val eventTitle = event.name
        val eventDetails = event.details
        val eventImg = event.imageResource
        val eventDate = event.date

        listIntent = Intent(this, EventDetailActivity::class.java).apply {
            putExtra("title", eventTitle)
            putExtra("details", eventDetails)
            putExtra("img", eventImg)
            putExtra("date", eventDate)
        }
        startActivity(listIntent)
    }
}
