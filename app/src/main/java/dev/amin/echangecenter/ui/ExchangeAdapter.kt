package dev.amin.echangecenter.ui

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.amin.echangecenter.R
import dev.amin.echangecenter.core.utils.CurrencyHelper
import dev.amin.echangecenter.data.models.RateEntry
import dev.amin.echangecenter.data.models.Rates
import kotlinx.android.synthetic.main.row_rate.view.*
import java.text.NumberFormat


class ExchangeAdapter(
    private val onClick: (rateEntry: RateEntry) -> Unit
) : RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    companion object {
        // Header is where I keep the BaseCurrency
        const val TYPE_HEADER = 0
        // When we are loading new rates based on a new currency
        const val TYPE_LOADING = 1
        // Rates
        const val TYPE_DATA = 2
    }

    var baseColor = Color.BLACK
    var validColor = Color.BLACK
    var invalidColor = Color.BLUE

    // The amount which has to be converted
    private var amount: Int = 100

    // Callback from user
    private val onAmountChanged: (amount: Int) -> Unit = { amount ->
        this.amount = amount
    }

    /* This state is used for controlling the status of
        the loading */
    private var state = State.LOADING

    // Dear data set
    private var ratesList: MutableList<RateEntry> = mutableListOf()

    private var shouldUpdate = true

    fun updateDataSet(rates: Rates) {

        /* This will cause the adapter to skip one update,
            so the next update will be with the new base. */
        if (!shouldUpdate) {
            shouldUpdate = true
            return
        }

        // Stop the loading
        state = State.SHOW

        ratesList.clear()
        ratesList.addAll(rates.rates)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {

            TYPE_HEADER, TYPE_DATA ->

                DataViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.row_rate,
                        parent,
                        false
                    ),
                    baseColor,
                    validColor,
                    invalidColor
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

        /* If the item is a header but the data is still downloading,
         I don't want to update the dataSet to have the view for input.
         therefor I keep the viewType as Data and not Header. To achieve
         the view I just call the animate function! */
        return if (position == 0 && state != State.LOADING) {

            // Header
            TYPE_HEADER
        } else if (position == 0) {

            // This is the case in which we are still loading
            TYPE_DATA
        } else if (state == State.LOADING) {

            // Loading
            TYPE_LOADING
        } else {

            // Data Holder
            TYPE_DATA
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (getItemViewType(position)) {

            TYPE_HEADER ->
                if (holder is DataViewHolder && ratesList.isNotEmpty())
                    holder.setInput(ratesList[0], onAmountChanged)

            TYPE_DATA ->
                if (holder is DataViewHolder)
                    setData(holder, position)

            else ->
                if (holder is LoadingViewHolder)
                    holder.startLoadingAnimation()

        }
    }

    /***
     * Sets the Currency Info on the DataViewHolder
     */
    private fun setData(holder: DataViewHolder, position: Int) {

        if (ratesList.isEmpty())
            return

        val currentRateEntry = ratesList[position]

        holder.setData(currentRateEntry, amount)

        holder.itemView.setOnClickListener {

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

            /* Change the state to loading so the adapter
                will load out loading views */
            state = State.LOADING

            holder.animateToInput(amount)

            /* Let the ViewModel know that we are requesting a
                new currency */
            onClick(currentRateEntry)
        }
    }

    /***
     * Responsible for animating the selected item to the top of the list
     * @param fromPosition It's current position
     * @param toPosition Target position, default is 0 because we always
     *  move to top
     */
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

            // TODO : load some animation here
        }
    }

    class DataViewHolder(
        itemView: View,
        private val baseColor: Int,
        private val validColor: Int,
        private val invalidColor: Int
    ) : ViewHolder(itemView) {

        fun setData(rateEntry: RateEntry, amount: Int) {

            itemView.apply {

                // Every android dev knows this pain
                etAmount.clearFocus()
                etAmount.isEnabled = false
                dataContainer.visibility = View.VISIBLE
                inputContainer.visibility = View.GONE
                dataContainer.alpha = 1f
                inputContainer.alpha = 0f

                // Normal data assignment
                val currency = rateEntry.currency
                val exchangeRate = rateEntry.exchangeRate

                tvCurrency.text = currency
                tvCurrencyName.text = CurrencyHelper.getCurrencyInfo(rateEntry.currency).first
                tvRate.text = exchangeRate.toString()
                tvExchangedRate.text = getExchangedRate(exchangeRate, amount)
                Glide.with(this)
                    .load(CurrencyHelper.getIcon(currency))
                    .centerCrop()
                    .into(ivFlag)

                container.setCardBackgroundColor(baseColor)
            }
        }

        /***
         * Init the holder as Input, This case is used when the ViewModel is
         * informed about the new Base Currency and has sent us the new data
         */
        fun setInput(rateEntry: RateEntry, amount: (amount: Int) -> Unit) {

            itemView.apply {

                inputContainer.visibility = View.VISIBLE
                dataContainer.visibility = View.GONE

                if (validateInput(etAmount.text.toString()))
                    container.setCardBackgroundColor(validColor)
                else
                    container.setCardBackgroundColor(invalidColor)

                // Ime options is for the return key.
                etAmount.isEnabled = true
                etAmount.imeOptions = EditorInfo.IME_ACTION_DONE
                etAmount.setOnEditorActionListener { textView, i, keyEvent ->

                    if (i == EditorInfo.IME_ACTION_DONE) {

                        val text = etAmount.text.toString()

                        // Send the value to the upper hands after validation

                        if (validateInput(text)) {
                            animateColorToValid()
                            amount(Integer.valueOf(text))
                        } else
                            animateColorToInvalid()

                        true
                    }

                    false
                }

                val currency = rateEntry.currency

                tvCurrency.text = currency
                tvCurrencyName.text = CurrencyHelper.getCurrencyInfo(rateEntry.currency).first
                Glide.with(this)
                    .load(CurrencyHelper.getIcon(currency))
                    .centerCrop()
                    .into(ivFlag)
            }
        }

        /***
         * This function is called when the new base is selected by the user, in order to
         * keep the UI business clean, I didn't want to change the viewType so I just animated
         * the views to get the same result!
         */
        fun animateToInput(amount: Int) {

            itemView.apply {

                setOnClickListener(null)

                inputContainer.visibility = View.VISIBLE
                inputContainer.animate().alpha(1f).setStartDelay(150).setDuration(250).start()

                etAmount.setText(amount.toString())

                dataContainer.animate().alpha(0f).setStartDelay(150).setDuration(250)
                    .withEndAction {

                        etAmount.isEnabled = true

                        dataContainer.visibility = View.GONE
                    }

                animateColorToValid()
            }
        }

        private fun validateInput(text: String): Boolean {

            return (text.isDigitsOnly() && text.length < 8)|| text.isEmpty()
        }

        private fun animateColorToValid() {

            itemView.container.apply {

                val cardColor = cardBackgroundColor.defaultColor

                if (cardColor == validColor)
                    return

                ValueAnimator.ofArgb(cardColor, validColor).apply {

                    duration = 900
                    start()

                    addUpdateListener {

                        setCardBackgroundColor(it.animatedValue as Int)
                    }
                }
            }
        }

        private fun animateColorToInvalid() {

            itemView.container.apply {

                val cardColor = cardBackgroundColor.defaultColor

                if (cardColor == invalidColor)
                    return

                ValueAnimator.ofArgb(cardBackgroundColor.defaultColor, invalidColor).apply {

                    duration = 500
                    start()

                    addUpdateListener {

                        setCardBackgroundColor(it.animatedValue as Int)
                    }
                }
            }
        }

        private fun getExchangedRate(exchangeRate: Double, amount: Int = 100): String {

            val formatter = NumberFormat.getNumberInstance().apply {
                maximumFractionDigits = 2
            }

            return formatter.format(exchangeRate * amount)
        }
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    enum class State {
        LOADING, SHOW
    }
}