package com.example.roomlibrary.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application) {
        NoteDatabase db = NoteDatabase.getDatabase(application);
        noteDao = db.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() { return allNotes; }

    public LiveData<List<Note>> searchNotes(String query) { return noteDao.searchNotes(query); }

    public void insert(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> noteDao.insert(note));
    }

    public void delete(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> noteDao.delete(note));
    }
}