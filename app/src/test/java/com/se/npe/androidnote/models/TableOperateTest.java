package com.se.npe.androidnote.models;

import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.events.NoteClearEvent;
import com.se.npe.androidnote.events.NoteDeleteEvent;
import com.se.npe.androidnote.events.NoteModifyEvent;
import com.se.npe.androidnote.interfaces.IData;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
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
    public void initConfigFile() {
        // successfully create dir
        File configDir = new File(TableConfig.SAVE_PATH + "/config");
        assertNotNull(configDir);
        // successfully create file
        File configFile = new File(TableConfig.SAVE_PATH + "/config/searchconfig.txt");
        assertNotNull(configFile);
        assertNotNull(configDir.listFiles());
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
    public void listStringToStringToListString() {
        // ListString -> String -> ListString
        Note note = DataExample.getExampleNote("listString_string");
        assertEquals(note.getTag(), tableOperate.stringToListString(tableOperate.listStringToString(note.getTag())));
    }

    @Test
    public void setSearchConfigAndGetSearchConfig() {
        final int SEARCH_CONFIG = 0;
        TableOperate.setSearchConfig(SEARCH_CONFIG);
        assertEquals(SEARCH_CONFIG, TableOperate.getSearchConfig());
    }

    @Test
    public void getAllNotes() {
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    @Test
    public void getSearchResult() {
        // Depends on example note
        // Whole note list cannot be test - precise search
        // Empty note list
        assertEquals(new ArrayList<Note>(), tableOperate.getSearchResult("wtf???"));

        List<Note> noteListSearched = new ArrayList<>();
        // Search for 3 using old note
        noteListSearched.add(this.noteList.get(3));
        assertEquals(noteListSearched, tableOperate.getSearchResult(this.noteList.get(3).getTitle()));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for 3 using new note
        // Not equals because new note has a different data
        noteListSearched.add(DataExample.getExampleNote(String.valueOf(3)));
        assertNotEquals(noteListSearched, tableOperate.getSearchResult(this.noteList.get(3).getTitle()));
        noteListSearched.clear();
    }

    @Test
    public void getSearchResultFuzzy() {
        // Depends on example note
        // Whole note list
        assertEquals(this.noteList, tableOperate.getSearchResultFuzzy("title"));
        assertNotSame(this.noteList, tableOperate.getSearchResultFuzzy("title"));
        // Empty note list
        assertEquals(new ArrayList<Note>(), tableOperate.getSearchResultFuzzy("wtf???"));

        List<Note> noteListSearched = new ArrayList<>();
        // Depends on addNote()
        // Search for 3 using old note
        noteListSearched.add(this.noteList.get(3));
        noteListSearched.add(this.noteList.get(13));
        assertEquals(noteListSearched, tableOperate.getSearchResultFuzzy("3"));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for 3 using new note
        // Not equals because new note has a different data
        noteListSearched.add(DataExample.getExampleNote(String.valueOf(3)));
        noteListSearched.add(DataExample.getExampleNote(String.valueOf(13)));
        assertNotEquals(noteListSearched, tableOperate.getSearchResultFuzzy("3"));
        noteListSearched.clear();
    }

    @Test
    public void addNote() {
        noteList = new ArrayList<>();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i));
            tableOperate.addNote(note);
            noteList.add(note);
            // SQL index starts at 1
            // noteList.get(i).getIndex() == i + 1
            // if addNote() is invoked the first time
        }
    }

    @Test
    public void setNote() {
        // Set some notes & Get notes
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            // index does not change
            int index = noteList.get(i).getIndex();
            Note note = DataExample.getExampleNote(String.valueOf(i + NOTE_LIST_SIZE));
            note.setIndex(index);
            // Old note get index -> New note set
            tableOperate.setNote(note);
            noteList.set(i, note);
            assertEquals(note, tableOperate.getNoteAt(note.getIndex()));
        }
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    @Test
    public void removeNote() {
        // Remove some notes & Get notes
        final int NOTE_LIST_SIZE_REMOVE = 4;
        // Remove note at start, middle
        for (int i = 0; i < NOTE_LIST_SIZE_REMOVE; ++i) {
            // Old note get index -> New note remove
            tableOperate.removeNote(noteList.get(i));
            noteList.remove(i);
            assertEquals(noteList, tableOperate.getAllNotes());
        }
        // Remove note at end
        tableOperate.removeNote(noteList.get(noteList.size() - 1));
        noteList.remove(noteList.size() - 1);
        assertEquals(noteList, tableOperate.getAllNotes());
        // Remove all notes one by one
        for (int i = 0; i < noteList.size(); ++i) {
            tableOperate.removeNote(noteList.get(i));
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
    public void onReceiveNote() {
        // add note
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i + NOTE_LIST_SIZE));
            EventBus.getDefault().post(new NoteModifyEvent(note));
            noteList.add(note);
            assertEquals(noteList, tableOperate.getAllNotes());
        }
        // set note
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            // index does not change
            int index = noteList.get(i).getIndex();
            Note note = DataExample.getExampleNote(String.valueOf(i + NOTE_LIST_SIZE + NOTE_LIST_SIZE));
            note.setIndex(index);
            // Old note get index -> New note set
            EventBus.getDefault().post(new NoteModifyEvent(note));
            noteList.set(i, note);
            assertEquals(noteList, tableOperate.getAllNotes());
        }
    }

    @Test
    public void onDeleteNote() {
        final int NOTE_LIST_SIZE_REMOVE = 4;
        // Remove note at start, middle
        for (int i = 0; i < NOTE_LIST_SIZE_REMOVE; ++i) {
            // Old note get index -> New note remove
            EventBus.getDefault().post(new NoteDeleteEvent(noteList.get(i)));
            noteList.remove(i);
            assertEquals(noteList, tableOperate.getAllNotes());
        }
        // Remove note at end
        EventBus.getDefault().post(new NoteDeleteEvent(noteList.get(noteList.size() - 1)));
        noteList.remove(noteList.size() - 1);
        assertEquals(noteList, tableOperate.getAllNotes());
        // Remove all notes one by one
        for (int i = 0; i < noteList.size(); ++i) {
            EventBus.getDefault().post(new NoteDeleteEvent(noteList.get(i)));
        }
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    @Test
    public void onClearNote() {
        EventBus.getDefault().post(new NoteClearEvent());
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes());
    }
}