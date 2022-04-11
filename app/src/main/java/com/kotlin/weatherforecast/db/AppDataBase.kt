package com.kotlin.weatherforecast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kotlin.weatherforecast.model.AlertsModel
import com.kotlin.weatherforecast.model.FavLocationsModel

@Database(entities = [FavLocationsModel::class, AlertsModel::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        private var instance: AppDataBase? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDataBase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, AppDataBase::class.java,
                    "fav locations"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!

        }

    }
}