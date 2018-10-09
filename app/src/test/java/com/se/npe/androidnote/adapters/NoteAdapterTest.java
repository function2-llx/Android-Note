package com.se.npe.androidnote.adapters;

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
            // Depends on the correctness of Note
            String title = "This is the title for " + String.valueOf(i);
            List<IData> content = new ArrayList<>();
            content.add(new TextData("This is the content for " + String.valueOf(i)));
            noteList.add(new Note(title, content));
        }
        mockActivity = new ListActivity();
        mockNoteAdapter = new NoteAdapter(noteList, mockActivity);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getAllNotes() {
        assertEquals(mockNoteAdapter.getAllNotes(), this.noteList);
    }

    @Test
    public void getSearchResult() {
    }

    @Test
    public void addNote() {
    }

    @Test
    public void getNoteAt() {
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
}