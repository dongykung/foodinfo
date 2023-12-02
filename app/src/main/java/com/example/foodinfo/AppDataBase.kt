package com.example.foodinfo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Food::class], version = 2)
abstract class AppDataBase : RoomDatabase(){

    abstract fun foodDao():FoodDAO
    companion object{
        private var INSTANCE : AppDataBase ?= null
        fun getInstance(context: Context) : AppDataBase?{
            if(INSTANCE==null){
                synchronized(AppDataBase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext, AppDataBase::class.java, "app-database.db"
                    ).fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}