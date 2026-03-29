package com.example.roomlibrary.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.roomlibrary.data.Note;
import com.example.roomlibrary.data.NoteRepository;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() { return allNotes; }
    public LiveData<List<Note>> searchNotes(String query) { return repository.searchNotes(query); }
    public void insert(Note note) { repository.insert(note); }
    public void delete(Note note) {
        repository.delete(note);
    }
}