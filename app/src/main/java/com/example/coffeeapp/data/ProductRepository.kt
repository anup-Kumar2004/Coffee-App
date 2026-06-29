package com.example.coffeeapp.data

import com.example.coffeeapp.model.Category
import com.example.coffeeapp.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

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

    /**
     * Atomically updates a product's rating using a Firestore transaction.
     *
     * Why a transaction and not a simple update?
     * We need to READ the current rating and totalRatings, compute new values,
     * then WRITE them back — all as one atomic operation. If two users rate
     * the same product simultaneously, a plain update would cause a race
     * condition and one rating would be lost. A transaction retries
     * automatically if a conflict is detected, so no rating is ever dropped.
     *
     * The new rating is rounded to one decimal place before storing,
     * so Firestore always holds a clean value like 4.7, never 4.6923.
     *
     * After a successful write we also patch the in-memory cache so the
     * UI reflects the change immediately without a full network reload.
     */
    suspend fun updateProductRating(productId: String, userRating: Int): Result<Unit> {
        return try {
            val docRef = firestore.collection("products").document(productId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)

                val oldRating = snapshot.getDouble("rating") ?: 0.0
                val oldTotalRatings = snapshot.getLong("totalRatings")?.toInt() ?: 0

                val newTotalRatings = oldTotalRatings + 1
                val rawNewRating = ((oldRating * oldTotalRatings) + userRating) / newTotalRatings

                // Round to one decimal — never store 4.6923, only 4.7
                val roundedNewRating = (rawNewRating * 10).roundToInt() / 10.0

                transaction.update(
                    docRef, mapOf(
                        "rating" to roundedNewRating,
                        "totalRatings" to newTotalRatings
                    )
                )

                // Return the new values so we can patch the cache below
                Pair(roundedNewRating, newTotalRatings)
            }.await().let { (newRating, newTotal) ->
                // Patch the in-memory cache so HomeScreen reflects the new
                // rating on the next recomposition without a full network fetch
                cachedProducts = cachedProducts?.map { product ->
                    if (product.id == productId) {
                        product.copy(rating = newRating, totalRatings = newTotal)
                    } else {
                        product
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clears the in-memory product and category cache.
     * Call this when you need the next getProducts() call to fetch
     * fresh data from Firestore — for example after navigating back
     * to HomeScreen following a rating submission.
     */
    fun clearCache() {
        cachedProducts = null
        cachedCategories = null
    }

    fun getCachedProducts(): List<Product> {
        return cachedProducts ?: emptyList()
    }

    fun getProductById(id: String): Product? {
        return cachedProducts?.find { it.id == id }
    }
}