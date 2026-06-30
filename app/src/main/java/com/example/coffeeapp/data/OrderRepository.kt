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

    fun listenToActivePendingOrder(): Flow<Order?> = callbackFlow {
        val uid = getCurrentUserId()
        if (uid.isEmpty()) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }

        val listenerRegistration = firestore
            .collection("orders")
            .whereEqualTo("userId", uid)
            .whereEqualTo("status", "pending")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val order = snapshot?.documents?.firstOrNull()?.let { doc ->
                    doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                }
                trySend(order)
            }
        awaitClose { listenerRegistration.remove() }
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