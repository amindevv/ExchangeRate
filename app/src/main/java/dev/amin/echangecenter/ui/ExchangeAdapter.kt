package dev.amin.echangecenter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.amin.echangecenter.R
import dev.amin.echangecenter.core.utils.IconSelector
import dev.amin.echangecenter.data.models.RateEntry
import dev.amin.echangecenter.data.models.Rates
import kotlinx.android.synthetic.main.row_rate.view.*

class ExchangeAdapter : RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    private var baseCurrency: String = ""
    private var ratesList: MutableList<RateEntry> = mutableListOf()

    fun updateDataSet(rates: Rates) {

        baseCurrency = rates.baseCurrency

        ratesList.clear()
        ratesList.addAll(rates.rates)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val holder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_rate,
                parent,
                false
            )
        )

        return holder
    }

    override fun getItemCount() = ratesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        populateHolder(holder, position)
    }

    private fun populateHolder(holder: ViewHolder, position: Int) {

        val rate = ratesList[position]

        holder.setData(rate)

        holder.itemView.setOnClickListener {

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(rate: RateEntry) {

            itemView.apply {

                val currency = rate.currency

                tvCurrency.text = currency

                etAmount.setText(rate.exchangeRate.toString())

                Glide.with(this)
                    .load(IconSelector.getIcon(currency))
                    .centerCrop()
                    .into(ivFlag)
            }
        }
    }
}