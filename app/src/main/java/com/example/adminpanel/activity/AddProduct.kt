package com.example.adminpanel.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.adminpanel.databinding.ActivityAddProductBinding
import com.example.adminpanel.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddProduct : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding

    private var productImage: Uri? = null
    private lateinit var productName: String
    private lateinit var productPrice: String
    private lateinit var productRating: String
    private lateinit var productDescription: String
    private lateinit var productModelName: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializedFirebase()
        setOnClick()
    }

    private fun initializedFirebase() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    private fun setOnClick() {
        binding.productImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Optional: Use this to finish the current activity
        }
        binding.add.setOnClickListener {
            productName = binding.productName.text.toString().trim()
            productPrice = binding.productPrice.text.toString().trim()
            productRating = binding.productRating.text.toString().trim()
            productDescription = binding.productDescription.text.toString().trim()
            productModelName = binding.modelName.text.toString().trim()

            if (!(productName.isBlank() || productPrice.isBlank() || productRating.isBlank() || productDescription.isBlank() || productModelName.isBlank())) {
                uploadData()
                Toast.makeText(this@AddProduct, "Product Added Successfully", Toast.LENGTH_SHORT)
                    .show()
                finish()
            } else {
                Toast.makeText(this@AddProduct, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadData() {
        val productRef = database.getReference("product")
        val itemKey = productRef.push().key

        if (productImage != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("product_images/$itemKey.jpg")
            val uploadTask = imageRef.putFile(productImage!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val product = Product(
                        itemKey,
                        productImage = downloadUrl.toString(),
                        productName,
                        productPrice,
                        productRating,
                        productDescription,
                        productModelName
                    )
                    itemKey?.let { key ->
                        productRef.child(key).setValue(product)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Product Added Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.addedImage.setImageURI(it)
                productImage = it
            }
        }
}