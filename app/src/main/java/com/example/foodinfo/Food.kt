package com.example.foodinfo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream


@Entity(tableName = "foodinfo")
data class Food (
    var address:String,
    var typemeal:String,
    var date:String,
    var datetime:String,
    var datemonth:String,
    var foodname:String,
    var imageuri: ByteArray,
    var review:String,
    var price:Int,
    var cal:Int,
    @PrimaryKey(autoGenerate = true) val id : Int = 0
        )



    // Bitmap -> ByteArray 변환

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

    // ByteArray -> Bitmap 변환

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}