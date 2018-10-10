package com.se.npe.androidnote.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.ListActivity;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TextData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NoteAdapterTest {
    @Mock
    NoteAdapter mockNoteAdapter;

    List<Note> noteList;
    final int NOTE_LIST_SIZE = 20;

    @Mock
    AppCompatActivity mockActivity;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        // Same are noteList and mockNoteAdapter.getAllNotes()
        assertSame(this.noteList, mockNoteAdapter.getAllNotes());
    }

    @Test
    public void getSearchResult() {
        // Depends on example note
        // Whole note list
        assertEquals(this.noteList, mockNoteAdapter.getSearchResult("title"));
        assertNotSame(this.noteList, mockNoteAdapter.getSearchResult("title"));
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
            mockNoteAdapter.addNote(getExampleNote(NOTE_LIST_SIZE + i));
            assertSame(noteList, mockNoteAdapter.getAllNotes());
            assertEquals(NOTE_LIST_SIZE + i + 1, mockNoteAdapter.getAllNotes().size());
        }
    }

    @Test
    public void getNoteAt() {
        // All corresponding notes are same
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            assertSame(noteList.get(i), mockNoteAdapter.getNoteAt(i));
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
    public void getNoteAtBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.getNoteAt(-1);
    }

    @Test
    public void getNoteAtAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.getNoteAt(NOTE_LIST_SIZE);
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
            assertSame(noteList.get(i), mockNoteAdapter.getItem(i));
        }
    }

    @Test
    public void getItemBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.getItem(-1);
    }

    @Test
    public void getItemAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.getItem(NOTE_LIST_SIZE);
    }


    @Test
    public void insert() {
        // Insert at start, middle
        final int NOTE_LIST_SIZE_ADD = 5;
        for (int i = 0; i < NOTE_LIST_SIZE_ADD; ++i) {
            Note note = getExampleNote(NOTE_LIST_SIZE + i);
            mockNoteAdapter.insert(note, i * NOTE_LIST_SIZE / NOTE_LIST_SIZE_ADD);
            assertSame(noteList, mockNoteAdapter.getAllNotes());
            assertEquals(NOTE_LIST_SIZE + i + 1, mockNoteAdapter.getAllNotes().size());
        }
        // Insert at end
        Note note = getExampleNote(NOTE_LIST_SIZE + NOTE_LIST_SIZE_ADD);
        mockNoteAdapter.insert(note, NOTE_LIST_SIZE + NOTE_LIST_SIZE_ADD);
        assertSame(noteList, mockNoteAdapter.getAllNotes());
        assertEquals(NOTE_LIST_SIZE + NOTE_LIST_SIZE_ADD + 1, mockNoteAdapter.getAllNotes().size());
    }

    @Test
    public void insertBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.insert(getExampleNote(-1), -1);
    }

    @Test
    public void insertAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.insert(getExampleNote(NOTE_LIST_SIZE + 1), NOTE_LIST_SIZE + 1);
    }

    @Test
    public void remove() {
        // Remove at start, middle
        final int NOTE_LIST_SIZE_SUBTRACT = 5;
        for (int i = 0; i < NOTE_LIST_SIZE_SUBTRACT; ++i) {
            mockNoteAdapter.remove(i);
            assertSame(noteList, mockNoteAdapter.getAllNotes());
            assertEquals(NOTE_LIST_SIZE - i - 1, mockNoteAdapter.getAllNotes().size());
        }
        // Remove at end
        mockNoteAdapter.remove(mockNoteAdapter.getAllNotes().size() - 1);
        assertSame(noteList, mockNoteAdapter.getAllNotes());
        assertEquals(NOTE_LIST_SIZE - NOTE_LIST_SIZE_SUBTRACT - 1, mockNoteAdapter.getAllNotes().size());
    }

    @Test
    public void removeBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.remove(-1);
    }

    @Test
    public void removeAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.remove(NOTE_LIST_SIZE);
    }

    @Test
    public void swapPositions() {
        // Swap position from/to start
        for (int i = 1; i < NOTE_LIST_SIZE; ++i) {
            mockNoteAdapter.swapPositions(0, i);
            assertEquals(noteList, mockNoteAdapter.getAllNotes());
            mockNoteAdapter.swapPositions(i, 0);
            assertEquals(noteList, mockNoteAdapter.getAllNotes());
        }
        // Swap position from/to end
        for (int i = 1; i < NOTE_LIST_SIZE; ++i) {
            mockNoteAdapter.swapPositions(NOTE_LIST_SIZE - 1, i);
            assertEquals(noteList, mockNoteAdapter.getAllNotes());
            mockNoteAdapter.swapPositions(i, NOTE_LIST_SIZE - 1);
            assertEquals(noteList, mockNoteAdapter.getAllNotes());
        }
        // Swap position in the middle
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            mockNoteAdapter.swapPositions(i, NOTE_LIST_SIZE - i - 1);
            assertEquals(noteList, mockNoteAdapter.getAllNotes());
        }
        // Swap same position
        for (int i = 0; i < NOTE_LIST_SIZE; ++i) {
            mockNoteAdapter.swapPositions(i, i);
            assertEquals(noteList, mockNoteAdapter.getAllNotes());
        }
    }

    @Test
    public void swapPositionsFromBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.swapPositions(-1, 0);
    }

    @Test
    public void swapPositionsToBeforeStart() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.swapPositions(0, -1);
    }

    @Test
    public void swapPositionsFromAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.swapPositions(NOTE_LIST_SIZE, NOTE_LIST_SIZE - 1);
    }

    @Test
    public void swapPositionsToAfterEnd() {
        // Expect exception thrown
        expectedException.expect(IndexOutOfBoundsException.class);
        mockNoteAdapter.swapPositions(NOTE_LIST_SIZE - 1, NOTE_LIST_SIZE);
    }

    @Test
    public void clear() {
        mockNoteAdapter.clear();
        assertEquals(new ArrayList<Note>(), mockNoteAdapter.getAllNotes());
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