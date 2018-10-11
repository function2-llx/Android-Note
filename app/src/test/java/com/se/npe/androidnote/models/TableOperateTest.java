package com.se.npe.androidnote.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TableOperateTest {

    private TableOperate tableOperate;
    private List<Note> noteList;
    private final int NOTE_LIST_SIZE = 20;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        tableOperate = new TableOperate(activity.getApplicationContext());
    }

    @After
    public void tearDown() throws Exception {
        // "helper" is the static variable name which holds the singleton MySQLiteOpenHelper instance
        resetSingleton(MySQLiteOpenHelper.class, "helper");
        // "manager" is the static variable name which holds the singleton DBManager instance
        resetSingleton(DBManager.class, "manager");
    }

    @Test
    public void encodeNote_decodeNote() {
        // Encode & Decode
        List<IData> dataList = getExampleDataList("encode_decode");
        assertEquals(dataList, tableOperate.decodeNote(tableOperate.encodeNote(dataList)));
    }

    @Test
    public void getAllNotes() {
        // Add note & Get all notes
        addNote();
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    @Test
    public void getNoteAt() {
        // Add note & Get note at each index
        addNote();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            assertEquals(noteList.get(i), tableOperate.getNoteAt(noteList.get(i).getIndex())); // noteList.get(i).getIndex() == i + 1
        }
    }

    @Test
    public void getNoteAtBeforeStart() {
        addNote();
        expectedException.expect(IndexOutOfBoundsException.class);
        tableOperate.getNoteAt(noteList.get(0).getIndex() - 1); // noteList.get(0).getIndex() == 1
    }

    @Test
    public void getNoteAtAfterEnd() {
        addNote();
        expectedException.expect(IndexOutOfBoundsException.class);
        tableOperate.getNoteAt(noteList.get(NOTE_LIST_SIZE - 1).getIndex() + 1); // noteList.get(NOTE_LIST_SIZE - 1).getIndex() == NOTE_LIST_SIZE
    }

    @Test
    public void addNote() {
        noteList = new ArrayList<Note>();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            Note note = getExampleNote(i);
            noteList.add(note);
            tableOperate.addNote(note);
            // SQL index starts at 1
            assertEquals(noteList.get(i).getIndex(), i + 1);
        }
    }

    @Test
    public void setNoteAt() {
        // Add note & Set note at some index & Get notes
        addNote();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            Note note = getExampleNote(i + NOTE_LIST_SIZE);
            // Old note get index -> New note set
            tableOperate.setNoteAt(noteList.get(i).getIndex(), note);
            noteList.set(i, note);
            assertEquals(note, tableOperate.getNoteAt(note.getIndex()));
        }
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    @Test
    public void removeNoteAt() {
        // Add note & Remove note at some index & Get notes
        addNote();
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
        // Remove all notes
        for (int i = 0; i < noteList.size(); ++i) {
            tableOperate.removeNoteAt(noteList.get(i).getIndex());
        }
        noteList.clear();
        assertEquals(noteList, tableOperate.getAllNotes());
    }

    // @Test
    // public void removeNoteAtBeforeStart() {
    //     addNote();
    //     expectedException.expect(IndexOutOfBoundsException.class);
    //     tableOperate.removeNoteAt(noteList.get(0).getIndex() - 1); // noteList.get(0).getIndex() == 1
    // }

    // @Test
    // public void removeNoteAtAfterEnd() {
    //     addNote();
    //     expectedException.expect(IndexOutOfBoundsException.class);
    //     tableOperate.removeNoteAt(noteList.get(NOTE_LIST_SIZE - 1).getIndex() + 1); // noteList.get(NOTE_LIST_SIZE - 1).getIndex() == NOTE_LIST_SIZE
    // }

    @Test
    public void loadFromFile() {
    }

    @Test
    public void saveToFile() {
    }

    @NonNull
    private Note getExampleNote(int data) {
        // Create a new example note
        String title = "This is the title for " + String.valueOf(data);
        List<IData> content = new ArrayList<>();
        content.add(new TextData("This is the content for " + String.valueOf(data)));
        return new Note(title, content);
    }

    @NonNull
    private List<IData> getExampleDataList(String data) {
        // Create a new example data list
        List<IData> dataList = new ArrayList<>();
        dataList.add(new TextData("This is the TextData text for " + data));
        dataList.add(new PictureData("This is the PictureData picture path for " + data, Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)));
        dataList.add(new SoundData("This is the SoundData sound path for " + data, "This is the SoundData text for " + data));
        dataList.add(new VideoData("This is the VideoData video path for " + data));
        return dataList;
    }

    /**
     * reset Singleton instance of class
     *
     * @param clazz     the name of class
     * @param fieldName the field name of Singleton instance
     */
    private void resetSingleton(Class clazz, String fieldName) {
        Field instance;
        try {
            instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}