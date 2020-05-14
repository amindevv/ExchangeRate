package dev.amin.echangecenter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.amin.echangecenter.data.repositories.MainActivityRepository


class MainActivityViewModel(
    private val repo: MainActivityRepository
) : ViewModel() {

    val rates = repo.rates

    fun getExchangeRates(baseCurrency: String = repo.baseCurrency) {

        repo.baseCurrency = baseCurrency
        repo.startUpdates()
    }

    fun stopUpdates() {

        repo.stopUpdates()
    }

    class Factory(private val repo: MainActivityRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(repo) as T
        }
    }
}