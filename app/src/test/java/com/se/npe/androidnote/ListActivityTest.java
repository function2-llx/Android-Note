package com.se.npe.androidnote;

import android.Manifest;
import android.content.Intent;
import android.view.MenuItem;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.adapters.NoteAdapter;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.SingletonResetter;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.models.TableOperateTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowActivity;

import java.util.ArrayList;

import static com.se.npe.androidnote.EditorActivity.CURRENT_GROUP;
import static com.se.npe.androidnote.EditorActivity.VIEW_ONLY;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class ListActivityTest {

    private ListActivity activity;
    private UltimateRecyclerView ultimateRecyclerView;
    private NoteAdapter noteAdapter;

    @Before
    public void setUp() {
        activity = initListActivity();
        ultimateRecyclerView = activity.findViewById(R.id.ultimate_recycler_view);
        noteAdapter = (NoteAdapter) ultimateRecyclerView.getAdapter();
        TableOperateTest.addExampleNote(new ArrayList<>());
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void onOptionsItemSelected() {
        clickOptionsMenuItem(R.id.menu_new_note);
        checkListActivityStartEditorActivity(activity, false, "");
        clickOptionsMenuItem(R.id.menu_open);
        // TODO: do something with open
        clickOptionsMenuItem(R.id.sort_title);
        assertEquals(TableConfig.Sorter.getSorterFieldToComparator(TableConfig.Sorter.getSorterOptionToField(R.id.sort_title)), noteAdapter.getComparator());
        clickOptionsMenuItem(R.id.sort_created_time);
        assertEquals(TableConfig.Sorter.getSorterFieldToComparator(TableConfig.Sorter.getSorterOptionToField(R.id.sort_created_time)), noteAdapter.getComparator());
        clickOptionsMenuItem(R.id.sort_modified_time);
        assertEquals(TableConfig.Sorter.getSorterFieldToComparator(TableConfig.Sorter.getSorterOptionToField(R.id.sort_modified_time)), noteAdapter.getComparator());
        clickOptionsMenuItem(R.id.clear);
        assertEquals(new ArrayList<Note>(), TableOperate.getInstance().getAllNotes("",null));
    }

    @Test
    public void onRefresh() {
        ultimateRecyclerView.mSwipeRefreshLayout.post(() ->
                ultimateRecyclerView.mSwipeRefreshLayout.setRefreshing(true)
        );
    }

    public static ListActivity initListActivity() {
        TableOperate.init(RuntimeEnvironment.application.getApplicationContext()); // workaround for TableOperate.init()
        PermissionTest.grantPermission(RuntimeEnvironment.application, Manifest.permission.RECORD_AUDIO); // workaround for permission RECORD_AUDIO
        return Robolectric.setupActivity(ListActivity.class);
    }

    public static void checkListActivityStartEditorActivity(ListActivity listActivity, boolean expectedViewOnly, String expectedCurrentGroup) {
        // check EditorActivity is properly started
        ShadowActivity shadowActivity = shadowOf(listActivity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();
        assertEquals(EditorActivity.class.getName(), nextIntent.getComponent().getClassName());
        // check EditorActivity's extra is properly transported
        assertEquals(expectedViewOnly, nextIntent.getBooleanExtra(VIEW_ONLY, false));
        assertEquals(expectedCurrentGroup, nextIntent.getStringExtra(CURRENT_GROUP));
    }

    private void clickOptionsMenuItem(int optionsMenuItemId) {
        MenuItem menuItem = shadowOf(activity).getOptionsMenu().findItem(optionsMenuItemId);
        activity.onOptionsItemSelected(menuItem);
    }
}