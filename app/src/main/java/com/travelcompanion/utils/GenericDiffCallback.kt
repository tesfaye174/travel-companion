package com.travelcompanion.utils

import androidx.recyclerview.widget.DiffUtil

class GenericDiffCallback<T : Any>(
    areItemsTheSame: (T, T) -> Boolean,
    areContentsTheSame: (T, T) -> Boolean
) : DiffUtil.ItemCallback<T>() {
    private val itemsTheSame = areItemsTheSame
    private val contentsTheSame = areContentsTheSame

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = itemsTheSame(oldItem, newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = contentsTheSame(oldItem, newItem)
}
