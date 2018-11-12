package com.se.npe.androidnote.models;

import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.interfaces.IData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

@RunWith(RobolectricTestRunner.class)
public class NoteTest {

    private Note note;
    private Note noteNotEquals;
    private TableOperate tableOperate;

    @Before
    public void setUp() {
        note = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN);
        noteNotEquals = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN + DataExample.EXAMPLE_MIX_IN);
        // set up tableOperate
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        TableOperate.init(activity.getApplicationContext());
        tableOperate = TableOperate.getInstance();
        // add note to initialize DBindex
        tableOperate.addNote(note);
        tableOperate.addNote(noteNotEquals);
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void buildTest() {
        List<IData> content = new ArrayList<IData>();
        content.add(new TextData("text"));
        List<String> tags = new ArrayList<String>();
        tags.add("TagZ");
        Note note1 = new Note("title",content);
        Note note2 = new Note("title",content,tags);
        note1.getTag().add("TagZ");
        assertEquals(note1,note2);
    }

    @Test
    public void getTitle() {
        assertEquals(DataExample.getExampleNoteTitle(DataExample.EXAMPLE_MIX_IN), note.getTitle());
    }

    @Test
    public void setTitle() {
        final String NOTE_TITLE = DataExample.getExampleNoteTitle("setTitle test");
        note.setTitle(NOTE_TITLE);
        assertEquals(NOTE_TITLE, note.getTitle());
    }

    @Test
    public void getContent() {
        assertEquals(DataExample.getExampleDataList(DataExample.EXAMPLE_MIX_IN), note.getContent());
    }

    @Test
    public void setContent() {
        final List<IData> NOTE_CONTENT = DataExample.getExampleDataList("setContent test");
        note.setContent(NOTE_CONTENT);
        assertEquals(NOTE_CONTENT, note.getContent());
    }

    @Test
    public void setStartTime() {
        // set start time & get start time
        Date startTime = new Date();
        note.setStartTime(startTime);
        assertEquals(startTime, note.getStartTime());
    }

    @Test
    public void setModifyTime() {
        // set modify time & get modify time
        Date modifyTime = new Date();
        note.setModifyTime(modifyTime);
        assertEquals(modifyTime, note.getModifyTime());
    }

    @Test
    public void getTag() {
        assertEquals(DataExample.getExampleNoteTag(DataExample.EXAMPLE_MIX_IN), note.getTag());
    }

    @Test
    public void setTag() {
        final List<String> NOTE_TAG = DataExample.getExampleNoteTag("setTag test");
        note.setTag(NOTE_TAG);
        assertEquals(NOTE_TAG, note.getTag());
    }

    @Test
    public void getIndex() {
        // index starts from 1
        assertEquals(1, note.getIndex());
    }

    @Test
    public void groupFunc() {
        assertEquals("",note.getGroupName());
        note.setGroupName("Group1");
        assertEquals("Group1",note.getGroupName());
    }

    @Test
    public void setIndex() {
        final int NOTE_INDEX = 100;
        note.setIndex(NOTE_INDEX);
        assertEquals(NOTE_INDEX, note.getIndex());
    }

    @Test
    public void getPreview() {
        Note tempNote = new Note();
        TextData tData = new TextData("text");
        PictureData pData = new PictureData("Pic");
        tempNote.getContent().add(tData);
        tempNote.getContent().add(pData);
        tempNote.setGroupName("Group");
        tempNote.setStartTime(new Date());
        tempNote.setModifyTime(new Date());

        assertEquals("text",tempNote.getPreview().text);
        assertEquals("Pic",tempNote.getPreview().picturePath);
        assertEquals("Group",tempNote.getPreview().groupName);
        assertEquals(tempNote.getStartTime(),tempNote.getPreview().startTime);
        assertEquals(tempNote.getModifyTime(),tempNote.getPreview().modifyTime);
        assertEquals("this is tile for -1",tempNote.getPreview().title);
    }

    @Test
    public void equalsTest() {
        // No two notes are equal
        // Not equals Note are different
        assertFalse(noteNotEquals.equals(note));
        assertNotEquals(note.getIndex(), noteNotEquals.getIndex());
    }

    @Test
    public void hashCodeTest() {
        // No two notes are equal
        assertNotEquals(noteNotEquals.hashCode(), note.hashCode());
    }

    @Test
    public void toStringTest() {
        // No two notes are equal
        assertNotEquals(noteNotEquals.toString(), note.toString());
    }
}