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


class ExchangeAdapter(
    private val onClick: (rateEntry: RateEntry) -> Unit
) : RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    /* This state is used for controlling the status of
        the loading */
    private var state = State.LOADING

    private var baseCurrency: String = ""
    private var ratesList: MutableList<RateEntry> = mutableListOf()

    private var shouldUpdate = true

    fun updateDataSet(rates: Rates) {

        baseCurrency = rates.baseCurrency

        if (!shouldUpdate) {
            shouldUpdate = true
            return
        }

        state = State.SHOW

        ratesList.clear()
        ratesList.addAll(rates.rates)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {

            0, 2 ->

                DataViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.row_rate,
                        parent,
                        false
                    )
                )
            else ->

                LoadingViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.row_loading,
                        parent,
                        false
                    )
                )
        }
    }

    override fun getItemCount(): Int {

        return when (state) {

            State.LOADING -> 10
            State.SHOW -> ratesList.size
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == 0) {
            2 // Header
        } else if (state == State.LOADING) {
            1 // Loading
        } else {
            2 // Data Holder
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (getItemViewType(position)) {

            0, 2 ->
                if (holder is DataViewHolder) {
                    setData(holder, position)
                }

            else ->
                if (holder is LoadingViewHolder) {
                    holder.startLoadingAnimation()
                }
        }
    }

    private fun setData(holder: DataViewHolder, position: Int) {

        if (ratesList.isEmpty())
            return

        val currentRateEntry = ratesList[position]

        holder.setData(currentRateEntry)

        holder.itemView.setOnClickListener {

            /* Change the state to loading so the adapter
                will load out loading views */


            /* Tell the adapter to ignore the incoming updates,
                This will make the adapter to skip one update. The
                 next update if it's successful will be with the new
                  baseCurrency */
            shouldUpdate = false

            /* Greet our new base currency to top,
                This is just a UI action, the actual data is on database
                 and will be untouched. */
            moveItem(position)

            notifyItemRangeRemoved(1, ratesList.lastIndex)

            ratesList.removeAll {
                it != currentRateEntry
            }

            state = State.LOADING

            /* Let the ViewModel know that we are requesting a
                new currency */
            onClick(currentRateEntry)
        }
    }

    private fun moveItem(fromPosition: Int, toPosition: Int = 0) {
        if (fromPosition == toPosition) return

        val movingItem = ratesList.removeAt(fromPosition)

        if (fromPosition < toPosition) {
            ratesList.add(toPosition - 1, movingItem)
        } else {
            ratesList.add(toPosition, movingItem)
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    class LoadingViewHolder(itemView: View) : ViewHolder(itemView) {

        fun startLoadingAnimation() {

        }
    }

    class DataViewHolder(itemView: View) : ViewHolder(itemView) {

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

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    enum class State {
        LOADING, SHOW
    }
}