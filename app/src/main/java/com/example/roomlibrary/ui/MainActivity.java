package com.example.roomlibrary.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomlibrary.R;
import com.example.roomlibrary.data.Note;
import com.example.roomlibrary.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Налаштування Списку
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        // 2. Ініціалізація ViewModel
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, notes -> {
            adapter.setNotes(notes);
        });

        // 3. Обробка кнопки "Додати"
        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(v -> showAddNoteDialog());

        // 4. ПОШУК (Сценарій B)
        EditText editTextSearch = findViewById(R.id.edit_text_search);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString()); // Фільтруємо список під час вводу
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 5. СВАЙП ДЛЯ ВИДАЛЕННЯ (Сценарій A/B)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Ми не підтримуємо перетягування (Drag & Drop)
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note noteToDelete = adapter.getNoteAt(position);
                noteViewModel.delete(noteToDelete);
                Toast.makeText(MainActivity.this, "Замітку видалено", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Нова замітка");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        EditText editTitle = view.findViewById(R.id.edit_text_title);
        EditText editDescription = view.findViewById(R.id.edit_text_description);
        builder.setView(view);

        builder.setPositiveButton("Зберегти", (dialog, which) -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(MainActivity.this, "Заповніть всі поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            Note note = new Note(title, description, System.currentTimeMillis());
            noteViewModel.insert(note);
            Toast.makeText(MainActivity.this, "Збережено", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Скасувати", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}