package ro.alexmamo.multiselecttodelete.products

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import dagger.hilt.android.AndroidEntryPoint
import ro.alexmamo.multiselecttodelete.R
import ro.alexmamo.multiselecttodelete.data.Product
import ro.alexmamo.multiselecttodelete.databinding.ActivityProductsBinding
import ro.alexmamo.multiselecttodelete.utils.General.Companion.displayMenuItem
import ro.alexmamo.multiselecttodelete.utils.General.Companion.displayProgressBar
import ro.alexmamo.multiselecttodelete.utils.General.Companion.hideMenuItem
import ro.alexmamo.multiselecttodelete.utils.General.Companion.hideProgressBar
import ro.alexmamo.multiselecttodelete.utils.General.Companion.logErrorMessage
import ro.alexmamo.multiselecttodelete.utils.General.Companion.vibrate
import ro.alexmamo.multiselecttodelete.utils.OnEventListener

@AndroidEntryPoint
class ProductsActivity : AppCompatActivity(), OnEventListener {
    private lateinit var dataBinding: ActivityProductsBinding
    private lateinit var deleteItem: MenuItem
    private val adapter = ProductsAdapter(mutableListOf(), this)
    private val viewModel: ProductsViewModel by viewModels()
    private var selectionModeEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = setContentView(this, R.layout.activity_products)
        setProductsAdapter()
        getProductList()
    }

    private fun setProductsAdapter() {
        dataBinding.productsRecyclerView.adapter = adapter
    }

    private fun getProductList() {
        viewModel.productListLiveData.observe(this, { dataOrException ->
            val productList = dataOrException.data
            if (productList != null) {
                if (adapter.productList.isNotEmpty()) {
                    productList.clear()
                }
                adapter.productList.addAll(productList)
                adapter.notifyDataSetChanged()
                hideProgressBar(dataBinding.progressBar)
            }

            if (dataOrException.e != null) {
                logErrorMessage(dataOrException.e!!.message!!)
            }
        })
    }

    override fun onClick(position: Int) {
        if (selectionModeEnabled) {
            startSelection(position)
        }
    }

    override fun onLongClick(position: Int) {
        vibrate(this)
        selectionModeEnabled = true
        startSelection(position)
    }

    private fun startSelection(position: Int) {
        if (!adapter.productList[position].isSelected) {
            selectProductAt(position)
        } else {
            deselectProductAt(position)
        }
        if (adapter.productList.none { p -> p.isSelected }) {
            setDefaultToolbar()
            selectionModeEnabled = false
        } else {
            setDeleteToolBar()
        }
    }

    private fun setDefaultToolbar() {
        title = resources.getString(R.string.app_name)
        hideMenuItem(deleteItem)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    private fun setDeleteToolBar() {
        title = adapter.getSelectedProductList().size.toString()
        displayMenuItem(deleteItem)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun selectProductAt(position: Int) {
        adapter.productList[position].isSelected = true
        adapter.notifyItemChanged(position)
    }

    private fun deselectProductAt(position: Int) {
        adapter.productList[position].isSelected = false
        adapter.notifyItemChanged(position)
    }

    private fun deselectAllProducts() {
        adapter.productList.forEachIndexed { position, product ->
            if (product.isSelected) {
                deselectProductAt(position)
            }
        }
        setDefaultToolbar()
        selectionModeEnabled = false
    }

    private fun deleteSelectedProducts() {
        val selectedProductList = adapter.getSelectedProductList()
        if (selectedProductList.isNotEmpty()) {
            val firstSelectedProduct = selectedProductList[0]
            val position = adapter.productList.indexOf(firstSelectedProduct)
            deleteProduct(firstSelectedProduct, position)
        }
    }

    private fun deleteProduct(product: Product, position: Int) {
        displayProgressBar(dataBinding.progressBar)
        val isProductDeletedLiveData = viewModel.deleteProduct(product.id!!)
        isProductDeletedLiveData.observe(this) { dataOrException ->
            val isProductDeleted = dataOrException.data
            if (isProductDeleted != null) {
                deselectProductAt(position)
                adapter.productList.remove(product)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, adapter.productList.size)
                if (adapter.getSelectedProductList().isEmpty()) {
                    hideProgressBar(dataBinding.progressBar)
                    setDefaultToolbar()
                    selectionModeEnabled = false
                }
                deleteSelectedProducts()
            }

            if (dataOrException.e != null) {
                logErrorMessage(dataOrException.e!!.message!!)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        deleteItem = menu.findItem(R.id.delete)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> deselectAllProducts()
            R.id.delete -> deleteSelectedProducts()
        }
        return super.onOptionsItemSelected(item)
    }
}