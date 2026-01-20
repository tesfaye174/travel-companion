package com.travelcompanion.ui.tripdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travelcompanion.databinding.ItemPhotoBinding
import java.io.File

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private var photos = listOf<PhotoItem>()

    fun submitList(photos: List<PhotoItem>) {
        this.photos = photos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount() = photos.size

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

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

    data class PhotoItem(
        val imageUrl: String,
        val caption: String
    )
}

