package com.example.foodinfo

import androidx.room.*

@Dao
interface FoodDAO {
    @Query("SELECT * from foodinfo ORDER BY id DESC")
    fun getAll():List<Food>

    @Query("SELECT *from foodinfo ORDER BY id DESC LIMIT 1")
    fun getLatestWord():Food

    @Query("SELECT * FROM foodinfo WHERE date = :findDate")
    fun getFoodByName(findDate: String): MutableList<Food>


    @Query("SELECT * FROM foodinfo WHERE datemonth = :findDate")
    fun getFoodByMonth(findDate: String): MutableList<Food>

    @Query("SELECT * FROM foodinfo WHERE id = :findDate")
    fun getFoodid(findDate: Int): Food


    @Insert
    fun insert(word:Food)

    @Delete
    fun delete(word:Food)

    @Update
    fun update(word:Food)

    @Query("DELETE FROM foodinfo")
    fun deleteAll()
}