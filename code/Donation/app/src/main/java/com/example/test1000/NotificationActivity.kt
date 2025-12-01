package com.example.test1000
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1000.adapters.NotificationAdapter
import com.example.test1000.model.Notification
import com.google.firebase.firestore.FirebaseFirestore

class NotificationActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var noNotificationsLayout: LinearLayout
    private lateinit var notificationsList: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        backButton = findViewById(R.id.back_button)
        noNotificationsLayout = findViewById(R.id.no_notifications_layout)
        notificationsList = findViewById(R.id.notifications_list)
        noNotificationsLayout.visibility = View.VISIBLE
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        getNotificationsFromFirestore()
    }

    private fun getNotificationsFromFirestore() {
        firestore.collection("notifications")
            .get()
            .addOnSuccessListener { documents ->
                val notifications = mutableListOf<Notification>()
                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val message = document.getString("message") ?: ""
                    notifications.add(Notification(title, message))
                }
                if (notifications.isNotEmpty()) {
                    noNotificationsLayout.visibility = View.GONE
                    notificationsList.visibility = View.VISIBLE
                    setupRecyclerView(notifications)
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun setupRecyclerView(notifications: List<Notification>) {
        notificationsList.layoutManager = LinearLayoutManager(this)
        notificationAdapter = NotificationAdapter(notifications)
        notificationsList.adapter = notificationAdapter
    }
}
