package com.example.coffeeapp.data

import com.example.coffeeapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private var cachedUser: User? = null

    suspend fun getUser(): Result<User> {
        cachedUser?.let { return Result.success(it) }

        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No user logged in"))

            val snapshot = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("User data not found"))

            cachedUser = user.copy(uid = snapshot.id)
            Result.success(cachedUser!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearCache() {
        cachedUser = null
    }

}