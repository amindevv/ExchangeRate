package dev.amin.echangecenter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.amin.echangecenter.R
import dev.amin.echangecenter.data.repositories.MainActivityRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repo = MainActivityRepository(this)

        val viewModel =
            ViewModelProvider(
                this,
                MainActivityViewModel.Factory(repo)
            ).get(MainActivityViewModel::class.java)

        viewModel.getExchangeRates()

        viewModel.rates.observe(this, Observer {rates ->

            rates
        })

        btn.setOnClickListener {
            viewModel.stopUpdates()
        }
    }
}
