package com.se.npe.androidnote.models;

import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.interfaces.IData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TableOperateTest {

    private TableOperate tableOperate;
    private List<Note> noteList;
    private static final int NOTE_LIST_SIZE = 20;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        init();
        addNote();
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void init() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        TableOperate.init(activity.getApplicationContext());
        tableOperate = TableOperate.getInstance();
        assertNotNull(tableOperate);
    }

    @Test
    public void getInstance() {
        // Check TableOperate is properly set up
        TableOperate tableOperate2 = TableOperate.getInstance();
        assertNotNull(tableOperate2);
        // Check singleton pattern is used
        assertSame(tableOperate, tableOperate2);
    }

    @Test
    public void encodeNoteAndDecodeNote() {
        // Encode & Decode
        List<IData> dataList = DataExample.getExampleDataList("encode_decode");
        assertEquals(dataList, tableOperate.decodeNote(tableOperate.encodeNote(dataList)));
    }

    @Test
    public void getAllNotes() {
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    @Test
    public void getNoteAt() {
        // Get note at each index
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            assertEquals(noteList.get(i), tableOperate.getNoteAt(noteList.get(i).getIndex())); // noteList.get(i).getIndex() == i + 1
        }
    }

    @Test
    public void getNoteAtBeforeStart() {
        expectedException.expect(IndexOutOfBoundsException.class);
        tableOperate.getNoteAt(noteList.get(0).getIndex() - 1); // noteList.get(0).getIndex() == 1
    }

    @Test
    public void getNoteAtAfterEnd() {
        expectedException.expect(IndexOutOfBoundsException.class);
        tableOperate.getNoteAt(noteList.get(NOTE_LIST_SIZE - 1).getIndex() + 1); // noteList.get(NOTE_LIST_SIZE - 1).getIndex() == NOTE_LIST_SIZE
    }

    @Test
    public void addNote() {
        noteList = new ArrayList<>();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i));
            noteList.add(note);
            tableOperate.addNote(note);
            // SQL index starts at 1
            // noteList.get(i).getIndex() == i + 1
            // if addNote() is invoked the first time
        }
    }

    @Test
    public void setNoteAt() {
        // Set note at some index & Get notes
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i + NOTE_LIST_SIZE));
            // Old note get index -> New note set
            tableOperate.setNoteAt(noteList.get(i).getIndex(), note);
            noteList.set(i, note);
            assertEquals(note, tableOperate.getNoteAt(note.getIndex()));
        }
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    @Test
    public void removeNoteAt() {
        // Remove note at some index & Get notes
        final int NOTE_LIST_SIZE_REMOVE = 4;
        // Remove note at start, middle
        for (int i = 0; i < NOTE_LIST_SIZE_REMOVE; ++i) {
            // Old note get index -> New note remove
            tableOperate.removeNoteAt(noteList.get(i).getIndex());
            noteList.remove(i);
            assertEquals(noteList, tableOperate.getAllNotes());
        }
        // Remove note at end
        tableOperate.removeNoteAt(noteList.get(noteList.size() - 1).getIndex());
        noteList.remove(noteList.size() - 1);
        assertEquals(noteList, tableOperate.getAllNotes());
        // Remove all notes one by one
        for (int i = 0; i < noteList.size(); ++i) {
            tableOperate.removeNoteAt(noteList.get(i).getIndex());
        }
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes());
        // Remove note before start/after end will not throw exception
    }

    @Test
    public void removeAllNotes() {
        tableOperate.removeAllNotes();
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    @Test
    public void removeAllNotesAndAddNote() {
        // Remove all notes & Add note
        removeAllNotes();
        addNote();
    }
}