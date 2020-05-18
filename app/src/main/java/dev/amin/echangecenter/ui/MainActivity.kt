package dev.amin.echangecenter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.amin.echangecenter.R
import dev.amin.echangecenter.data.RequestStatus
import dev.amin.echangecenter.data.models.RateEntry
import dev.amin.echangecenter.data.models.Rates
import dev.amin.echangecenter.data.repositories.MainActivityRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_main.view.*

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

        viewModel.requestStatus.observe(this, Observer {

            when (it) {
                RequestStatus.SUCCESS -> playSuccessAnimation()
                RequestStatus.FAILURE -> playFailureAnimation()
                RequestStatus.NONE -> updatesStopped()
            }
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

        setSupportActionBar(toolbarContainer.toolbar)

        exchangeAdapter.apply {
            baseColor = ContextCompat.getColor(this@MainActivity, R.color.colorSurface)
            validColor = ContextCompat.getColor(this@MainActivity, R.color.colorGreen)
            invalidColor = ContextCompat.getColor(this@MainActivity, R.color.colorRed)
        }

        rvExchange.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = exchangeAdapter
        }
    }

    private fun playSuccessAnimation() {

        toolbarContainer.viewConnectionStatusFailure.apply {

            cancelAnimation()
            visibility = View.GONE
        }

        toolbarContainer.viewConnectionStatusSuccess.apply {

            if (this.isAnimating)
                return

            Toast.makeText(
                this@MainActivity,
                getString(R.string.message_status_success),
                Toast.LENGTH_LONG
            ).show()

            visibility = View.VISIBLE

            setAnimation(R.raw.animation_connection_status_ssuccess)
            playAnimation()
        }
    }

    private fun playFailureAnimation() {

        // Stop the animation on success view
        toolbarContainer.viewConnectionStatusSuccess.apply {

            cancelAnimation()
            visibility = View.GONE
        }

        toolbarContainer.viewConnectionStatusFailure.apply {

            if (this.isAnimating)
                return

            Toast.makeText(
                this@MainActivity,
                getString(R.string.message_staus_retrying),
                Toast.LENGTH_LONG
            ).show()

            visibility = View.VISIBLE

            setAnimation(R.raw.animation_connection_status_failure)
            playAnimation()
        }
    }

    private fun updatesStopped() {

        Toast.makeText(
            this@MainActivity,
            getString(R.string.message_status_disconnected),
            Toast.LENGTH_LONG
        ).show()
    }
}
