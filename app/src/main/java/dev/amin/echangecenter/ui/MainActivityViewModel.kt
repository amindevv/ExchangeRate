package dev.amin.echangecenter.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.amin.echangecenter.data.RequestStatus
import dev.amin.echangecenter.data.models.Rates
import dev.amin.echangecenter.data.repositories.MainActivityRepository
import java.util.*


class MainActivityViewModel(
    private val repo: MainActivityRepository
) : ViewModel() {

    private val repoRates = repo.rates

    val requestStatus = repo.requestStatus

    private val repoRatesObserver = Observer<Rates> {

        /* This is to prevent direct recyclerView access to the data
            If there is any logical modification to the data it should
            be done here */

        rates.postValue(it)
    }

    val rates = MutableLiveData<Rates>()

    init {

        // Looking for updates from Repo
        repoRates.observeForever(repoRatesObserver)
    }

    fun getExchangeRates(baseCurrency: String = repo.baseCurrency) {

        repo.baseCurrency = baseCurrency
    }

    fun startUpdates() {

        repo.startUpdates()
    }

    fun stopUpdates() {

        repo.stopUpdates()
    }

    override fun onCleared() {
        stopUpdates()
        rates.removeObserver(repoRatesObserver)
        super.onCleared()
    }

    class Factory(private val repo: MainActivityRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(repo) as T
        }
    }
}