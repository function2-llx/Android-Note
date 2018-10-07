package com.se.npe.androidnote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.marshalchen.ultimaterecyclerview.DragDropTouchListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.itemTouchHelper.SimpleItemTouchHelperCallback;
import com.marshalchen.ultimaterecyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.se.npe.androidnote.adapters.NoteAdapter;
import com.se.npe.androidnote.editor.ImageLoader;
import com.se.npe.androidnote.interfaces.INoteCollection;
import com.se.npe.androidnote.models.Note;

import java.util.ArrayList;

// TODO show a list of the preview of note(data stored in noteCollection)
// TODO show a search input text(implement it using SearchView)

public class ListActivity extends AppCompatActivity {
    private INoteCollection noteCollection;
    private  LinearLayoutManager layoutManager;
    private NoteAdapter noteAdapter;
    private UltimateRecyclerView ultimateRecyclerView;
    private DragDropTouchListener dragDropTouchListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.activity_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_new_note: {
                Intent intent = new Intent(ListActivity.this, EditorActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.sort_title: {
                Collections.sort(this.noteList, new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
                this.noteAdapter.notifyDataSetChanged();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private List<Note> noteList;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        this.setTitle(this.getResources().getString(R.string.list_title));


        this.noteCollection = new INoteCollection() {
            @Override
            public List<Note> getAllNotes() {
                int size = 10;
                List<Note> ret = new ArrayList<>();
                for (int i = 0; i < size; i++) ret.add(new Note());
                return ret;
            }

            @Override
            public List<Note> getSearchResult(String parameter) {
                return null;
            }

            @Override
            public void addNote(Note note) {

            }

            @Override
            public Note getNoteAt(int index) {
                return null;
            }

            @Override
            public void setNoteAt(int index, Note Note) {

            }

            @Override
            public void removeNoteAt(int index) {

            }

            @Override
            public void loadFromFile(String fileName) {

            }

            @Override
            public void saveToFile(String fileName) {

            }
        };
        this.noteList = this.noteCollection.getAllNotes();
        this.noteAdapter = new NoteAdapter(noteList);
        this.layoutManager = new LinearLayoutManager(this);
        this.ultimateRecyclerView = this.findViewById(R.id.ultimate_recycler_view);
        this.ultimateRecyclerView.setLayoutManager(layoutManager);
        this.ultimateRecyclerView.setAdapter(noteAdapter);
//        this.ultimateRecyclerView.reenableLoadmore();
//        this.noteAdapter.setCustomLoadMoreView(LayoutInflater.from(this).inflate(R.layout.custom_bottom_progressbar, null));
//        ultimateRecyclerView.setOnLoadMoreListener((itemsCount, maxLastVisiblePosition) -> {
//            Handler handler = new Handler();
//            handler.postDelayed(() -> {
//                noteList.add(new Note());
//                noteAdapter.notifyDataSetChanged();
//            }, 1000);
//        });

        this.enableDrag();
        this.enableRefresh();
    }

    private void enableRefresh()
    {
        this.ultimateRecyclerView.setDefaultOnRefreshListener(() -> new Handler().postDelayed(() -> {
//                        simpleRecyclerViewAdapter.insert("Refresh things", 0);
            ListActivity.this.noteAdapter.insert(new Note(), 0);
            ListActivity.this.ultimateRecyclerView.setRefreshing(false);
            //   ultimateRecyclerView.scrollBy(0, -50);
            layoutManager.scrollToPosition(0);
        }, 1000));
    }

    private void enableDrag()
    {
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

    private  ItemTouchHelper itemTouchHelper;

}
