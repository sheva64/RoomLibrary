package com.example.roomlibrary.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomlibrary.R;
import com.example.roomlibrary.data.Note;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private List<Note> notes = new ArrayList<>();
    private List<Note> notesFull = new ArrayList<>(); // Копія для пошуку

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        this.notesFull = new ArrayList<>(notes); // Зберігаємо повний список
        notifyDataSetChanged();
    }

    // Метод для отримання замітки (потрібен для свайпу)
    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    // Метод для локального пошуку
    public void filter(String text) {
        notes.clear();
        if (text.isEmpty()) {
            notes.addAll(notesFull);
        } else {
            text = text.toLowerCase();
            for (Note item : notesFull) {
                if (item.getTitle().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text)) {
                    notes.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;

        public NoteHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
        }
    }
}