package ro.alexmamo.multiselecttodelete.products

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class ProductsViewModel @ViewModelInject constructor(
    private val repository: ProductsRepository
): ViewModel() {
    val productListLiveData = liveData(Dispatchers.IO) {
        emit(repository.getProductListFromFirestore())
    }

    fun deleteProduct(id: String) = liveData(Dispatchers.IO) {
        emit(repository.deleteProductInFirestore(id))
    }
}