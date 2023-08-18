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
import com.example.resellapp.databinding.ListItemHomeCategoryBinding
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ItemsHomeAdapter(
    val clickListener: ItemHomeListener,
    val type:Int
    ):ListAdapter<Item,RecyclerView.ViewHolder>(ItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (type == 1) {
            ViewHolderType1.from(parent)
        } else {
            ViewHolderType2.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)!!
        when (holder) {
            is ViewHolderType1 -> holder.bind(item, clickListener)
            is ViewHolderType2 -> holder.bind(item, clickListener)
        }
    }

    fun setFilteredList(mList: List<Item>){
        submitList(mList)
//        notifyDataSetChanged()
    }


    class ViewHolderType1 private constructor(val binding: ListItemHomeBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Item, clickListener: ItemHomeListener ){
            binding.itemValue = item

            binding.clickListener  = clickListener
            val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter.applyPattern("#,##0.##")
            val formattedNumber = formatter.format(item.price)


            binding.priceText.text = formattedNumber.toString() + "$"
            binding.nameText.text = item.name.toString()

            Glide.with(binding.root.context).load(item.imageUrl).into(binding.imageView2)

            binding.executePendingBindings()

        }

        companion object{
            fun from(parent: ViewGroup):ViewHolderType1{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemHomeBinding.inflate(layoutInflater,parent,false)

                return ViewHolderType1(binding)
            }
        }

    }

    class ViewHolderType2 private constructor(val binding: ListItemHomeCategoryBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Item, clickListener: ItemHomeListener ){
            binding.itemValue = item

            binding.clickListener  = clickListener
            val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter.applyPattern("#,##0.##")
            val formattedNumber = formatter.format(item.price)


            binding.priceText.text = formattedNumber.toString() + "$"
            binding.nameText.text = item.name.toString()

            Glide.with(binding.root.context).load(item.imageUrl).into(binding.imageView2)

            binding.executePendingBindings()

        }

        companion object{
            fun from(parent: ViewGroup):ViewHolderType2{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemHomeCategoryBinding.inflate(layoutInflater,parent,false)

                return ViewHolderType2(binding)
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