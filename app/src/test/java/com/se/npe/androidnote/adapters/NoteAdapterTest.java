package com.se.npe.androidnote.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.ListActivity;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TextData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NoteAdapterTest {
    @Mock
    NoteAdapter mockNoteAdapter;

    List<Note> noteList;
    final int NOTE_LIST_SIZE = 20;

    @Mock
    AppCompatActivity mockActivity;

    @Before
    public void setUp() {
        this.noteList = new ArrayList<>();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            noteList.add(getExampleNote(i));
        }
        mockActivity = new ListActivity();
        mockNoteAdapter = new NoteAdapter(noteList, mockActivity);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getAllNotes() {
        assertEquals(this.noteList, mockNoteAdapter.getAllNotes());
    }

    @Test
    public void getSearchResult() {
        // Depends on example note
        // Whole note list
        assertEquals(this.noteList, mockNoteAdapter.getSearchResult("title"));
        // Empty note list
        assertEquals(new ArrayList<Note>(), mockNoteAdapter.getSearchResult("wtf???"));

        List<Note> noteListSearched = new ArrayList<>();
        // Depends on setUp()
        // Search for 3 using old note
        noteListSearched.add(this.noteList.get(3));
        noteListSearched.add(this.noteList.get(13));
        assertEquals(noteListSearched, mockNoteAdapter.getSearchResult("3"));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for 3 using new note
        // Not equals because new note has a different data
        noteListSearched.add(getExampleNote(3));
        noteListSearched.add(getExampleNote(13));
        assertNotEquals(noteListSearched, mockNoteAdapter.getSearchResult("3"));
        noteListSearched.clear();
    }

    @Test
    public void addNote() {
        // Add 5 notes
        final int NOTE_LIST_SIZE_ADD = 5;
        for (int i = 0; i < NOTE_LIST_SIZE_ADD; ++i) {
            Note note = getExampleNote(NOTE_LIST_SIZE + i);
            mockNoteAdapter.addNote(note);
            noteList.add(note);
        }
        assertEquals(noteList, mockNoteAdapter.getAllNotes());
        // Add a different note
        // Not equals
        mockNoteAdapter.addNote(getExampleNote(NOTE_LIST_SIZE + NOTE_LIST_SIZE_ADD));
        noteList.add(getExampleNote(NOTE_LIST_SIZE + NOTE_LIST_SIZE_ADD + 1));
        assertNotEquals(noteList, mockNoteAdapter.getAllNotes());
    }

    @Test
    public void getNoteAt() {
        // All corresponding notes are equal
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            assertEquals(noteList.get(i), mockNoteAdapter.getNoteAt(i));
        }
        // Not corresponding notes are not equal
        assertNotEquals(noteList.get(0), mockNoteAdapter.getNoteAt(1));
        assertNotEquals(noteList.get(10), mockNoteAdapter.getNoteAt(3));
        // New note is not equal
        Note note = getExampleNote(NOTE_LIST_SIZE);
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            assertNotEquals(note, mockNoteAdapter.getNoteAt(i));
        }
    }

    @Test
    public void setNoteAt() {
    }

    @Test
    public void removeNoteAt() {
    }

    @Test
    public void loadFromFile() {
    }

    @Test
    public void saveToFile() {
    }

    @Test
    public void updateList() {
    }

    @Test
    public void setOnDragStartListener() {
    }

    @Test
    public void onCreateViewHolder() {
    }

    @Test
    public void onBindViewHolder() {
    }

    @Test
    public void getAdapterItemCount() {
    }

    @Test
    public void generateHeaderId() {
    }

    @Test
    public void newHeaderHolder() {
    }

    @Test
    public void onCreateHeaderViewHolder() {
    }

    @Test
    public void onBindHeaderViewHolder() {
    }

    @Test
    public void newFooterHolder() {
    }

    @Test
    public void getItem() {
    }

    @Test
    public void insert() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void swapPositions() {
    }

    @Test
    public void clear() {
    }

    @Test
    public void onItemMove() {
    }

    @Test
    public void onItemDismiss() {
    }

    @NonNull
    private Note getExampleNote(int data) {
        // Create a new example note
        String title = "This is the title for " + String.valueOf(data);
        List<IData> content = new ArrayList<>();
        content.add(new TextData("This is the content for " + String.valueOf(data)));
        return new Note(title, content);
    }
}