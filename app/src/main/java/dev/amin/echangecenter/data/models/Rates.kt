package dev.amin.echangecenter.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "rates")
class Rates(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var dateCreated: Date = Date(),

    /* This is just a UI feature, may
        someone prefers to hide some pairs */
    val isVisible: Boolean = true,

    val baseCurrency: String = "",
    val rates: HashMap<String, Double> = hashMapOf()
)