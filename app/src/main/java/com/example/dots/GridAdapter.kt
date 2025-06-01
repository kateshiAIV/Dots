package com.example.dots

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class GridAdapter(
    private val size: Int,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<GridAdapter.CellViewHolder>() {

    inner class CellViewHolder(val button: Button) : RecyclerView.ViewHolder(button)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cell, parent, false) as Button
        return CellViewHolder(button)
    }

    override fun getItemCount(): Int = size * size

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        holder.button.setOnClickListener {
            onClick(position)
        }
    }
}
