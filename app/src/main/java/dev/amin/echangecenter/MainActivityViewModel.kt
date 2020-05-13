package dev.amin.echangecenter

import androidx.lifecycle.ViewModel
import dev.amin.echangecenter.data.repositories.MainActivityRepository
class MainActivityViewModel : ViewModel() {

    private val repo = MainActivityRepository()

    fun getExchangeRates(baseCurrency: String = repo.baseCurrency) {

        repo.baseCurrency = baseCurrency
        repo.startUpdates()
    }

    fun stopUpdates() {

        repo.stopUpdates()
    }
}