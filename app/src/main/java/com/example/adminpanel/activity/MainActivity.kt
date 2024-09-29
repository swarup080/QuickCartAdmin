package com.example.adminpanel.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.adminpanel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClick()
    }

    private fun setOnClick() {

        binding.addProduct.setOnClickListener {
            val intent = Intent(this@MainActivity, AddProduct::class.java).apply {
                putExtra("addProduct",binding.addPro.text)
            }
            startActivity(intent)
        }
        binding.viewAllProduct.setOnClickListener {
            val intent = Intent(this@MainActivity, ViewAllProduct::class.java)
            startActivity(intent)
        }
    }
}