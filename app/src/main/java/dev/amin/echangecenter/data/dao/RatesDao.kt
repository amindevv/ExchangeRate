package dev.amin.echangecenter.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.amin.echangecenter.data.models.Rates

@Dao
interface RatesDao {

    @Query("Select * from rates")
    fun getLatesLive(): List<Rates>

    @Insert
    fun insertRate(rates: Rates)

    @Query("Delete from rates")
    fun flush()
}