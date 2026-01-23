package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Note
import com.travelcompanion.domain.model.PhotoNote
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * Use case per aggiungere note e foto a un viaggio.
 * 
 * Gestisce la logica di business per allegare documentazione
 * (note testuali e foto) a un viaggio specifico.
 */
class AddTripDocumentationUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    /**
     * Aggiunge una nota testuale a un viaggio.
     * @param note La nota da aggiungere
     * @return L'ID della nota creata
     */
    suspend fun addNote(note: Note): Long {
        require(note.tripId > 0) { "La nota deve essere associata a un viaggio valido" }
        require(note.content.isNotBlank()) { "Il contenuto della nota non può essere vuoto" }
        return repository.insertNote(note)
    }

    /**
     * Aggiunge una foto con nota a un viaggio.
     * @param photoNote La foto con nota da aggiungere
     * @return L'ID della foto creata
     */
    suspend fun addPhotoNote(photoNote: PhotoNote): Long {
        require(photoNote.tripId > 0) { "La foto deve essere associata a un viaggio valido" }
        require(photoNote.imagePath.isNotBlank()) { "La foto deve avere un percorso immagine valido" }
        return repository.insertPhotoNote(photoNote)
    }
}
