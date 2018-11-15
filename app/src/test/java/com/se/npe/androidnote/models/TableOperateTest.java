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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

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
        noteList = new ArrayList<>();
        addExampleNote(noteList);
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
    public void groupFunctionTest() {
        List<String> groupList = new ArrayList<>();
        groupList.add(DataExample.getExampleGroupName("1"));

        TableOperate.getInstance().addGroup(DataExample.getExampleGroupName("1"));
        assertEquals(groupList,TableOperate.getInstance().getAllGroup());

        TableOperate.getInstance().removeGroup(DataExample.getExampleGroupName("1"));
        assertEquals(new ArrayList<>(),TableOperate.getInstance().getAllGroup());
    }

    @Test
    public void setSearchConfigAndGetSearchConfig() {
        for (String searchConfig : TableConfig.Sorter.SORTER_FIELDS) {
            TableOperate.setSearchConfig(searchConfig);
            assertEquals(searchConfig, TableOperate.getSearchConfig());
        }
    }

    @Test
    public void getAllNotes() {
        List<String> tagList = new ArrayList<>();
        tagList.add("TagZ");
        assertEquals(noteList, tableOperate.getAllNotes("",null));
        assertEquals(2,tableOperate.getAllNotes(DataExample.getExampleGroupName("1"),null).size());
        assertEquals(2,tableOperate.getAllNotes("",tagList).size());
        assertEquals(1,tableOperate.getAllNotes(DataExample.getExampleGroupName("1"),tagList).size());
    }

    @Test
    public void fuzzySearch() {
        // Depends on example note
        // Whole note list cannot be test - precise search
        // Empty note list
        assertEquals(new ArrayList<Note>(), tableOperate.fuzzySearch("wtf???","",null));

        List<Note> noteListSearched = new ArrayList<>();
        // Search for 3 using old note
        noteListSearched.add(this.noteList.get(3));
        assertEquals(noteListSearched, tableOperate.fuzzySearch(this.noteList.get(3).getTitle(),"",null));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for 3 using new note
        // Not equals because new note has a different data
        noteListSearched.add(DataExample.getExampleNote(String.valueOf(3)));
        assertNotEquals(noteListSearched, tableOperate.fuzzySearch(this.noteList.get(3).getTitle(),"",null));
        noteListSearched.clear();

        List<String> tagList = new ArrayList<>();
        tagList.add("TagZ");
        assertEquals(20,tableOperate.fuzzySearch("This","",null).size());
        assertEquals(2,tableOperate.fuzzySearch("This",DataExample.getExampleGroupName("1"),null).size());
        assertEquals(1,tableOperate.fuzzySearch("This",DataExample.getExampleGroupName("1"),tagList).size());
        assertEquals(2,tableOperate.fuzzySearch("This","",tagList).size());
    }

    public static void addExampleNote(List<Note> noteList) {
        for (int i = 0; i < NOTE_LIST_SIZE - 3; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i));
            TableOperate.getInstance().addNote(note);
            noteList.add(note);
        }
        Note note;

        //Note with Group1
        note = DataExample.getExampleNote(String.valueOf(NOTE_LIST_SIZE - 1));
        note.setGroupName(DataExample.getExampleGroupName("1"));
        TableOperate.getInstance().addNote(note);
        noteList.add(note);

        //Note with Group1 and TagZ
        note = DataExample.getExampleNote(String.valueOf(NOTE_LIST_SIZE - 1));
        note.setGroupName(DataExample.getExampleGroupName("1"));
        note.getTag().add("TagZ");
        TableOperate.getInstance().addNote(note);
        noteList.add(note);

        //Note with Group2 and TagZ
        note = DataExample.getExampleNote(String.valueOf(NOTE_LIST_SIZE - 1));
        note.setGroupName(DataExample.getExampleGroupName("2"));
        note.getTag().add("TagZ");
        TableOperate.getInstance().addNote(note);
        noteList.add(note);
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
        assertEquals(noteList, tableOperate.getAllNotes("",null));
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
            assertEquals(noteList, tableOperate.getAllNotes("",null));
        }
        // Remove note at end
        tableOperate.removeNote(noteList.get(noteList.size() - 1));
        noteList.remove(noteList.size() - 1);
        assertEquals(noteList, tableOperate.getAllNotes("",null));
        // Remove all notes one by one
        for (int i = 0; i < noteList.size(); ++i) {
            tableOperate.removeNote(noteList.get(i));
        }
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes("",null));
        // Remove note before start/after end will not throw exception
    }

    @Test
    public void removeAllNotes() {
        tableOperate.removeAllNotes();
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes("",null));
    }

    @Test
    public void removeAllNotesAndAddNote() {
        // Remove all notes & Add note
        removeAllNotes();
        addExampleNote(noteList);
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
}