package ro.alexmamo.multiselecttodelete.products

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query.Direction.ASCENDING
import kotlinx.coroutines.tasks.await
import ro.alexmamo.multiselecttodelete.data.DataOrException
import ro.alexmamo.multiselecttodelete.data.Product
import ro.alexmamo.multiselecttodelete.utils.Constants.NAME_PROPERTY
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(
    private val productsRef: CollectionReference
) {
    suspend fun getProductListFromFirestore(): DataOrException<MutableList<Product>, Exception> {
        val dataOrException = DataOrException<MutableList<Product>, Exception>()
        try {
            val productList = mutableListOf<Product>()
            val products = productsRef.orderBy(NAME_PROPERTY, ASCENDING).get().await()
            for (document in products) {
                document.toObject(Product::class.java).let {
                    productList.add(it)
                }
            }
            dataOrException.data = productList
        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e
        }
        return dataOrException
    }

    suspend fun deleteProductInFirestore(id: String): DataOrException<Boolean, Exception> {
        val dataOrException = DataOrException<Boolean, Exception>()
        try {
            productsRef.document(id).delete().await()
            dataOrException.data = true
        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e
        }
        return dataOrException
    }
}