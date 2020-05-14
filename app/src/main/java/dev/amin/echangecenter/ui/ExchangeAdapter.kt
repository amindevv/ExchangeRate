package dev.amin.echangecenter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.amin.echangecenter.R

class ExchangeAdapter() : RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {


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

    override fun getItemCount() = 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}