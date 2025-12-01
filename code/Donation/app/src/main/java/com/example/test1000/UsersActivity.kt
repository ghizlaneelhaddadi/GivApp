package com.example.test1000

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test1000.adapter.UserAdapter
import com.example.test1000.data.Datasource
import com.example.test1000.databinding.ActivityUsersBinding
import com.example.test1000.model.Users
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase

class UsersActivity: AppCompatActivity(), UserClickListener {
    private lateinit var binding: ActivityUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
       binding.backButton.setOnClickListener {
           onBackPressed()
       }
        setupUserRecyclerView()
    }

    private fun setupUserRecyclerView() {
        Datasource().loadUsers { users ->
            binding.recycleuser.apply {
                layoutManager = LinearLayoutManager(this@UsersActivity)
                adapter = UserAdapter(this@UsersActivity, users, this@UsersActivity)
                setHasFixedSize(true)
            }
        }
    }

    private fun show(user: Users) {
        val bottomSheetView = layoutInflater.inflate(R.layout.buttom_sheet_users, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)

        val buttonsup = bottomSheetView.findViewById<Button>(R.id.supprimer)
        val buttonrendreadmin = bottomSheetView.findViewById<Button>(R.id.rendre_admin)
        buttonrendreadmin.setOnClickListener {
            makeAdmin(user)
            dialog.dismiss()
        }
        buttonsup.setOnClickListener {
            deleteUser(user)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteUser(user: Users) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users")

        databaseRef.child(user.username)
            .removeValue()
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
            }
    }


    private fun makeAdmin(user: Users) {
    }


    override fun onItemClick(user: Users) {
        show(user)
    }
}