package dev.amin.echangecenter.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "rates")
class Rates(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var dateCreated: Date = Date(System.currentTimeMillis()),

    val baseCurrency: String = "",
    val rates: List<RateEntry> = listOf()
)