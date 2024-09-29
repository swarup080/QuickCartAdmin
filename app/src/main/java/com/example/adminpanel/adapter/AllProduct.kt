package com.example.adminpanel.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminpanel.databinding.AllProductUiBinding
import com.example.adminpanel.models.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AllProduct(
    private val context: Context,
    private val productList: ArrayList<Product>,
) :
    RecyclerView.Adapter<AllProduct.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllProduct.ViewHolder {
        val binding =
            AllProductUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllProduct.ViewHolder, position: Int) {
        holder.bind(productList[position])

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(private val binding: AllProductUiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                val uriString = product.productImage
                val uri = Uri.parse(uriString)

                Glide.with(root.context).load(uri).into(productImage)
                productName.text = product.productName
                productPrice.text = "₹${product.productPrice}"
                productModelName.text = "Model: ${product.productModelName}"
                unitPrice.text = "Unit Price: ${product.productPrice}"

                delete.setOnClickListener {
                    deleteItem(adapterPosition, product.id)

                }
            }
        }

        private fun deleteItem(position: Int, productId: String?) {
            productId?.let {
                // Obtain a reference to the product node in the Firebase database
                val databaseReference = FirebaseDatabase.getInstance().getReference("product")

                // Remove the item from the Firebase database
                databaseReference.child(it).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If successful, remove the item from the list and notify the adapter
                        productList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, productList.size)
                    } else {
                        // Handle failure (e.g., by showing a Toast message)
                        // Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}