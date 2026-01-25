package com.travelcompanion.ui.tripdetails

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.travelcompanion.R
import com.travelcompanion.databinding.DialogAddNoteBinding

class AddNoteDialogFragment : DialogFragment() {

    private var _binding: DialogAddNoteBinding? = null
    private val binding get() = _binding!!

    private var onNoteAdded: ((String) -> Unit)? = null

    companion object {
        fun newInstance(onAdd: (String) -> Unit): AddNoteDialogFragment {
            return AddNoteDialogFragment().apply {
                onNoteAdded = onAdd
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddNoteBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.new_note_title)
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                saveNote()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun saveNote() {
        val content = binding.etNoteContent.text?.toString()?.trim()

        if (content.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.error_saving_trip, Toast.LENGTH_SHORT).show()
            return
        }

        onNoteAdded?.invoke(content)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
