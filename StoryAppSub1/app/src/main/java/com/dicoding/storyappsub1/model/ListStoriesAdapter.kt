package com.dicoding.storyappsub1.model

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyappsub1.databinding.ItemRowStoriesBinding

class ListStoriesAdapter(private val list: ArrayList<ListStoryItem>, private val context: Context) :
    RecyclerView.Adapter<ListStoriesAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val adapterBinding =
            ItemRowStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListViewHolder(adapterBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val photo = list[position].photoUrl
        val name = list[position].name

        Glide.with(context).load(photo).into(holder.binding.ivPhoto)

        holder.binding.tvName.text = name

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(list[holder.adapterPosition])
        }
    }

    override fun getItemCount() = list.size

    class ListViewHolder(var binding: ItemRowStoriesBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickCallback {
        fun onItemClicked(user: ListStoryItem)
    }
}