package com.se.npe.androidnote;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.adapters.Adapter;
import com.se.npe.androidnote.interfaces.INoteCollection;

import java.util.ArrayList;

// TODO show a list of the preview of note(data stored in noteCollection)
// TODO show a search input text(implement it using SearchView)

public class ListActivity extends AppCompatActivity {
    private List<String> stringList;
    private INoteCollection noteCollection;
    private  LinearLayoutManager layoutManager;
    private Adapter adapter;
    private UltimateRecyclerView ultimateRecyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.activity_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        this.setTitle(this.getResources().getString(R.string.list_title));
        int size = 100;
        this.stringList = new ArrayList<>();
        for (int i = 0; i < size; i++) this.stringList.add(String.valueOf(i));
        this.adapter = new Adapter(stringList);
        this.layoutManager = new LinearLayoutManager(this);
        this.ultimateRecyclerView = this.findViewById(R.id.ultimate_recycler_view);
        this.ultimateRecyclerView.setLayoutManager(layoutManager);
        this.ultimateRecyclerView.setAdapter(adapter);
    }
}
