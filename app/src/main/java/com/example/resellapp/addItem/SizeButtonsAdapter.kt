package com.example.resellapp.addItem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.resellapp.ButtonItem
import com.example.resellapp.R

class SizeButtonsAdapter(private val buttonItems: List<ButtonItem>) :
    RecyclerView.Adapter<SizeButtonsAdapter.ButtonViewHolder>() {

    override fun getItemCount() = buttonItems.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_button, parent, false)
        return ButtonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val currentItem = buttonItems[position]
        holder.button.text = currentItem.name
//        holder.button.setBackgroundResource(R.drawable.button_subcategory)
    }


    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.buttonItem)
    }
}