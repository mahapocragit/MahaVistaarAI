package `in`.gov.mahapocra.mahavistaarai.util.helpers

import com.google.firebase.messaging.FirebaseMessaging

object FirebaseTopicHelper {
    fun subscribeToTopic(topic: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance()
            .subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    fun unSubscribeToTopic(topic: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }
}