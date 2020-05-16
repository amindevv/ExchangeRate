package dev.amin.echangecenter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.amin.echangecenter.R
import dev.amin.echangecenter.data.models.RateEntry
import dev.amin.echangecenter.data.models.Rates
import dev.amin.echangecenter.data.repositories.MainActivityRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val exchangeAdapter = ExchangeAdapter { rateEntry ->
        onRateEntryClick(rateEntry)
    }

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        val repo = MainActivityRepository(this)

        viewModel =
            ViewModelProvider(
                this,
                MainActivityViewModel.Factory(repo)
            ).get(MainActivityViewModel::class.java)

        viewModel.rates.observe(this, Observer { rates ->

            updateAdapter(rates)
        })
    }

    override fun onPause() {
        super.onPause()

        viewModel.stopUpdates()
    }

    override fun onResume() {
        super.onResume()

        viewModel.startUpdates()
    }

    private fun onRateEntryClick(rateEntry: RateEntry) {

        if (::viewModel.isInitialized) {

            rvExchange.scrollToPosition(0)

            viewModel.getExchangeRates(rateEntry.currency)
        }
    }

    private fun updateAdapter(rates: Rates?) {
        exchangeAdapter.updateDataSet(rates ?: return)
    }

    private fun init() {

        rvExchange.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = exchangeAdapter
        }
    }
}
