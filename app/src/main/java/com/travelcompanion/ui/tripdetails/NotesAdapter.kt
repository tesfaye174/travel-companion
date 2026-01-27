package com.travelcompanion.ui.tripdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelcompanion.databinding.ItemNoteBinding
import com.travelcompanion.utils.GenericDiffCallback

data class NoteItem(
    val content: String,
    val date: String
)

class NoteAdapter : ListAdapter<NoteItem, NoteAdapter.NoteViewHolder>(
    GenericDiffCallback<NoteItem>(
        areItemsTheSame = { old, new -> old.content == new.content && old.date == new.date },
        areContentsTheSame = { old, new -> old == new }
    )
) {

    // Use inherited submitList from ListAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount() = currentList.size

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteItem) {
            binding.tvNoteContent.text = note.content
            binding.tvNoteDate.text = note.date
        }
    }
}
