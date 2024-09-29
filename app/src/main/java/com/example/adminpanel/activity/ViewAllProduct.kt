package com.example.adminpanel.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminpanel.adapter.AllProduct
import com.example.adminpanel.databinding.ActivityViewAllProductBinding
import com.example.adminpanel.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewAllProduct : AppCompatActivity() {
    private lateinit var binding: ActivityViewAllProductBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var productList: ArrayList<Product>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        productList = ArrayList()
        retrieveProductDetails()
        setOnClick()
    }

    private fun setOnClick() {
        binding.back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Optional: Use this to finish the current activity
        }
    }

    private fun retrieveProductDetails() {
        // Show the ProgressBar
        binding.progressBar.visibility = View.VISIBLE
        val itemRef = database.reference.child("product")
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (i in snapshot.children) {
                    val data = i.getValue(Product::class.java)
                    data?.let {
                        productList.add(it)
                    }
                }
                setAdapter()
                // Hide the ProgressBar after data is loaded
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Hide the ProgressBar in case of an error
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun setAdapter() {
        val adapter = AllProduct(this,productList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}