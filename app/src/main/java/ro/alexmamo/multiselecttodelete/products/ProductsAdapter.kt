package ro.alexmamo.multiselecttodelete.products

import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product.view.*
import ro.alexmamo.multiselecttodelete.BR
import ro.alexmamo.multiselecttodelete.R
import ro.alexmamo.multiselecttodelete.data.Product
import ro.alexmamo.multiselecttodelete.databinding.ProductDataBinding
import ro.alexmamo.multiselecttodelete.products.ProductsAdapter.ProductViewHolder
import ro.alexmamo.multiselecttodelete.utils.ClickHandler
import ro.alexmamo.multiselecttodelete.utils.OnEventListener

class ProductsAdapter(
        val productList: MutableList<Product> = mutableListOf(),
        private val onEventListener: OnEventListener
): RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = ProductDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ProductViewHolder(dataBinding, onEventListener)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bindProduct(product, position)
    }

    override fun getItemCount() = productList.size

    fun getSelectedProductList() = productList.filter { p -> p.isSelected }

    inner class ProductViewHolder(
            private val dataBinding: ViewDataBinding,
            private val onEventListener: OnEventListener
    ): RecyclerView.ViewHolder(dataBinding.root) {
        fun bindProduct(product: Product, position: Int) {
            if (product.isSelected) {
                selectView(dataBinding.root)
            } else {
                deselectView(dataBinding.root)
            }
            dataBinding.setVariable(BR.product, product)
            dataBinding.setVariable(BR.position, position)
            dataBinding.setVariable(BR.clickHandler, ClickHandler(onEventListener))
        }

        private fun selectView(rootView: View) {
            val color = getColor(rootView.context, R.color.colorProductSelected)
            rootView.product_container.setBackgroundColor(color)
            rootView.selected_image_view.visibility = VISIBLE
        }

        private fun deselectView(rootView: View) {
            val color = getColor(rootView.context, R.color.colorWhite)
            rootView.product_container.setBackgroundColor(color)
            rootView.selected_image_view.visibility = INVISIBLE
        }
    }
}