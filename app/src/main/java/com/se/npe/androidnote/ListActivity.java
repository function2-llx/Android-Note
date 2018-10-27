package com.se.npe.androidnote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.itemTouchHelper.SimpleItemTouchHelperCallback;
import com.se.npe.androidnote.adapters.GroupAdapter;
import com.se.npe.androidnote.adapters.NoteAdapter;
import com.se.npe.androidnote.events.NoteDeleteEvent;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableOperate;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * show a list of the preview of note(data stored in noteCollection)
 * show a search input text(implement it using SearchView)
 *
 * @author llx
 */
public class ListActivity extends AppCompatActivity {
    private LinearLayoutManager layoutManager;
    private NoteAdapter noteAdapter, searchAdapter;
    private UltimateRecyclerView ultimateRecyclerView;
    private SlidingMenu slidingMenu;

    public SlidingMenu getSlidingMenu() {
        return slidingMenu;
    }

    /* Options menu */

    final static int MENU_SORT_GROUP_ID = 2333;

    private void setSortOption(int sortOptionId) {
        TableOperate.setSearchConfig(sortOptionId);

        switch (sortOptionId) {
            case R.id.sort_title: {
                noteAdapter.setComparator(Comparator.comparing(Note::getTitle));
                break;
            }

            case R.id.sort_created_time: {
                noteAdapter.setComparator(Comparator.comparing(Note::getStarttime));
                break;
            }

            case R.id.sort_modified_time: {
                noteAdapter.setComparator(Comparator.comparing(Note::getModifytime));
                break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.activity_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        this.configureSearchView(searchView);

        // launch from short cut
        if (getIntent().hasExtra("SEARCH")) {
            searchView.onActionViewExpanded();
        } else if (getIntent().hasExtra("NEW")) {
            Intent intent = new Intent(ListActivity.this, EditorActivity.class);
            this.startActivity(intent);
        }

        int sortOptionId;
        int selectedId = TableOperate.getSearchConfig();
        if (selectedId != -1)
            sortOptionId = selectedId;
        else sortOptionId = R.id.sort_title;

        SubMenu sortMenu = menu.findItem(R.id.menu_sort).getSubMenu();
        for (int i = 0; i < sortMenu.size(); i++) {
            MenuItem item = sortMenu.getItem(i);
            if (item.getItemId() == sortOptionId) {
                item.setChecked(true);
                break;
            }
        }

        setSortOption(sortOptionId);

//        SubMenu subMenu = menu.findItem(R.id.menu_sort).getSubMenu();
//        final String[] sortOptions = getResources().getStringArray(R.array.sort_options_human_readable);
//        for (int i = 0; i < sortOptions.length; i++) {
//            if (subMenu.findItem(i) == null) {
//                subMenu.add(MENU_SORT_GROUP_ID, i, i, sortOptions[i]);
//            }
//        }
//
//        subMenu.setGroupCheckable(MENU_SORT_GROUP_ID, true, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_note: {
                Intent intent = new Intent(ListActivity.this, EditorActivity.class);
                this.startActivity(intent);
                break;
            }

            case R.id.clear: {
                int size = noteAdapter.getAdapterItemCount();
                for (int i = 0; i < size; i++) {
                    Note note = noteAdapter.getItem(0);
                    noteAdapter.remove(0);
                    EventBus.getDefault().post(new NoteDeleteEvent(note));
                }
                break;
            }

            case R.id.sort_title: {
                item.setChecked(true);
                setSortOption(R.id.sort_title);
                break;
            }

            case R.id.sort_created_time: {
                item.setChecked(true);
                setSortOption(R.id.sort_created_time);
                break;
            }

            case R.id.sort_modified_time: {
                item.setChecked(true);
                setSortOption(R.id.sort_modified_time);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        this.setTitle(this.getResources().getString(R.string.list_title));

        this.layoutManager = new LinearLayoutManager(this);
        this.ultimateRecyclerView = this.findViewById(R.id.ultimate_recycler_view);
        this.ultimateRecyclerView.setLayoutManager(layoutManager);


        //magic do not touch
        List<Note> testNoteList = new ArrayList<>();
        testNoteList.add(new Note());
        this.noteAdapter = new NoteAdapter(testNoteList, this);
        EventBus.getDefault().register(noteAdapter);
        this.ultimateRecyclerView.setAdapter(noteAdapter);
        this.noteAdapter.notifyDataSetChanged();
        this.noteAdapter.clear();


        this.noteAdapter.updateList(TableOperate.getInstance().getAllNotes());

//        this.ultimateRecyclerView.reenableLoadmore();
//        this.noteAdapter.setCustomLoadMoreView(LayoutInflater.from(this).inflate(R.layout.custom_bottom_progressbar, null));
//        ultimateRecyclerView.setOnLoadMoreListener((itemsCount, maxLastVisiblePosition) -> {
//            Handler handler = new Handler();
//            handler.postDelayed(() -> {
//                noteList.add(new Note());
//                noteAdapter.notifyDataSetChanged();
//            }, 1000);
//        });

//        this.enableDrag();


        this.enableRefresh();

//        this.setSlidingMenu();

        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        }
    }

    /**
     * Configure search view to set hint & listener
     */

    void setSlidingMenu() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_behind_witdh);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.sliding_menu_list);
        slidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

    private void configureSearchView(@NonNull SearchView searchView) {
        searchView.setQueryHint("search by title...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter = new NoteAdapter(TableOperate.getInstance().getSearchResultFuzzy(query), ListActivity.this);
                ultimateRecyclerView.setAdapter(searchAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter = new NoteAdapter(TableOperate.getInstance().getSearchResultFuzzy(newText), ListActivity.this);
                ultimateRecyclerView.setAdapter(searchAdapter);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            ultimateRecyclerView.setAdapter(noteAdapter);
            return false;
        });
    }

    //refresh the list
    private void enableRefresh() {
        this.ultimateRecyclerView.setDefaultOnRefreshListener(() -> new Handler().postDelayed(() -> {
            noteAdapter.updateList(TableOperate.getInstance().getAllNotes());
            ListActivity.this.ultimateRecyclerView.setRefreshing(false);
            // ultimateRecyclerView.scrollBy(0, -50);
            layoutManager.scrollToPosition(0);
        }, 500));
    }

    // drag the view
    private void enableDrag() {
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(noteAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(ultimateRecyclerView.mRecyclerView);
        noteAdapter.setOnDragStartListener(new NoteAdapter.OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(noteAdapter);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ItemTouchHelper itemTouchHelper;

}
