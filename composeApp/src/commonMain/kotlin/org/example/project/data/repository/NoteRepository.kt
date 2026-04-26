package org.example.project.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.example.project.db.AppDatabase
import org.example.project.model.Note
import org.example.project.model.NoteCategory
import org.example.project.model.NoteColor

class NoteRepository(database: AppDatabase) {
    private val queries = database.noteEntityQueries

    fun getAllNotes(): Flow<List<Note>> =
        queries.selectAll(::mapToNote).asFlow().mapToList(Dispatchers.Default)

    fun getFavoriteNotes(): Flow<List<Note>> =
        queries.selectFavorites(::mapToNote).asFlow().mapToList(Dispatchers.Default)

    fun searchNotes(query: String): Flow<List<Note>> {
        val searchParam = "%$query%"
        return queries.search(searchParam, searchParam, ::mapToNote)
            .asFlow().mapToList(Dispatchers.Default)
    }

    suspend fun getNoteById(id: Int): Note? = withContext(Dispatchers.Default) {
        queries.selectById(id.toLong(), ::mapToNote).executeAsOneOrNull()
    }

    suspend fun insertNote(title: String, content: String, category: NoteCategory, color: NoteColor) {
        withContext(Dispatchers.Default) {
            queries.insert(
                title = title, content = content, category = category.name,
                isFavorite = 0,
                createdAt = org.example.project.di.getCurrentTime(), // <--- UBAH MENJADI INI
                color = color.name
            )
        }
    }

    suspend fun updateNote(id: Int, title: String, content: String, category: NoteCategory, isFavorite: Boolean, color: NoteColor) {
        withContext(Dispatchers.Default) {
            queries.update(
                title = title, content = content, category = category.name,
                isFavorite = if (isFavorite) 1L else 0L, color = color.name, id = id.toLong()
            )
        }
    }

    suspend fun toggleFavorite(id: Int, isCurrentlyFavorite: Boolean) {
        withContext(Dispatchers.Default) {
            val newStatus = if (isCurrentlyFavorite) 0L else 1L
            // Update khusus favorite tanpa merubah field lain (bisa dibuat query khusus di .sq)
            val note = queries.selectById(id.toLong()).executeAsOne()
            queries.update(note.title, note.content, note.category, newStatus, note.color, id.toLong())
        }
    }

    suspend fun deleteNote(id: Int) {
        withContext(Dispatchers.Default) { queries.delete(id.toLong()) }
    }

    private fun mapToNote(id: Long, title: String, content: String, category: String, isFavorite: Long, createdAt: Long, color: String): Note {
        return Note(
            id = id.toInt(), title = title, content = content,
            category = NoteCategory.valueOf(category), isFavorite = isFavorite == 1L,
            createdAt = createdAt, color = NoteColor.valueOf(color)
        )
    }
}