package com.example.papb

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ToDoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun addToDoItem(item: ToDoItem) {
        val userId = getUserId() ?: return
        db.collection("users")
            .document(userId)
            .collection("todos")
            .document(item.id)
            .set(item)
            .await()
    }

    suspend fun updateToDoItem(item: ToDoItem) {
        val userId = getUserId() ?: return
        db.collection("users")
            .document(userId)
            .collection("todos")
            .document(item.id)
            .set(item)
            .await()
    }

    suspend fun deleteToDoItem(itemId: String) {
        val userId = getUserId() ?: return
        db.collection("users")
            .document(userId)
            .collection("todos")
            .document(itemId)
            .delete()
            .await()
    }

    suspend fun getToDoItems(): List<ToDoItem> {
        val userId = getUserId() ?: return emptyList()
        val result = db.collection("users")
            .document(userId)
            .collection("todos")
            .get()
            .await()
        return result.documents.mapNotNull { doc ->
            doc.toObject(ToDoItem::class.java)
        }
    }

    fun getToDoItemsFlow(): Flow<List<ToDoItem>> = callbackFlow {
        val userId = getUserId() ?: return@callbackFlow

        val listener = db.collection("users")
            .document(userId)
            .collection("todos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ToDoItem::class.java)
                } ?: emptyList()

                trySend(items)
            }

        // Cleanup listener when flow is cancelled
        awaitClose { listener.remove() }
    }
}
