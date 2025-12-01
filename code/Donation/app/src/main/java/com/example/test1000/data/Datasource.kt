package com.example.test1000.data

import android.annotation.SuppressLint
import com.google.firebase.database.*
import com.example.test1000.model.Events
import com.example.test1000.model.Users
import com.example.test1000.model.societies

class Datasource {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun loadEvents(callback: (List<Events>) -> Unit) {
        val eventsRef = database.child("events")
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Events>()
                for (eventSnapshot in snapshot.children) {
                    val date = eventSnapshot.child("date").getValue(String::class.java)?:""
                    val name = eventSnapshot.child("name").getValue(String::class.java) ?: ""
                    val details = eventSnapshot.child("details").getValue(String::class.java) ?: ""
                    val downloadUrl = eventSnapshot.child("imageResource").value.toString()
                    val event = Events(name, downloadUrl,details,date)
                    events.add(event)
                }
                callback(events)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun loadSocieties(category: String, callback: (List<societies>) -> Unit) {
        val societiesRef = database.child("societies").child(category)
        societiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val societiesList = mutableListOf<societies>()
                for (societySnapshot in snapshot.children) {
                    val name = societySnapshot.child("name").getValue(String::class.java) ?: ""
                    val description = societySnapshot.child("description").getValue(String::class.java) ?: ""
                    val accumulator = societySnapshot.child("accumulator").getValue(Int::class.java) ?: 0
                    val goalsociety = societySnapshot.child("goalsociety").getValue(Int::class.java) ?: 1000000
                    val downloadUrl = societySnapshot.child("image").value.toString()
                    val address = societySnapshot.child("contact").child("address").getValue(String::class.java) ?: ""
                    val email = societySnapshot.child("contact").child("email").getValue(String::class.java) ?: ""
                    val phone = societySnapshot.child("contact").child("phone").getValue(String::class.java) ?: ""
                    val facebook = societySnapshot.child("social_media").child("facebook").getValue(String::class.java)?: ""
                    val instagram = societySnapshot.child("social_media").child("instagram").getValue(String::class.java)?: ""
                    val twitter = societySnapshot.child("social_media").child("twitter").getValue(String::class.java)?: ""
                    val site = societySnapshot.child("website").getValue(String::class.java)?:""
                    val society = societies(name, description, downloadUrl, category, accumulator, goalsociety, address, email, phone, facebook,instagram,twitter)
                    societiesList.add(society)
                }
                callback(societiesList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

fun loadUsers(callback: (List<Users>) -> Unit){
    val usersRef = database.child("users")
    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val users = mutableListOf<Users>()
            for (userSnapshot in snapshot.children) {
                val username = userSnapshot.child("username").getValue(String::class.java)?:""
                val phone = userSnapshot.child("telephone").getValue(String::class.java) ?: ""
                val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                val image = userSnapshot.child("image").value.toString()
                val nationality = userSnapshot.child("nationality").getValue(String::class.java)?:""
                val status = userSnapshot.child("status").getValue(String::class.java)?:""
                val adresse = userSnapshot.child("adresse").getValue(String::class.java)?:""
                val user = Users(username, phone,image,email,nationality,status,adresse)
                users.add(user)
            }
            callback(users)
        }

        override fun onCancelled(error: DatabaseError) {
        }
    })
}

    fun filterSocieties(category: String, query: String, callback: (List<societies>) -> Unit) {
        val societiesRef = database.child("societies").child(category)
        societiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val filteredSocieties = mutableListOf<societies>()
                for (societySnapshot in snapshot.children) {
                    val name = societySnapshot.child("name").getValue(String::class.java) ?: ""
                    val description = societySnapshot.child("description").getValue(String::class.java) ?: ""
                    val downloadUrl = societySnapshot.child("image").value.toString()
                    val accumulator = societySnapshot.child("accumulator").getValue(Int::class.java) ?: 0
                    val goalsociety = societySnapshot.child("goalsociety").getValue(Int::class.java) ?: 1000000
                    val society = societies(name, description, downloadUrl, category,accumulator, goalsociety)
                    if (society.name.contains(query, ignoreCase = true) || society.description.contains(query, ignoreCase = true)) {
                        filteredSocieties.add(society)
                    }
                }
                callback(filteredSocieties)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }
}
