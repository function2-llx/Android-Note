package com.se.npe.androidnote.adapters;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.ListActivity;
import com.se.npe.androidnote.PermissionTest;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.models.DataExample;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.SingletonResetter;
import com.se.npe.androidnote.models.TableOperate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NoteAdapterTest {

    private NoteAdapter noteAdapter;
    private static final int NOTE_LIST_SIZE = 20;
    private List<Note> noteList;
    private UltimateRecyclerView ultimateRecyclerView;

    @Before
    public void setUp() {
        TableOperate.init(RuntimeEnvironment.application.getApplicationContext()); // workaround for TableOperate.init()
        PermissionTest.grantPermission(RuntimeEnvironment.application, Manifest.permission.RECORD_AUDIO); // workaround for permission RECORD_AUDIO
        ListActivity activity = Robolectric.setupActivity(ListActivity.class);
        ultimateRecyclerView = activity.findViewById(R.id.ultimate_recycler_view);
        noteAdapter = new NoteAdapter(activity);

        noteList = new ArrayList<>();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i));
            noteList.add(note);
            TableOperate.getInstance().addNote(note);
        }
        noteAdapter.updateAllNotesList();
        noteList.sort(Comparator.comparing(Note::getTitle)); // sort note list
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void setComparator() {
        for (int i = 0; i < 3; ++i) {
            Comparator<Note> comparator;
            switch (i) {
                case 0: {
                    comparator = Comparator.comparing(Note::getTitle);
                    break;
                }
                case 1: {
                    comparator = Comparator.comparing(Note::getStartTime);
                    break;
                }
                default: {
                    comparator = Comparator.comparing(Note::getModifyTime);
                    break;
                }
            }
            noteAdapter.setComparator(comparator);
            noteList.sort(comparator);
            assertEquals(noteList, noteAdapter.getItems());
        }
    }

    @Test
    public void getAdapterItemCount() {
        assertEquals(NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
    }

    @Test
    public void updateAllNotesList() {
        noteAdapter.updateAllNotesList();
        assertEquals(NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
        // add notes
        for (int i = NOTE_LIST_SIZE; i < NOTE_LIST_SIZE + NOTE_LIST_SIZE; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i));
            TableOperate.getInstance().addNote(note);
        }
        // before updateAllNotesList
        assertEquals(NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
        noteAdapter.updateAllNotesList();
        // after updateAllNotesList
        assertEquals(NOTE_LIST_SIZE + NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
    }

    @Test
    public void updateSearchList() {
        // Whole note list
        noteAdapter.updateSearchList("title");
        assertEquals(noteList, noteAdapter.getItems());
        // Empty note list
        noteAdapter.updateSearchList("wtf???");
        assertEquals(new ArrayList<Note>(), noteAdapter.getItems());

        // Depends on addNote()
        // Search for 3
        List<Note> noteListSearched = new ArrayList<>();
        for (Note note : noteList) {
            if (note.getTitle().contains("3")) {
                noteListSearched.add(note);
            }
        }
        noteListSearched.sort(Comparator.comparing(Note::getTitle));
        noteAdapter.updateSearchList("3");
        assertEquals(noteListSearched, noteAdapter.getItems());
    }

    @Test
    public void updateGroupNotesList() {
    }

    @Test
    public void updateList() {
        assertEquals(NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
    }

    @Test
    public void insert() {
        // insert at end
        Note noteEnd = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN + "end");
        noteAdapter.insert(noteEnd, NOTE_LIST_SIZE);
        noteList.add(NOTE_LIST_SIZE, noteEnd);
        assertEquals(noteEnd, noteAdapter.getItem(NOTE_LIST_SIZE));
        // insert at start
        Note noteStart = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN + "start");
        noteAdapter.insert(noteStart, 0);
        noteList.add(0, noteStart);
        assertEquals(noteStart, noteAdapter.getItem(0));
        // insert at middle
        for (int i = 5; i <= NOTE_LIST_SIZE; i += 5) {
            Note note = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN + i);
            noteAdapter.insert(note, i);
            noteList.add(i, note);
            assertEquals(note, noteAdapter.getItem(i));
        }
        assertEquals(noteList, noteAdapter.getItems());
    }

    @Test
    public void remove() {
        // remove at end
        noteAdapter.remove(NOTE_LIST_SIZE - 1);
        noteList.remove(NOTE_LIST_SIZE - 1);
        assertEquals(noteList, noteAdapter.getItems());
        // remove at start
        noteAdapter.remove(0);
        noteList.remove(0);
        assertEquals(noteList, noteAdapter.getItems());
        // remove at middle
        for (int i = 5; i < NOTE_LIST_SIZE; i += 5) {
            noteAdapter.remove(i);
            noteList.remove(i);
            assertEquals(noteList, noteAdapter.getItems());
        }
        // remove to empty
        for (int i = noteAdapter.getAdapterItemCount(); i > 0; --i) {
            noteAdapter.remove(0);
            noteList.remove(0);
            assertEquals(noteList, noteAdapter.getItems());
        }
    }

    @Test
    public void clear() {
        noteAdapter.clear();
        noteList.clear();
        assertEquals(0, noteAdapter.getAdapterItemCount());
        assertEquals(noteList, noteAdapter.getItems());
    }

    @Test
    public void createViewHolder() {
        ultimateRecyclerView.setLayoutManager(new LinearLayoutManager(RuntimeEnvironment.application.getApplicationContext()));
        ultimateRecyclerView.setAdapter(noteAdapter);
    }

    @Test
    public void clickViewHolder() {
        createViewHolder();
        ultimateRecyclerView.getChildAt(0).performClick();
    }

    @Test
    public void longClickViewHolder() {
        createViewHolder();
        ultimateRecyclerView.getChildAt(0).performLongClick();
    }
}