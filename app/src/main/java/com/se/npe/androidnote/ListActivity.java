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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.marshalchen.ultimaterecyclerview.DragDropTouchListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.itemTouchHelper.SimpleItemTouchHelperCallback;
import com.se.npe.androidnote.adapters.NoteAdapter;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableOperate;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
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

    /* Options menu */

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
                    noteAdapter.remove(0);
                }
                break;
            }

            case R.id.sort_title: {
                this.noteAdapter.sortByTitle();
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

        this.enableDrag();
        this.enableRefresh();

        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        }
    }

    /**
     * Configure search view to set hint & listener
     */
    private void configureSearchView(@NonNull SearchView searchView) {
        searchView.setQueryHint("search by title...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter = new NoteAdapter(noteAdapter.getSearchResult(query), ListActivity.this);
                ultimateRecyclerView.setAdapter(searchAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter = new NoteAdapter(noteAdapter.getSearchResult(newText), ListActivity.this);
                ultimateRecyclerView.setAdapter(searchAdapter);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ultimateRecyclerView.setAdapter(noteAdapter);
                return false;
            }
        });
    }

    private void enableRefresh() {
        this.ultimateRecyclerView.setDefaultOnRefreshListener(() -> new Handler().postDelayed(() -> {
            // simpleRecyclerViewAdapter.insert("Refresh things", 0);
//            ListActivity.this.noteAdapter.insert(new Note(), 0);
            noteAdapter.updateList(TableOperate.getInstance().getAllNotes());
            ListActivity.this.ultimateRecyclerView.setRefreshing(false);
            // ultimateRecyclerView.scrollBy(0, -50);
            layoutManager.scrollToPosition(0);
        }, 500));
    }

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
