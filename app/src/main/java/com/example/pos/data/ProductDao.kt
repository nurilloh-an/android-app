package com.example.pos.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY type, name")
    fun getAllProducts(): LiveData<List<Product>>
    
    @Query("SELECT DISTINCT type FROM products")
    fun getProductTypes(): LiveData<List<String>>
    
    @Query("SELECT * FROM products WHERE type = :type")
    fun getProductsByType(type: String): LiveData<List<Product>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long
    
    @Update
    suspend fun update(product: Product)
    
    @Delete
    suspend fun delete(product: Product)
    
    @Query("DELETE FROM products")
    suspend fun deleteAll()
}
