package com.example.coffeeapp.data

import com.example.coffeeapp.model.Category
import com.example.coffeeapp.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private var cachedProducts: List<Product>? = null
    private var cachedCategories: List<Category>? = null

    suspend fun getProducts(): Result<List<Product>> {
        cachedProducts?.let { return Result.success(it) }
        return try {
            val snapshot = firestore
                .collection("products")
                .get()
                .await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            cachedProducts = products
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeaturedProducts(): Result<List<Product>> {
        cachedProducts?.let { products ->
            return Result.success(products.filter { it.isFeatured })
        }
        return try {
            val snapshot = firestore
                .collection("products")
                .whereEqualTo("isFeatured", true)
                .get()
                .await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        cachedProducts?.let { products ->
            return Result.success(products.filter { it.category == category })
        }
        return try {
            val snapshot = firestore
                .collection("products")
                .whereEqualTo("category", category)
                .get()
                .await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<Category>> {
        cachedCategories?.let { return Result.success(it) }
        return try {
            val snapshot = firestore
                .collection("categories")
                .orderBy("order")
                .get()
                .await()
            val categories = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Category::class.java)?.copy(id = doc.id)
            }
            cachedCategories = categories
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCachedProducts(): List<Product> {
        return cachedProducts ?: emptyList()
    }
}