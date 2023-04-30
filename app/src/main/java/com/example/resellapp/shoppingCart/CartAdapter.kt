package com.example.resellapp.shoppingCart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resellapp.Item
import com.example.resellapp.databinding.ListCartBinding
import com.example.resellapp.databinding.ListItemBinding
import com.example.resellapp.myItems.ItemListener
import com.example.resellapp.myItems.ItemsAdapter

class CartAdapter(private val shoppingCartViewModel: ShoppingCartViewModel): ListAdapter<Item, CartAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)!!
        holder.bind(item,shoppingCartViewModel)
    }



    class ViewHolder private constructor(val binding: ListCartBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Item, shoppingCartViewModel:ShoppingCartViewModel){
            binding.itemValue = item

            binding.shoppingCartViewModel = shoppingCartViewModel

            binding.priceText.text = item.price.toString()
            binding.nameText.text = item.name.toString()

            Glide.with(binding.root.context).load(item.imageUrl).into(binding.imageView3)

            binding.executePendingBindings()

        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListCartBinding.inflate(layoutInflater,parent,false)

                return ViewHolder(binding)
            }
        }

    }
}

class ItemDiffCallback: DiffUtil.ItemCallback<Item>(){
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}
