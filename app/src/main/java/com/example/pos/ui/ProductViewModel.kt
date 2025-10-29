package com.example.pos.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.pos.data.AppDatabase
import com.example.pos.data.Product
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val productDao = database.productDao()
    
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()
    val productTypes: LiveData<List<String>> = productDao.getProductTypes()
    
    fun getProductsByType(type: String): LiveData<List<Product>> {
        return productDao.getProductsByType(type)
    }
    
    fun insert(product: Product) = viewModelScope.launch {
        productDao.insert(product)
    }
    
    fun update(product: Product) = viewModelScope.launch {
        productDao.update(product)
    }
    
    fun delete(product: Product) = viewModelScope.launch {
        productDao.delete(product)
    }
}
