package com.se.npe.androidnote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.models.TextData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

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
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_new_note: {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new TableOperate(getApplicationContext());
        /*
        TableOperate newtable = new TableOperate(getApplicationContext());
        Log.d("debug0001","OK_setup");
        ArrayList<IData> templist = new ArrayList<IData>();
        TextData data1 = new TextData("test data");
        templist.add(data1);
        newtable.addNote(new Note("data1",templist));
        Log.d("debug0001","OK_insert");

        List<Note> allnotes = newtable.getAllNotes();

        Log.d("debug0001","All Notes in DB "+Integer.toString(allnotes.size()));
        */

        //Log.d("debug0001",newtable.getNoteAt(5).toString());

        //newtable.removeNoteAt(5);

        //newtable.setNoteAt(17,new Note("data2",templist));

        //List<Note> anslist = newtable.getSearchResult("data");

        //Log.d("debug0001",Integer.toString(anslist.size()));

        setContentView(R.layout.activity_main);
        initListener();
        EventBus.getDefault().register(this);
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        }
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
        Toast.makeText(this, "get a note from editor, title = " + note.getTitle(), Toast.LENGTH_SHORT).show();
        selectedNote = note;
    }

    private void initListener() {
        findViewById(R.id.launch_list_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.launch_editor_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.launch_editor_activity_with_old_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // in the real app, generateNoteForTest() will be replaced by the selected note in the list
                selectedNote = generateNoteForTest();
                EventBus.getDefault().postSticky(selectedNote);
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    private Note generateNoteForTest() {
        if (selectedNote == null)
            return new Note("hello from MainActivity", new ArrayList<>());
        return selectedNote;
    }
}
