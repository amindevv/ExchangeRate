package dev.amin.echangecenter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel =
            ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.getExchangeRates()

        btn.setOnClickListener {
            viewModel.stopUpdates()
        }
    }
}
