package dev.amin.echangecenter.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.amin.echangecenter.data.models.Rates
import dev.amin.echangecenter.data.repositories.MainActivityRepository


class MainActivityViewModel(
    private val repo: MainActivityRepository
) : ViewModel() {

    private val repoRates = repo.rates

    private val repoRatesObserver = Observer<Rates> {


        // I filter the data here then post it for the view

        /*

            val currentBaseOldPos = 1
            val

         */

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

    fun stopUpdates() {

        repo.stopUpdates()
    }

    override fun onCleared() {
        rates.removeObserver(repoRatesObserver)
        super.onCleared()
    }

    class Factory(private val repo: MainActivityRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(repo) as T
        }
    }
}