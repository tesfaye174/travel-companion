package com.travelcompanion.utils

import androidx.recyclerview.widget.DiffUtil

class GenericDiffCallback<T : Any>(
    private val areItemsTheSame: (T, T) -> Boolean,
    private val areContentsTheSame: (T, T) -> Boolean
) : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = areItemsTheSame(oldItem, newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = areContentsTheSame(oldItem, newItem)
}
