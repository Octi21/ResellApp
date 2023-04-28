package com.example.resellapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resellapp.Item
import com.example.resellapp.databinding.ListItemBinding
import com.example.resellapp.databinding.ListItemHomeBinding
import kotlinx.coroutines.withContext

class ItemsHomeAdapter(val clickListener: ItemHomeListener):ListAdapter<Item,ItemsHomeAdapter.ViewHolder>(ItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)!!
        holder.bind(item, clickListener)
    }

    fun setFilteredList(mList: List<Item>){
        submitList(mList)
//        notifyDataSetChanged()
    }



    class ViewHolder private constructor(val binding: ListItemHomeBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Item, clickListener: ItemHomeListener ){
            binding.itemValue = item

            binding.clickListener  = clickListener

            binding.priceText.text = item.price.toString()
            binding.nameText.text = item.name.toString()

            Glide.with(binding.root.context).load(item.imageUrl).into(binding.imageView2)

            binding.executePendingBindings()

        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemHomeBinding.inflate(layoutInflater,parent,false)

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

class ItemHomeListener(val clickListener: (itemId: String?) -> Unit){
    fun onClick(item: Item) = clickListener(item.id)
}