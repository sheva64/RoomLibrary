package com.example.roomlibrary.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface NoteDao {
    // Сценарій A: CRUD
    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    // Сценарій B: Просунуті функції (Сортування)
    @Query("SELECT * FROM notes_table ORDER BY timestamp DESC")
    LiveData<List<Note>> getAllNotes();

    // Сценарій B: Просунуті функції (Пошук)
    @Query("SELECT * FROM notes_table WHERE title LIKE '%' || :searchQuery || '%'")
    LiveData<List<Note>> searchNotes(String searchQuery);
}