import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            it.body?.let { message ->
                saveNotificationToFirestore(it.title ?: "Notification", message)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
    }

    private fun saveNotificationToFirestore(title: String, message: String) {
        val notificationData = hashMapOf(
            "title" to title,
            "message" to message
        )

        firestore.collection("notifications")
            .add(notificationData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Notification added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding notification", e)
            }
    }
}
