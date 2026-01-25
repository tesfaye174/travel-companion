package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Note
import com.travelcompanion.domain.model.PhotoNote
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

// saves notes and photos to a trip
class SaveTripDocsUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    suspend fun addNote(note: Note): Long {
        require(note.tripId > 0) { "Note must be linked to a valid trip" }
        require(note.content.isNotBlank()) { "Note content cannot be empty" }
        return repository.insertNote(note)
    }

    suspend fun addPhotoNote(photoNote: PhotoNote): Long {
        require(photoNote.tripId > 0) { "Photo must be linked to a valid trip" }
        require(photoNote.imagePath.isNotBlank()) { "Photo must have valid image path" }
        return repository.insertPhotoNote(photoNote)
    }
}
