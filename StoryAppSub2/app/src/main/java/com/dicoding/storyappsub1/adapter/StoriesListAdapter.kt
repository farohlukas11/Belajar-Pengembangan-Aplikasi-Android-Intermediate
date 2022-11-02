package com.dicoding.storyappsub1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyappsub1.databinding.ItemRowStoriesBinding
import com.dicoding.storyappsub1.model.ListStoryItem

class StoriesListAdapter :
    PagingDataAdapter<ListStoryItem, StoriesListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding =
            ItemRowStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)

        if (data != null) {
            holder.bind(data)
        }
    }

    inner class MyViewHolder(private val binding: ItemRowStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {

            Glide.with(itemView.context).load(data.photoUrl).into(binding.ivPhoto)
            binding.tvName.text = data.name

            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: ListStoryItem)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}


