package com.se.npe.androidnote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.se.npe.androidnote.interfaces.INoteCollection;

// TODO show a list of the preview of note(data stored in noteCollection)
// TODO show a search input text(implement it using SearchView)

public class ListActivity extends AppCompatActivity {
    private INoteCollection noteCollection;
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.activity_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(this.getResources().getString(R.string.list_title));
        setContentView(R.layout.activity_list);
    }
}
