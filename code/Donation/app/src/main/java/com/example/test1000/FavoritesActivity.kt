package com.example.test1000

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test1000.adapter.FavoriteSocietyAdapter
import com.example.test1000.databinding.ActivityFavoritesBinding
import com.example.test1000.model.FavoriteSociety
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var database: DatabaseReference
    private lateinit var favoriteSocieties: MutableList<FavoriteSociety>
    private lateinit var adapter: FavoriteSocietyAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        favoriteSocieties = mutableListOf()
        adapter = FavoriteSocietyAdapter(favoriteSocieties)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        database = FirebaseDatabase.getInstance().getReference("favorites").child(userId)

        loadFavorites()
    }

    private fun loadFavorites() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoriteSocieties.clear()
                for (dataSnapshot in snapshot.children) {
                    val favoriteSociety = dataSnapshot.getValue(FavoriteSociety::class.java)
                    if (favoriteSociety != null) {
                        favoriteSocieties.add(favoriteSociety)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FavoritesActivity", "Error loading favorites", error.toException())
            }
        })
    }
}
