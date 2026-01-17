package com.example.travelcompanion.ui.tripdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.travelcompanion.R
import com.example.travelcompanion.databinding.ItemPhotoBinding
import com.example.travelcompanion.domain.model.Photo

class PhotosAdapter(
    private val onPhotoClick: (Photo) -> Unit
) : ListAdapter<Photo, PhotosAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

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

    inner class PhotoViewHolder(
        private val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            // Carica immagine con Glide
            Glide.with(binding.root.context)
                .load(photo.filePath)
                .centerCrop()
                .placeholder(R.drawable.placeholder_photo)
                .error(R.drawable.ic_broken_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imagePhoto)

            // Click listener
            binding.root.setOnClickListener {
                onPhotoClick(photo)
            }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}