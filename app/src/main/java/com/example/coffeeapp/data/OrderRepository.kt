package com.example.coffeeapp.data

import com.example.coffeeapp.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    suspend fun placeOrder(order: Order): Result<String> {
        return try {
            val docRef = firestore.collection("orders").document()
            val orderWithId = order.copy(orderId = docRef.id)
            docRef.set(orderWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val snapshot = firestore
                .collection("orders")
                .document(orderId)
                .get()
                .await()
            val order = snapshot.toObject(Order::class.java)
                ?: return Result.failure(Exception("Order not found"))
            Result.success(order.copy(orderId = snapshot.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            firestore
                .collection("orders")
                .document(orderId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sets rated=true on the order document in Firestore.
     * Called after the user successfully submits product ratings so we
     * can prevent a second rating submission if they return to this screen.
     * Silent failure is intentional — if this fails, the worst outcome is
     * the user sees the rating form again on next visit, but RatingState.Submitted
     * in the ViewModel still prevents a double submission in the current session.
     */
    suspend fun markOrderAsRated(orderId: String) {
        try {
            firestore
                .collection("orders")
                .document(orderId)
                .update("rated", true)
                .await()
        } catch (_: Exception) {
            // Silent failure — not critical enough to surface to the user
        }
    }

    fun listenToOrder(orderId: String): Flow<Result<Order>> = callbackFlow {
        val listenerRegistration = firestore
            .collection("orders")
            .document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val order = snapshot?.toObject(Order::class.java)?.copy(orderId = snapshot.id)
                if (order != null) {
                    trySend(Result.success(order))
                } else {
                    trySend(Result.failure(Exception("Order not found")))
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun submitSupportTicket(
        orderId: String,
        issueType: String,
        description: String
    ): Result<Unit> {
        return try {
            val ticket = hashMapOf(
                "orderId" to orderId,
                "userId" to getCurrentUserId(),
                "issueType" to issueType,
                "description" to description,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            firestore
                .collection("support_tickets")
                .document()
                .set(ticket)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}