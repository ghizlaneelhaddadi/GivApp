package com.example.test1000

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.test1000.databinding.ActivityDetailsBinding
import com.example.test1000.model.Comment
import com.example.test1000.model.FavoriteSociety
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DetailSocietyActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var database: DatabaseReference
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentList: MutableList<Comment>
    private var isFavorite = false
    private lateinit var societyName: String
    private var accumulateur : Int = 0
    private var goal :Int = 0
    private lateinit var societyImage: String
    private lateinit var desc: String
    private lateinit var categorie: String
    private lateinit var address: String
    private lateinit var email: String
    private lateinit var phone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        database = FirebaseDatabase.getInstance().getReference("societies")

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.address.setOnClickListener {
            val mapIntent = Intent(this, MapsActivity::class.java).apply {
                putExtra("address", address)
            }
            startActivity(mapIntent)
        }

        binding.phone.setOnClickListener {
            val phoneIntent = Intent(Intent.ACTION_DIAL)
            phoneIntent.data = Uri.parse("tel:$phone")
            startActivity(phoneIntent)
        }

        binding.email.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                putExtra(Intent.EXTRA_TEXT, "Body Here")
            }
            startActivity(emailIntent)
        }

        binding.donner.setOnClickListener { donner() }

        binding.favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon()
            updateFavoriteState(societyName, isFavorite)
        }

        societyName = intent.getStringExtra("nom") ?: ""
        categorie = intent.getStringExtra("category")?:""
        societyImage = intent.getStringExtra("img") ?: ""
        desc = intent.getStringExtra("desc") ?: ""
        accumulateur = intent.getIntExtra("acc",0)
        address = intent.getStringExtra("address") ?: ""
        goal = intent.getIntExtra("goal",0)
        email = intent.getStringExtra("email") ?: ""
        phone = intent.getStringExtra("phone") ?: ""


        binding.nom.text = societyName
        binding.description.text = desc
        binding.address.text = address
        binding.email.text = email
        binding.phone.text = phone
        binding.progressText.text = (accumulateur/goal).toString()

        Glide.with(this)
            .load(societyImage)
            .into(binding.image)

        updateProgressBar(societyName,accumulateur,goal)

        binding.commentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString().trim()
            if (commentText.isNotEmpty()) {
                getCurrentUserNameFromDatabase(commentText)
            }
        }


        // Initialisation de la RecyclerView pour les commentaires
        commentList = mutableListOf()
        commentAdapter = CommentAdapter(commentList)
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.commentsRecyclerView.adapter = commentAdapter

        // Charger les commentaires depuis Firebase
        loadCommentsFromFirebase()
    }

    private fun postComment(commentText: String, userName: String) {
        val commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(societyName)

        val commentId = commentsRef.push().key

        commentId?.let {
            val userId = getCurrentUserId()

            val comment = Comment(it, commentText, System.currentTimeMillis().toString(), userId, userName)

            commentsRef.child(it).setValue(comment)
                .addOnSuccessListener {
                    Log.d(TAG, "Comment posted successfully")
                    binding.commentEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to post comment", e)
                }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Partager la société")
                    putExtra(Intent.EXTRA_TEXT, "Découvrez cette société : $societyName")
                }
                startActivity(Intent.createChooser(shareIntent, "Partager via"))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun donner() {
        val listIntent = Intent(this, DonationActivity::class.java).apply {
            putExtra("cat", categorie)
            putExtra("nom", societyName)
            putExtra("image", societyImage)
        }
        startActivity(listIntent)
    }

    private fun updateProgressBar(societyName: String?,accumulateur : Int ,goal : Int ) {
        societyName?.let {
            val societyRef = database.child(it)
            societyRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        binding.goalProgressBar.max = goal
                        binding.goalProgressBar.progress = accumulateur
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Error getting data", databaseError.toException())
                }
            })
        }
    }

    private fun updateFavoriteIcon() {
        val favoriteIconRes = if (isFavorite) R.drawable.coeur_rempli else R.drawable.coeur_vide
        binding.favoriteButton.setImageResource(favoriteIconRes)
    }

    private fun updateFavoriteState(societyName: String?, isFavorite: Boolean) {
        societyName?.let {
            val favoriteRef = FirebaseDatabase.getInstance().getReference("favorites").child(it)
            if (isFavorite) {
                saveFavoriteSociety(it, societyImage, desc)
            } else {
                favoriteRef.removeValue()
                    .addOnSuccessListener {
                        Log.d(TAG, "Favorite state removed successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error removing favorite state", e)
                    }
            }
        }
    }

    private fun saveFavoriteSociety(name: String, image: String, desc: String) {
        val favoriteRef = FirebaseDatabase.getInstance().getReference("favorites").child(name)
        val favoriteSociety = FavoriteSociety(name, image, desc)
        favoriteRef.setValue(favoriteSociety)
            .addOnSuccessListener {
                Log.d(TAG, "Favorite society saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving favorite society", e)
            }
    }

    private fun loadCommentsFromFirebase() {
        val commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(societyName)
        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList.clear()
                for (snapshot in dataSnapshot.children) {
                    val comment = snapshot.getValue(Comment::class.java)
                    comment?.let {
                        commentList.add(it)
                    }
                }
                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error getting comments", databaseError.toException())
            }
        })
    }

    companion object {
        private const val TAG = "DetailSocietyActivity"
    }

    private fun getCurrentUserNameFromDatabase(commentText: String) {
        val userId = getCurrentUserId()
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userName = dataSnapshot.child("username").getValue(String::class.java)
                userName?.let {
                    postComment(commentText, it)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error getting current user's username", databaseError.toException())
            }
        })
    }

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
    }

}
