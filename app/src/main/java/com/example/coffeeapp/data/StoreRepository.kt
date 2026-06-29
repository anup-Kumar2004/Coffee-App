package com.example.coffeeapp.data

import com.example.coffeeapp.model.Store
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private var cachedStores: List<Store>? = null

    suspend fun getStores(): Result<List<Store>> {
        cachedStores?.let { return Result.success(it) }
        return try {
            val snapshot = firestore
                .collection("stores")
                .get()
                .await()
            val stores = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Store::class.java)?.copy(id = doc.id)
            }
            cachedStores = stores
            Result.success(stores)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCachedStores(): List<Store> {
        return cachedStores ?: emptyList()
    }

    fun getStoreById(id: String): Store? {
        return cachedStores?.find { it.id == id }
    }
}