package com.example.travelcompanion.ui.tripdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelcompanion.R
import com.example.travelcompanion.databinding.ItemNoteBinding
import com.example.travelcompanion.domain.model.Note
import com.example.travelcompanion.utils.DateUtils

class NotesAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(NoteDiffCallback()) {

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

    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            // Titolo e contenuto
            binding.textNoteTitle.text = note.title
            binding.textNoteContent.text = note.content

            // Timestamp
            binding.textNoteTimestamp.text = DateUtils.formatDateTime(note.timestamp)

            // Posizione (se disponibile)
            if (note.latitude != null && note.longitude != null) {
                binding.textNoteLocation.visibility = View.VISIBLE
                binding.textNoteLocation.text = String.format(
                    "📍 %.6f°N, %.6f°E",
                    note.latitude,
                    note.longitude
                )
            } else {
                binding.textNoteLocation.visibility = View.GONE
            }

            // Foto allegata (se disponibile)
            if (note.photoPath != null) {
                binding.imageNotePhoto.visibility = View.VISIBLE
                Glide.with(binding.root.context)
                    .load(note.photoPath)
                    .centerCrop()
                    .into(binding.imageNotePhoto)
            } else {
                binding.imageNotePhoto.visibility = View.GONE
            }

            // Click listeners
            binding.root.setOnClickListener {
                onNoteClick(note)
            }

            binding.buttonDeleteNote.setOnClickListener {
                onDeleteClick(note)
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}