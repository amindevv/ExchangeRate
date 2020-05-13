package dev.amin.echangecenter.core

import android.content.Context
import androidx.room.*
import dev.amin.echangecenter.data.dao.RatesDao
import dev.amin.echangecenter.data.models.Rates

@Database(entities = [Rates::class], version = 1)
@TypeConverters(DBTypeConverters::class)
abstract class AppDb : RoomDatabase() {

    abstract fun ratesDao(): RatesDao

    companion object {
        @Volatile
        private var instance: AppDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDb::class.java, "exchange-center.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}