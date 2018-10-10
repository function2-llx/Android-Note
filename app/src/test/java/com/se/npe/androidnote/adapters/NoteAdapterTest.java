package com.se.npe.androidnote.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.ListActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TextData;

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
public class NoteAdapterTest {

    NoteAdapter noteAdapter;

    List<Note> noteList;
    final int NOTE_LIST_SIZE = 20;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        this.noteList = new ArrayList<>();
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            noteList.add(getExampleNote(i));
        }
        AppCompatActivity activity = Robolectric.setupActivity(ListActivity.class);
        noteAdapter = new NoteAdapter(noteList, activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        UltimateRecyclerView ultimateRecyclerView = activity.findViewById(R.id.ultimate_recycler_view);
        ultimateRecyclerView.setLayoutManager(layoutManager);
        ultimateRecyclerView.setAdapter(noteAdapter);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getAllNotes() {
        // Same are noteList and noteAdapter.getAllNotes()
        assertSame(this.noteList, noteAdapter.getAllNotes());
    }

    @Test
    public void getSearchResult() {
        // Depends on example note
        // Whole note list
        assertEquals(this.noteList, noteAdapter.getSearchResult("title"));
        assertNotSame(this.noteList, noteAdapter.getSearchResult("title"));
        // Empty note list
        assertEquals(new ArrayList<Note>(), noteAdapter.getSearchResult("wtf???"));

        List<Note> noteListSearched = new ArrayList<>();
        // Depends on setUp()
        // Search for 3 using old note
        noteListSearched.add(this.noteList.get(3));
        noteListSearched.add(this.noteList.get(13));
        assertEquals(noteListSearched, noteAdapter.getSearchResult("3"));
        noteListSearched.clear(); // Pay attention to clear noteListSearch
        // Search for 3 using new note
        // Not equals because new note has a different data
        noteListSearched.add(getExampleNote(3));
        noteListSearched.add(getExampleNote(13));
        assertNotEquals(noteListSearched, noteAdapter.getSearchResult("3"));
        noteListSearched.clear();
    }

    @Test
    public void addNote() {
        // Add 5 notes
        final int NOTE_LIST_SIZE_ADD = 5;
        for (int i = 0; i < NOTE_LIST_SIZE_ADD; ++i) {
            noteAdapter.addNote(getExampleNote(NOTE_LIST_SIZE + i));
            assertSame(noteList, noteAdapter.getAllNotes());
            assertEquals(NOTE_LIST_SIZE + i + 1, noteAdapter.getAllNotes().size());
        }
    }

    @Test
    public void getNoteAt() {
        // All corresponding notes are same
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            assertSame(noteList.get(i), noteAdapter.getNoteAt(i));
        }
        // Not corresponding notes are not equal
        assertNotEquals(noteList.get(0), noteAdapter.getNoteAt(1));
        assertNotEquals(noteList.get(10), noteAdapter.getNoteAt(3));
        // New note is not equal
        Note note = getExampleNote(NOTE_LIST_SIZE);
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            assertNotEquals(note, noteAdapter.getNoteAt(i));
        }
    }

    @Test
    public void getNoteAtBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.getNoteAt(-1);
    }

    @Test
    public void getNoteAtAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.getNoteAt(NOTE_LIST_SIZE);
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
        // Get all items
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            assertSame(noteList.get(i), noteAdapter.getItem(i));
        }
    }

    @Test
    public void getItemBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.getItem(-1);
    }

    @Test
    public void getItemAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.getItem(NOTE_LIST_SIZE);
    }

    @Test
    public void insert() {
        // Insert at start, middle
        final int NOTE_LIST_SIZE_ADD = 5;
        for (int i = 0; i < NOTE_LIST_SIZE_ADD; ++i) {
            Note note = getExampleNote(NOTE_LIST_SIZE + i);
            noteAdapter.insert(note, i * NOTE_LIST_SIZE / NOTE_LIST_SIZE_ADD);
            assertSame(noteList, noteAdapter.getAllNotes());
            assertEquals(NOTE_LIST_SIZE + i + 1, noteAdapter.getAllNotes().size());
        }
        // Insert at end
        Note note = getExampleNote(NOTE_LIST_SIZE + NOTE_LIST_SIZE_ADD);
        noteAdapter.insert(note, NOTE_LIST_SIZE + NOTE_LIST_SIZE_ADD);
        assertSame(noteList, noteAdapter.getAllNotes());
        assertEquals(NOTE_LIST_SIZE + NOTE_LIST_SIZE_ADD + 1, noteAdapter.getAllNotes().size());
    }

    @Test
    public void insertBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.insert(getExampleNote(-1), -1);
    }

    @Test
    public void insertAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.insert(getExampleNote(NOTE_LIST_SIZE + 1), NOTE_LIST_SIZE + 1);
    }

    @Test
    public void remove() {
        // Remove at start, middle
        final int NOTE_LIST_SIZE_SUBTRACT = 5;
        for (int i = 0; i < NOTE_LIST_SIZE_SUBTRACT; ++i) {
            noteAdapter.remove(i);
            assertSame(noteList, noteAdapter.getAllNotes());
            assertEquals(NOTE_LIST_SIZE - i - 1, noteAdapter.getAllNotes().size());
        }
        // Remove at end
        noteAdapter.remove(noteAdapter.getAllNotes().size() - 1);
        assertSame(noteList, noteAdapter.getAllNotes());
        assertEquals(NOTE_LIST_SIZE - NOTE_LIST_SIZE_SUBTRACT - 1, noteAdapter.getAllNotes().size());
    }

    @Test
    public void removeBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.remove(-1);
    }

    @Test
    public void removeAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.remove(NOTE_LIST_SIZE);
    }

    @Test
    public void swapPositions() {
        // Swap position from/to start
        for (int i = 1; i < NOTE_LIST_SIZE; ++i) {
            noteAdapter.swapPositions(0, i);
            assertEquals(noteList, noteAdapter.getAllNotes());
            noteAdapter.swapPositions(i, 0);
            assertEquals(noteList, noteAdapter.getAllNotes());
        }
        // Swap position from/to end
        for (int i = 1; i < NOTE_LIST_SIZE; ++i) {
            noteAdapter.swapPositions(NOTE_LIST_SIZE - 1, i);
            assertEquals(noteList, noteAdapter.getAllNotes());
            noteAdapter.swapPositions(i, NOTE_LIST_SIZE - 1);
            assertEquals(noteList, noteAdapter.getAllNotes());
        }
        // Swap position in the middle
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            noteAdapter.swapPositions(i, NOTE_LIST_SIZE - i - 1);
            assertEquals(noteList, noteAdapter.getAllNotes());
        }
        // Swap same position
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            noteAdapter.swapPositions(i, i);
            assertEquals(noteList, noteAdapter.getAllNotes());
        }
    }

    @Test
    public void swapPositionsFromBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.swapPositions(-1, 0);
    }

    @Test
    public void swapPositionsToBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.swapPositions(0, -1);
    }

    @Test
    public void swapPositionsFromAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.swapPositions(NOTE_LIST_SIZE, NOTE_LIST_SIZE - 1);
    }

    @Test
    public void swapPositionsToAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        noteAdapter.swapPositions(NOTE_LIST_SIZE - 1, NOTE_LIST_SIZE);
    }

    @Test
    public void clear() {
        noteAdapter.clear();
        assertEquals(new ArrayList<Note>(), noteAdapter.getAllNotes());
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