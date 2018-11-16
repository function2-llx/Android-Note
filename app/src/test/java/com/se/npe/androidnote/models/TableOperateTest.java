package com.se.npe.androidnote.models;

import android.support.v7.app.AppCompatActivity;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@RunWith(RobolectricTestRunner.class)
public class TableOperateTest {

    private TableOperate tableOperate;
    private List<Note> noteList;
    public static final int NOTE_LIST_SIZE = 20;

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
        File configDir = new File(TableConfig.FileSave.getSavePath() + "/config");
        assertNotNull(configDir);
        // successfully create file
        File configFile = new File(TableConfig.FileSave.getSavePath() + "/config/searchconfig.txt");
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
        // add group
        List<String> groupList = new ArrayList<>();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            groupList.add(DataExample.getExampleGroupName(String.valueOf(i)));
        }
        assertEquals(groupList, TableOperate.getInstance().getAllGroups());
        // remove group
        TableOperate.getInstance().removeGroup(DataExample.getExampleGroupName("3"));
        groupList.remove(3);
        assertEquals(groupList, TableOperate.getInstance().getAllGroups());
    }

    @Test
    public void tagFunctionTest() {
        List<String> tagList = new ArrayList<>();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            tagList.add(DataExample.getExampleNoteTag(String.valueOf(i)));
        }
        assertEquals(tagList, tableOperate.getAllTags());
    }

    @Test
    public void setSearchConfigAndGetSearchConfig() {
        for (String searchConfig : TableConfig.Sorter.getSorterFields()) {
            TableOperate.setSearchConfig(searchConfig);
            assertEquals(searchConfig, TableOperate.getSearchConfig());
        }
    }

    @Test
    public void getAllNotes() {
        // Whole note list
        assertEquals(noteList, tableOperate.getAllNotes("", null));

        List<Note> noteListSearched = new ArrayList<>();
        // Search for tag 3 & group 3
        noteListSearched.add(noteList.get(3));
        assertEquals(noteListSearched, tableOperate.getAllNotes(DataExample.getExampleGroupName("3"), DataExample.getExampleNoteTags("3")));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for tag 5
        noteListSearched.add(noteList.get(5));
        assertEquals(noteListSearched, tableOperate.getAllNotes("", DataExample.getExampleNoteTags("5")));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for group 7
        noteListSearched.add(noteList.get(7));
        assertEquals(noteListSearched, tableOperate.getAllNotes(DataExample.getExampleGroupName("7"), null));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for tag 3 & group 13
        assertEquals(noteListSearched, tableOperate.getAllNotes(DataExample.getExampleGroupName("3"), DataExample.getExampleNoteTags("13")));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
    }

    @Test
    public void fuzzySearch() {
        // Depends on example note
        // Whole note list
        assertEquals(noteList, tableOperate.fuzzySearch("title", "", null));
        // Empty note list
        assertEquals(new ArrayList<Note>(), tableOperate.fuzzySearch("wtf???", "", null));

        List<Note> noteListSearched = new ArrayList<>();
        // Search for title 3
        noteListSearched.add(noteList.get(3));
        noteListSearched.add(noteList.get(13));
        assertEquals(noteListSearched, tableOperate.fuzzySearch("3", "", null));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for tag 5
        noteListSearched.add(noteList.get(5));
        assertEquals(noteListSearched, tableOperate.fuzzySearch("title", "", DataExample.getExampleNoteTags("5")));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for group 7
        noteListSearched.add(noteList.get(7));
        assertEquals(noteListSearched, tableOperate.fuzzySearch("title", DataExample.getExampleGroupName("7"), null));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for tag 10 & group 10
        noteListSearched.add(noteList.get(10));
        assertEquals(noteListSearched, tableOperate.fuzzySearch("title", DataExample.getExampleGroupName("10"), DataExample.getExampleNoteTags("10")));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for tag 13 & group 15
        assertEquals(noteListSearched, tableOperate.fuzzySearch("title", DataExample.getExampleGroupName("13"), DataExample.getExampleNoteTags("15")));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
    }

    public static void addExampleNote(List<Note> noteList) {
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i));
            TableOperate.getInstance().addNote(note);
            TableOperate.getInstance().addGroup(note.getGroupName());
            noteList.add(note);
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
        assertEquals(noteList, tableOperate.getAllNotes("", null));
    }

    @Test
    public void modifyNote() {
        noteList.get(0).setTitle("title");
        tableOperate.modifyNote(noteList.get(0));
        tableOperate.modifyNote(DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN));
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
            assertEquals(noteList, tableOperate.getAllNotes("", null));
        }
        // Remove note at end
        tableOperate.removeNote(noteList.get(noteList.size() - 1));
        noteList.remove(noteList.size() - 1);
        assertEquals(noteList, tableOperate.getAllNotes("", null));
        // Remove all notes one by one
        for (int i = 0; i < noteList.size(); ++i) {
            tableOperate.removeNote(noteList.get(i));
        }
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes("", null));
        // Remove note before start/after end will not throw exception
    }

    @Test
    public void removeAllNotes() {
        tableOperate.removeAllNotes();
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes("", null));
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