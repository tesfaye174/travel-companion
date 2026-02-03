package com.travelcompanion.ui.tripdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
            val model: Any? = when {
                photo.imageUrl.startsWith("content://") -> photo.imageUrl
                photo.imageUrl.startsWith("file://") -> photo.imageUrl
                else -> {
                    val f = File(photo.imageUrl)
                    if (f.exists()) f else null
                }
            }

            if (model != null) {
                Glide.with(binding.ivPhoto)
                    .load(model)
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.ivPhoto)
            } else {
                // file non trovato o model null -> mostra placeholder per evitare crash
                Glide.with(binding.ivPhoto)
                    .load(android.R.drawable.ic_menu_report_image)
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.ivPhoto)
            }
        }
    }
}

data class PhotoItem(
    val imageUrl: String,
    val caption: String
)
