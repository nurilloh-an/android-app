package com.example.pos.network

import retrofit2.http.*

interface TelegramService {
    @FormUrlEncoded
    @POST("bot{token}/sendMessage")
    suspend fun sendMessage(
        @Path("token") token: String,
        @Field("chat_id") chatId: Long,
        @Field("text") text: String,
        @Field("parse_mode") parseMode: String = "HTML",
        @Field("reply_markup") replyMarkup: String? = null
    )

    @GET("bot{token}/getUpdates")
    suspend fun getUpdates(
        @Path("token") token: String,
        @Query("offset") offset: Long? = null,
        @Query("limit") limit: Int = 100,
        @Query("timeout") timeout: Int = 0
    ): TelegramResponse<Update>
}

data class TelegramResponse<T>(
    val ok: Boolean,
    val result: List<T>,
    val description: String? = null
)

data class Update(
    val update_id: Long,
    val message: Message? = null,
    val callback_query: CallbackQuery? = null
)

data class Message(
    val message_id: Long,
    val from: User,
    val chat: Chat,
    val date: Long,
    val text: String? = null,
    val contact: Contact? = null,
    val location: Location? = null
)

data class CallbackQuery(
    val id: String,
    val from: User,
    val message: Message? = null,
    val data: String? = null
)

data class User(
    val id: Long,
    val first_name: String,
    val last_name: String? = null,
    val username: String? = null,
    val is_bot: Boolean = false
)

data class Chat(
    val id: Long,
    val type: String,
    val first_name: String? = null,
    val last_name: String? = null,
    val username: String? = null
)

data class Contact(
    val phone_number: String,
    val first_name: String,
    val last_name: String? = null,
    val user_id: Long? = null
)

data class Location(
    val latitude: Double,
    val longitude: Double
)
