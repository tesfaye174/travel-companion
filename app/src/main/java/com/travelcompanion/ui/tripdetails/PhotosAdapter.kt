package com.travelcompanion.ui.tripdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travelcompanion.databinding.ItemPhotoBinding
import com.travelcompanion.utils.GenericDiffCallback
import java.io.File

class PhotoAdapter : ListAdapter<PhotoItem, PhotoAdapter.PhotoViewHolder>(
    GenericDiffCallback<PhotoItem>(
        areItemsTheSame = { old, new -> old.imageUrl == new.imageUrl && old.caption == new.caption },
        areContentsTheSame = { old, new -> old == new }
    )
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: PhotoItem) {
            binding.tvPhotoCaption.text = photo.caption
            val model: Any = if (photo.imageUrl.startsWith("content://") || photo.imageUrl.startsWith("file://")) {
                photo.imageUrl
            } else {
                File(photo.imageUrl)
            }
            Glide.with(binding.ivPhoto)
                .load(model)
                .centerCrop()
                .into(binding.ivPhoto)
        }
    }
}

data class PhotoItem(
    val imageUrl: String,
    val caption: String
)
