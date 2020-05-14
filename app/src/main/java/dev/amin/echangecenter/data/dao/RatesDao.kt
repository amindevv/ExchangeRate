package dev.amin.echangecenter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.amin.echangecenter.data.models.Rates

@Dao
interface RatesDao {

    @Query("Select * from rates WHERE id = (SELECT MAX(id) from rates)")
    fun getLiveRates(): LiveData<Rates>

    @Insert
    fun insertRate(rates: Rates)

    @Query("Delete from rates")
    fun flush()
}