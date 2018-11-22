package com.se.npe.androidnote.adapters;

import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.ListActivity;
import com.se.npe.androidnote.ListActivityTest;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.models.DataExample;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.SingletonResetter;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.models.TableOperateTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowPopupMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class NoteAdapterTest {

    private NoteAdapter noteAdapter;
    private List<Note> noteList; // used to check noteAdapter is right
    private ListActivity activity;
    private UltimateRecyclerView ultimateRecyclerView;

    @Before
    public void setUp() {
        activity = ListActivityTest.initListActivity();
        ultimateRecyclerView = activity.findViewById(R.id.ultimate_recycler_view);
        noteAdapter = new NoteAdapter(activity);

        noteList = new ArrayList<>();
        TableOperateTest.addExampleNote(noteList);
        noteList.sort(Comparator.comparing(Note::getTitle)); // sort note list

        noteAdapter.updateGroupNotesList();
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void getAdapterItemCount() {
        assertEquals(TableOperateTest.NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
    }

    @Test
    public void updateSearchList() {
        try {
            // Whole note list
            noteAdapter.updateSearchList(DataExample.EXAMPLE_TITLE_WHOLE_NOTE_LIST, null);
            assertEquals(noteList, noteAdapter.getItems());
            // Empty note list
            noteAdapter.updateSearchList(DataExample.EXAMPLE_TITLE_EMPTY_NOTE_LIST, null);
            assertEquals(new ArrayList<Note>(), noteAdapter.getItems());

            // Depends on addNote()
            List<Note> noteListSearched = new ArrayList<>();
            // Search for title 3
            for (Note note : noteList)
                if (note.getTitle().contains("3"))
                    noteListSearched.add(note);
            noteAdapter.updateSearchList("3", null);
            assertEquals(noteListSearched, noteAdapter.getItems());
            noteListSearched.clear(); // Pay attention to clear noteListSearch
            // Search for tag 5
            for (Note note : noteList)
                if (note.getTag().contains(DataExample.getExampleNoteTag("5")))
                    noteListSearched.add(note);
            noteAdapter.updateSearchList(DataExample.EXAMPLE_TITLE_WHOLE_NOTE_LIST, DataExample.getExampleNoteTags("5"));
            assertEquals(noteListSearched, noteAdapter.getItems());
            noteListSearched.clear(); // Pay attention to clear noteListSearch
            // Search for tag 10 & group 10
            for (Note note : noteList)
                if (note.getTag().contains(DataExample.getExampleNoteTag("10")))
                    noteListSearched.add(note);
            noteAdapter.setCurrentGroup(DataExample.getExampleGroupName("10"));
            noteAdapter.updateSearchList(DataExample.EXAMPLE_TITLE_WHOLE_NOTE_LIST, DataExample.getExampleNoteTags("10"));
            assertEquals(noteListSearched, noteAdapter.getItems());
            noteListSearched.clear(); // Pay attention to clear noteListSearch
            // Search for tag 13 & group 15
            noteAdapter.setCurrentGroup(DataExample.getExampleGroupName("13"));
            noteAdapter.updateSearchList(DataExample.EXAMPLE_TITLE_WHOLE_NOTE_LIST, DataExample.getExampleNoteTags("15"));
            assertEquals(noteListSearched, noteAdapter.getItems());
            noteListSearched.clear(); // Pay attention to clear noteListSearch
        } catch (IllegalStateException e) {
            // no-op
        }
    }

    @Test
    public void updateGroupNotesList() {
        // all notes
        noteAdapter.updateGroupNotesList();
        assertEquals(noteList, noteAdapter.getItems());

        // add notes
        for (int i = TableOperateTest.NOTE_LIST_SIZE; i < TableOperateTest.NOTE_LIST_SIZE + TableOperateTest.NOTE_LIST_SIZE; ++i) {
            Note note = DataExample.getExampleNote(String.valueOf(i));
            TableOperate.getInstance().addNote(note);
        }
        // before updateGroupNotesList
        assertEquals(TableOperateTest.NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
        noteAdapter.updateGroupNotesList();
        // after updateGroupNotesList
        assertEquals(TableOperateTest.NOTE_LIST_SIZE + TableOperateTest.NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
    }

    @Test
    public void updateList() {
        assertEquals(TableOperateTest.NOTE_LIST_SIZE, noteAdapter.getAdapterItemCount());
    }

    @Test
    public void setSortField() {
        for (String sortField : TableConfig.Sorter.getSorterFields()) {
            noteAdapter.setSortField(sortField);
            noteList.sort(TableConfig.Sorter.getSorterFieldToComparator(sortField));
            assertEquals(noteList, noteAdapter.getItems());
        }
    }

    @Test
    public void setCurrentGroup() {
        List<Note> noteGroupList = new ArrayList<>();
        // group 3
        for (Note note : noteList)
            if (note.getGroupName().equals(DataExample.getExampleGroupName("3")))
                noteGroupList.add(note);
        noteAdapter.setCurrentGroup(DataExample.getExampleGroupName("3"));
        assertEquals(DataExample.getExampleGroupName("3"), noteAdapter.getCurrentGroup());
        assertEquals(noteGroupList, noteAdapter.getItems());
    }

    @Test
    public void insert() {
        // insert at end
        Note noteEnd = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN + "end");
        noteAdapter.insert(noteEnd, TableOperateTest.NOTE_LIST_SIZE);
        noteList.add(TableOperateTest.NOTE_LIST_SIZE, noteEnd);
        assertEquals(noteEnd, noteAdapter.getItem(TableOperateTest.NOTE_LIST_SIZE));
        // insert at start
        Note noteStart = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN + "start");
        noteAdapter.insert(noteStart, 0);
        noteList.add(0, noteStart);
        assertEquals(noteStart, noteAdapter.getItem(0));
        // insert at middle
        for (int i = 5; i <= TableOperateTest.NOTE_LIST_SIZE; i += 5) {
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
        noteAdapter.remove(TableOperateTest.NOTE_LIST_SIZE - 1);
        noteList.remove(TableOperateTest.NOTE_LIST_SIZE - 1);
        assertEquals(noteList, noteAdapter.getItems());
        // remove at start
        noteAdapter.remove(0);
        noteList.remove(0);
        assertEquals(noteList, noteAdapter.getItems());
        // remove at middle
        for (int i = 5; i < TableOperateTest.NOTE_LIST_SIZE; i += 5) {
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
        ultimateRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        ultimateRecyclerView.setAdapter(noteAdapter);
    }

    @Test
    public void emptyViewHolder() {
        Note note = new Note();
        note.setTitle("");
        noteAdapter.insert(note, 0);
        createViewHolder();
    }

    @Test
    public void clickViewHolder() {
        createViewHolder();
        // click view holder
        RecyclerView.ViewHolder viewHolder = ultimateRecyclerView.mRecyclerView.findViewHolderForAdapterPosition(0);
        viewHolder.itemView.performClick();
        // check EditorActivity is properly started
        ListActivityTest.checkListActivityStartEditorActivity(activity, false, "");
    }

    @Test
    public void longClickViewHolder() {
        createViewHolder();
        // long click view holder
        RecyclerView.ViewHolder viewHolder = ultimateRecyclerView.mRecyclerView.findViewHolderForAdapterPosition(0);
        viewHolder.itemView.performLongClick();
        // check preview is properly done
        clickLatestPopupMenuItem(R.id.preview);
        ListActivityTest.checkListActivityStartEditorActivity(activity, true, "");
        // check set group is properly done
        clickLatestPopupMenuItem(R.id.set_group);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowAlertDialog = shadowOf(alertDialog);
        shadowAlertDialog.clickOnItem(1); // click noteAdapter.getItem(1)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick(); // click confirm
        assertEquals(noteList.get(1).getGroupName(), noteAdapter.getItem(0).getGroupName());
        // check remove group is properly done
        clickLatestPopupMenuItem(R.id.remove_from_current_group);
        assertEquals("", noteAdapter.getItem(0).getGroupName());
        // check delete is properly done
        clickLatestPopupMenuItem(R.id.delete);
        noteList.remove(0);
        assertEquals(noteList, noteAdapter.getItems());
    }

    private void clickLatestPopupMenuItem(int popupMenuItemId) {
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        ShadowPopupMenu shadowPopupMenu = shadowOf(popupMenu);
        // check popup menu is showing
        assertTrue(shadowPopupMenu.isShowing());
        // click popup menu item
        MenuItem menuItem = popupMenu.getMenu().findItem(popupMenuItemId);
        shadowPopupMenu.getOnMenuItemClickListener().onMenuItemClick(menuItem);
    }
}