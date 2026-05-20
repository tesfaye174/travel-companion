package com.travelcompanion.ui.tripdetails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.travelcompanion.R
import com.travelcompanion.databinding.BottomSheetAddNoteBinding

class AddNoteDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddNoteBinding? = null
    private val binding get() = _binding!!

    private var onNoteAdded: ((String) -> Unit)? = null

    companion object {
        fun newInstance(onAdd: (String) -> Unit): AddNoteDialogFragment {
            return AddNoteDialogFragment().apply {
                onNoteAdded = onAdd
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener { saveNote() }
    }

    private fun saveNote() {
        val content = binding.etNoteContent.text?.toString()?.trim()
        if (content.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.note_empty, Toast.LENGTH_SHORT).show()
            return
        }
        onNoteAdded?.invoke(content)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
