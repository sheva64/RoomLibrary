package com.example.roomlibrary;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.roomlibrary.data.Note;
import com.example.roomlibrary.data.NoteDao;
import com.example.roomlibrary.data.NoteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NoteDaoTest {

    // Це правило дозволяє LiveData виконуватися синхронно у тестах
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private NoteDatabase database;
    private NoteDao noteDao;

    @Before
    public void setup() {
        // Ініціалізуємо БД у пам'яті. Вона повністю очищається після виконання тестів.
        database = Room.inMemoryDatabaseBuilder(
                        ApplicationProvider.getApplicationContext(),
                        NoteDatabase.class)
                .allowMainThreadQueries() // Дозволяємо запити в головному потоці тільки для тестів
                .build();
        noteDao = database.noteDao();
    }

    @After
    public void teardown() {
        database.close();
    }

    // ТЕСТ 1: Перевірка вставки та зчитування (CRUD)
    @Test
    public void test1_insertAndReadNote() throws InterruptedException {
        Note note = new Note("Купити молоко", "Сільпо, 2 пакети", 1000L);
        noteDao.insert(note);

        List<Note> allNotes = LiveDataTestUtil.getOrAwaitValue(noteDao.getAllNotes());

        assertEquals(1, allNotes.size());
        assertEquals("Купити молоко", allNotes.get(0).getTitle());
    }

    // ТЕСТ 2: Перевірка видалення (CRUD)
    @Test
    public void test2_deleteNote() throws InterruptedException {
        Note note = new Note("Тест", "Видалити мене", 1000L);
        noteDao.insert(note);

        // Отримуємо з БД, щоб мати об'єкт зі згенерованим ID
        Note insertedNote = LiveDataTestUtil.getOrAwaitValue(noteDao.getAllNotes()).get(0);
        noteDao.delete(insertedNote);

        List<Note> allNotes = LiveDataTestUtil.getOrAwaitValue(noteDao.getAllNotes());
        assertTrue(allNotes.isEmpty());
    }

    // ТЕСТ 3: Перевірка оновлення (CRUD)
    @Test
    public void test3_updateNote() throws InterruptedException {
        Note note = new Note("Стара назва", "Опис", 1000L);
        noteDao.insert(note);

        Note insertedNote = LiveDataTestUtil.getOrAwaitValue(noteDao.getAllNotes()).get(0);

        // Змінюємо дані через конструктор та встановлюємо старий ID
        Note updatedNote = new Note("Нова назва", "Опис", 1000L);
        updatedNote.setId(insertedNote.getId());

        noteDao.update(updatedNote);

        Note checkedNote = LiveDataTestUtil.getOrAwaitValue(noteDao.getAllNotes()).get(0);
        assertEquals("Нова назва", checkedNote.getTitle());
    }

    // ТЕСТ 4: Перевірка сортування за часом (Просунуті функції)
    @Test
    public void test4_getAllNotes_returnsOrderedByTimestamp() throws InterruptedException {
        Note noteOld = new Note("Стара", "1", 1000L);
        Note noteNew = new Note("Нова", "2", 5000L);

        noteDao.insert(noteOld);
        noteDao.insert(noteNew);

        List<Note> allNotes = LiveDataTestUtil.getOrAwaitValue(noteDao.getAllNotes());

        // Оскільки ми сортуємо DESC (за спаданням), "Нова" має бути першою (індекс 0)
        assertEquals("Нова", allNotes.get(0).getTitle());
        assertEquals("Стара", allNotes.get(1).getTitle());
    }

    // ТЕСТ 5: Перевірка пошуку (Просунуті функції)
    @Test
    public void test5_searchNotes_returnsMatching() throws InterruptedException {
        Note note1 = new Note("Квитки в кіно", "Ряд 5", 1000L);
        Note note2 = new Note("Рецепт пирога", "Яблука, тісто", 2000L);
        Note note3 = new Note("Завдання з кіно", "Подивитися фільм", 3000L);

        noteDao.insert(note1);
        noteDao.insert(note2);
        noteDao.insert(note3);

        // Шукаємо слово "кіно"
        List<Note> searchResults = LiveDataTestUtil.getOrAwaitValue(noteDao.searchNotes("кіно"));

        assertEquals(2, searchResults.size()); // Має знайти note1 та note3
        assertTrue(searchResults.get(0).getTitle().contains("кіно"));
    }
}