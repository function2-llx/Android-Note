package com.se.npe.androidnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.se.npe.androidnote.models.Note;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {
    private Note selectedNote;
    private ListView noteList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_list: {
                System.err.println("test");
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // receive the note sent back from EditorActivity
    @Subscribe
    public void getNoteFromEditor(Note note) {
        if (note == selectedNote) { // the note sent by self
            return;
        }
        if (note != null) {
            Toast.makeText(this, "get a note from editor, title = " + note.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "get a null note", Toast.LENGTH_SHORT).show();
        }
    }

    private void initListener() {

    }

    private Note generateNoteForTest() {
        return new Note("hello from MainActivity", null);
    }
}
