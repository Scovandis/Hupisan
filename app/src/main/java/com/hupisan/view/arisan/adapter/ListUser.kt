package com.hupisan.view.arisan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hupisan.databinding.ItemUserBinding
import com.hupisan.models.ListUserModels

class ListUser (): RecyclerView.Adapter<ListUser.ViewHolder>(){
    var lists= ArrayList<ListUserModels>()
    fun getData(list: ArrayList<ListUserModels>){
        this.lists = list
    }
    fun addUser(data: ListUserModels){
        lists.add(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ListUserModels){
            with(binding){
                tvUsername.text = item.username
                tvNumberOfAdd.text = item.number.toString()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position])
    }

    override fun getItemCount()= lists.size

}