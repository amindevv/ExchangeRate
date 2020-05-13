package dev.amin.echangecenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.amin.echangecenter.data.repositories.MainActivityRepository
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivityViewModel(
    private val repo: MainActivityRepository
) : ViewModel() {

    fun getExchangeRates(baseCurrency: String = repo.baseCurrency) {

        repo.baseCurrency = baseCurrency
        repo.startUpdates()
    }

    fun stopUpdates() {

        repo.stopUpdates()
    }

    class Factory(private val repo: MainActivityRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return  MainActivityViewModel(repo) as T
        }
    }
}