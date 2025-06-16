package com.example.dots

import android.graphics.Color
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class DotAdapter(
    private val items: List<String>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<DotAdapter.DotViewHolder>() {

    class DotViewHolder(val button: Button) : RecyclerView.ViewHolder(button)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DotViewHolder {
        val button = Button(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(28, 28)
            textSize = 12f
            setPadding(0, 0, 0, 0)
        }
        return DotViewHolder(button)
    }

    override fun onBindViewHolder(holder: DotViewHolder, position: Int) {
        val player = items[position]
        holder.button.text = when (player) {
            "X" -> "•"
            "O" -> "•"
            else -> ""
        }
        holder.button.setTextColor(
            when (player) {
                "X" -> Color.RED
                "O" -> Color.GREEN
                else -> Color.BLACK
            }
        )
        holder.button.setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
