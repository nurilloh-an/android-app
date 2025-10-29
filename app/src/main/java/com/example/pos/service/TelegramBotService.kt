package com.example.pos.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pos.MainActivity
import com.example.pos.R
import com.example.pos.data.Order
import com.example.pos.data.OrderStatus
import com.example.pos.data.Product
import com.example.pos.network.TelegramService
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class TelegramBotService(
    private val context: Context,
    private val onNewOrder: (Order) -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastUpdateId: Long = 0
    private val botToken = "YOUR_BOT_TOKEN" // Replace with your bot token
    
    private val telegramService: TelegramService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.telegram.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TelegramService::class.java)
    }

    fun start() {
        scope.launch {
            while (isActive) {
                try {
                    val updates = telegramService.getUpdates(
                        token = botToken,
                        offset = lastUpdateId + 1
                    )
                    
                    if (updates.ok) {
                        updates.result.forEach { update ->
                            update.update_id.takeIf { it > lastUpdateId }?.let { 
                                lastUpdateId = it 
                            }
                            
                            update.message?.let { message ->
                                handleMessage(message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle error
                    delay(5000) // Wait before retrying
                }
                delay(1000) // Polling interval
            }
        }
    }

    private suspend fun handleMessage(message: Message) {
        when (message.text) {
            "/start" -> {
                sendWelcomeMessage(message.chat.id)
            }
            "/menu" -> {
                // Send menu with products
                sendMainMenu(message.chat.id)
            }
            else -> {
                // Handle other messages or commands
            }
        }
    }

    private suspend fun sendWelcomeMessage(chatId: Long) {
        val welcomeText = """
            🛍️ *Welcome to Our Store!* 🛍️
            
            Use the following commands:
            /menu - Browse our products
            /order - Place a new order
            /help - Show this help message
        """.trimIndent()
        
        telegramService.sendMessage(
            token = botToken,
            chatId = chatId,
            text = welcomeText,
            parseMode = "Markdown"
        )
    }

    private suspend fun sendMainMenu(chatId: Long) {
        val menuText = """
            *🏪 Our Menu*
            
            *1. Drinks*
            1.1 Cola - $2.00
            1.2 Pepsi - $2.00
            1.3 Water - $1.00
            
            *2. Food*
            2.1 Pizza - $10.00
            2.2 Burger - $8.00
            
            *How to order?*
            Send /order to start a new order.
        """.trimIndent()
        
        telegramService.sendMessage(
            token = botToken,
            chatId = chatId,
            text = menuText,
            parseMode = "Markdown"
        )
    }

    fun notifyNewOrder(order: Order) {
        val notificationId = Random().nextInt(10000)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("order_id", order.id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New Order #${order.id.takeLast(4)}")
            .setContentText("${order.items.size} items - $${order.total}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
        
        // Also send to Telegram
        scope.launch {
            sendOrderNotificationToAdmin(order)
        }
    }
    
    private suspend fun sendOrderNotificationToAdmin(order: Order) {
        val orderText = """
            *🆕 New Order #${order.id.takeLast(4)}*
            
            *Customer:* ${order.customerName}
            *Phone:* ${order.phoneNumber}
            *Address:* ${order.address}
            
            *Items:*
            ${order.items.joinToString("\n") { "- ${it.quantity}x ${it.productName} - $${it.price * it.quantity}" }}
            
            *Total: $${order.total}*
            
            Status: ${order.status}
        """.trimIndent()
        
        telegramService.sendMessage(
            token = botToken,
            chatId = YOUR_ADMIN_CHAT_ID, // Replace with your admin chat ID
            text = orderText,
            parseMode = "Markdown"
        )
    }

    fun stop() {
        scope.cancel()
    }

    companion object {
        private const val CHANNEL_ID = "order_notifications"
        private const val NOTIFICATION_ID = 1
        
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Order Notifications"
                val descriptionText = "Notifications for new orders"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
