package com.example.resellapp.myItems

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resellapp.Item
import com.example.resellapp.databinding.ListItemBinding
import com.example.resellapp.databinding.ListItemBoughtBinding

class BoughtItemsAdapter(val clickListener: ItemListener2):ListAdapter<Item,BoughtItemsAdapter.ViewHolder>(ItemDiffCallback2()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)!!
        holder.bind(item, clickListener)
    }



    class ViewHolder private constructor(val binding: ListItemBoughtBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Item, clickListener: ItemListener2){
            binding.itemValue = item

            binding.clickListener  = clickListener

            binding.nameText.text = item.name.toString()

            Glide.with(binding.root.context).load(item.imageUrl).into(binding.imageView2)

            binding.executePendingBindings()

        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBoughtBinding.inflate(layoutInflater,parent,false)

                return ViewHolder(binding)
            }
        }

    }
}

class ItemDiffCallback2: DiffUtil.ItemCallback<Item>(){
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}

class ItemListener2(val clickListener: (itemId: String?) -> Unit){
    fun onClick(item: Item) = clickListener(item.id)
}