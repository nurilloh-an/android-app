package com.example.pos.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val customerName: String,
    val phoneNumber: String,
    val address: String,
    val items: List<OrderItem>,
    val total: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val telegramChatId: Long? = null
)

data class OrderItem(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
