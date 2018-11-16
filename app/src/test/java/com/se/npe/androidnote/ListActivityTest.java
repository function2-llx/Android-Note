package com.se.npe.androidnote;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.SubMenu;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.adapters.NoteAdapter;
import com.se.npe.androidnote.adapters.TagGroupManager;
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
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.support.v4.ShadowSwipeRefreshLayout;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

import co.lujun.androidtagview.TagView;

import static com.se.npe.androidnote.EditorActivity.CURRENT_GROUP;
import static com.se.npe.androidnote.EditorActivity.VIEW_ONLY;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class ListActivityTest {

    private ListActivity activity;
    private ShadowActivity shadowActivity;
    private UltimateRecyclerView ultimateRecyclerView;
    private NoteAdapter noteAdapter;

    @Before
    public void setUp() {
        activity = initListActivity();
        shadowActivity = shadowOf(activity);
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
        shadowActivity.clickMenuItem(R.id.menu_new_note);
        checkListActivityStartEditorActivity(activity, false, "");
        shadowActivity.clickMenuItem(R.id.menu_open);
        checkListActivityStartFileChooser();
        shadowActivity.clickMenuItem(R.id.sort_title);
        assertEquals(TableConfig.Sorter.getSorterFieldToComparator(TableConfig.Sorter.getSorterOptionToField(R.id.sort_title)), noteAdapter.getComparator());
        shadowActivity.clickMenuItem(R.id.sort_created_time);
        assertEquals(TableConfig.Sorter.getSorterFieldToComparator(TableConfig.Sorter.getSorterOptionToField(R.id.sort_created_time)), noteAdapter.getComparator());
        shadowActivity.clickMenuItem(R.id.sort_modified_time);
        assertEquals(TableConfig.Sorter.getSorterFieldToComparator(TableConfig.Sorter.getSorterOptionToField(R.id.sort_modified_time)), noteAdapter.getComparator());
        shadowActivity.clickMenuItem(R.id.clear);
        assertEquals(new ArrayList<Note>(), TableOperate.getInstance().getAllNotes("", null));
    }

    @Test
    public void onNewNoteButtonClicked() {
        activity.findViewById(R.id.new_note_button).performClick();
        checkListActivityStartEditorActivity(activity, false, "");
    }

    @Test
    public void onRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = ultimateRecyclerView.mSwipeRefreshLayout;
        ShadowSwipeRefreshLayout shadowSwipeRefreshLayout = (ShadowSwipeRefreshLayout) shadowOf(swipeRefreshLayout);
        swipeRefreshLayout.post(() ->
                swipeRefreshLayout.setRefreshing(true)
        ); // swipe-refresh animation
        shadowSwipeRefreshLayout.getOnRefreshListener().onRefresh(); // swipe-refresh on
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks(); // enable delayed swipe-refresh
    }

    @Test
    public void onNavigate() throws NoSuchFieldException, IllegalAccessException {
//        NavigationView navigationView = activity.findViewById(R.id.nav_view);
//        MenuItem menuItem = navigationView.getMenu().findItem(R.id.group_all_notes);
//        navigationView.setCheckedItem(R.id.group_all_notes);
//        shadowOf(navigationView).getOnClickListener().performClick(0);
//        navigationView.getMenu().performIdentifierAction(R.id.group_all_notes, 0);
        // trick: click menu item of NavigationView directly
//        shadowActivity.clickMenuItem(R.id.group_all_notes);
//        assertEquals("", noteAdapter.getCurrentGroup());
//        shadowActivity.clickMenuItem(R.id.group_groups);
//        shadowActivity.clickMenuItem(R.id.group_operations);
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        // get OnNavigationItemSelectedListener
        Field field = NavigationView.class.getDeclaredField("listener");
        field.setAccessible(true);
        NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = (NavigationView.OnNavigationItemSelectedListener) field.get(navigationView);

        Menu navigationViewGroupMenu = navigationView.getMenu().findItem(R.id.groups).getSubMenu();
        onNavigationItemSelectedListener.onNavigationItemSelected(navigationViewGroupMenu.findItem(R.id.all_notes));
//        refreshGroups
//        onNavigationItemSelectedListener.onNavigationItemSelected(navigationViewGroupMenu.getItem(1));
//        onNavigationItemSelectedListener.onNavigationItemSelected(navigationViewGroupMenu.findItem(R.id.group_groups));
        Menu navigationViewGroupManageMenu = navigationView.getMenu().findItem(R.id.group_manage).getSubMenu();
        onNavigationItemSelectedListener.onNavigationItemSelected(navigationViewGroupManageMenu.findItem(R.id.new_group));
        onNavigationItemSelectedListener.onNavigationItemSelected(navigationViewGroupManageMenu.findItem(R.id.manage_group));
    }

    @Test
    public void onSearch() throws NoSuchFieldException, IllegalAccessException {
        SearchView searchView = activity.findViewById(R.id.action_search);
        TagGroupManager tagGroupManager = activity.findViewById(R.id.tag_group_manager);

        // open searchView
        searchView.setIconified(false);

        // query searchView
        searchView.setQuery("title", false); // text changed
        assertEquals(TableOperateTest.NOTE_LIST_SIZE, noteAdapter.getItemCount());
        searchView.findViewById(R.id.search_go_btn).performClick(); // submit
        assertEquals(TableOperateTest.NOTE_LIST_SIZE, noteAdapter.getItemCount());
        searchView.setQuery("wtf???", true); // text changed & submit
        assertEquals(0, noteAdapter.getItemCount());

        // get OnTagClickListener
        Field field = TagView.class.getDeclaredField("mOnTagClickListener");
        field.setAccessible(true);
        TagView.OnTagClickListener onTagClickListener = (TagView.OnTagClickListener) field.get(tagGroupManager);

        // click tag
        searchView.setQuery("", true);
        onTagClickListener.onTagClick(3, null); // click tag 3 with tag-all unchecked
        assertEquals(1, noteAdapter.getItemCount());
        onTagClickListener.onTagClick(8, null); // click tag 3 & 8 with tag-all unchecked
        assertEquals(2, noteAdapter.getItemCount());

        // click tag & query searchView
        searchView.setQuery("8", false);
        assertEquals(1, noteAdapter.getItemCount());
        searchView.setQuery("13", true);
        assertEquals(0, noteAdapter.getItemCount());

        // with tag-all checked
        // click tag
        searchView.setQuery("", true);
        onTagClickListener.onTagClick(TableOperateTest.NOTE_LIST_SIZE, null);
        assertEquals(TableOperateTest.NOTE_LIST_SIZE, noteAdapter.getItemCount());

        // click tag & query searchView
        searchView.setQuery("8", false);
        assertEquals(2, noteAdapter.getItemCount()); // 8, 18
        searchView.setQuery("13", true);
        assertEquals(1, noteAdapter.getItemCount());

        // just to cover onTagLongClick & onTagCrossClick
        onTagClickListener.onTagLongClick(0, null);
        onTagClickListener.onTagCrossClick(0);

        searchView.setIconified(true); // close searchView
        searchView.findViewById(R.id.search_close_btn).performClick(); // close searchView
    }

    private void onActivityResult(Intent openFileIntent) {
        // simulate onActivityResult by passing in uri to openFileIntent
        // suffix: null
        Uri uri = FileUtils.getUri(new File(DataExample.getExamplePath(DataExample.EXAMPLE_MIX_IN)));
        shadowActivity.receiveResult(openFileIntent, AppCompatActivity.RESULT_OK, new Intent().setData(uri));
        // suffix: note
        uri = FileUtils.getUri(new File(DataExample.getExamplePath(DataExample.EXAMPLE_MIX_IN) + ".note"));
        shadowActivity.receiveResult(openFileIntent, AppCompatActivity.RESULT_OK, new Intent().setData(uri));
        // suffix: pdf
        uri = FileUtils.getUri(new File(DataExample.getExamplePath(DataExample.EXAMPLE_MIX_IN) + ".pdf"));
        shadowActivity.receiveResult(openFileIntent, AppCompatActivity.RESULT_OK, new Intent().setData(uri));
    }

    @Test
    public void setTitle() {
        final CharSequence title = "title";
        activity.setTitle(title);
        assertEquals(title, activity.getTitle());
        assertEquals(title, Objects.requireNonNull(activity.getSupportActionBar()).getTitle());
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

    private void checkListActivityStartFileChooser() {
        Intent nextIntent = shadowActivity.getNextStartedActivity();
        assertEquals(Intent.ACTION_CHOOSER, nextIntent.getAction());
        onActivityResult(nextIntent);
    }
}