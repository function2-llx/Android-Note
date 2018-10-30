package com.se.npe.androidnote.adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.ListActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.models.SingletonResetter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NoteAdapterTest {

    private NoteAdapter noteAdapter;

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(ListActivity.class);
        noteAdapter = new NoteAdapter(activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        UltimateRecyclerView ultimateRecyclerView = activity.findViewById(R.id.ultimate_recycler_view);
        ultimateRecyclerView.setLayoutManager(layoutManager);
        ultimateRecyclerView.setAdapter(noteAdapter);
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void setComparator() {
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
    public void updateAllNotesList() {

    }

    @Test
    public void updateSearchList() {
    }

    @Test
    public void updateList() {

    }

    @Test
    public void insert() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void clear() {
    }
}