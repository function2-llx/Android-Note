package com.se.npe.androidnote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import com.marshalchen.ultimaterecyclerview.DragDropTouchListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.adapters.NoteAdapter;
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
        }
        return super.onOptionsItemSelected(item);
    }

    DragDropTouchListener dragDropTouchListener;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        this.setTitle(this.getResources().getString(R.string.list_title));

        this.noteCollection = new INoteCollection() {
            @Override
            public List<Note> getAllNotes() {
                int size = 100;
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
        this.noteAdapter = new NoteAdapter(this.noteCollection.getAllNotes());
        this.layoutManager = new LinearLayoutManager(this);
        this.ultimateRecyclerView = this.findViewById(R.id.ultimate_recycler_view);
        this.ultimateRecyclerView.setLayoutManager(layoutManager);
        this.ultimateRecyclerView.setAdapter(noteAdapter);
        this.ultimateRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        simpleRecyclerViewAdapter.insert("Refresh things", 0);
                        ListActivity.this.noteAdapter.insert(new Note(), 0);
                        ListActivity.this.ultimateRecyclerView.setRefreshing(false);
                        //   ultimateRecyclerView.scrollBy(0, -50);
                        layoutManager.scrollToPosition(0);
                    }
                }, 1000);
            }
        });
    }

}
