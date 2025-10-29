package com.example.pos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val price: Double,
    val weight: Double,
    val amount: Int,
    val imageUri: String? = null
)
