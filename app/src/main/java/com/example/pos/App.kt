package com.example.pos

import android.app.Application
import com.example.pos.data.AppDatabase

class PosApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
