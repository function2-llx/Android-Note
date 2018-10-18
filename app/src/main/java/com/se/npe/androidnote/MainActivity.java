package com.se.npe.androidnote;

import android.Manifest;
import android.arch.core.executor.DefaultTaskExecutor;
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
import android.widget.ListView;
import android.widget.Toast;

import com.se.npe.androidnote.events.NoteEvent;
import com.se.npe.androidnote.interfaces.IData;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.SoundData;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.models.TextData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Note selectedNote;
    private ListView noteList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        new Thread(() -> SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5bbc8c0f")).start();
        super.onCreate(savedInstanceState);
        TableOperate.init(this.getApplicationContext());

        /*
        TableOperate newtable = TableOperate.getInstance();
        newtable.removeAllNotes();
        Note tempnote = new Note("data2",new ArrayList<IData>());
        Log.d("debug0001","success1");
        //tempnote.getTag().add("shit");
        Log.d("debug0001","success2");

        newtable.addNote(tempnote);
        List<Note> templist = newtable.getSearchResult("data2");
        Log.d("debug0001",Integer.toString(templist.size()));
        Log.d("debug0001",Integer.toString(templist.get(0).getTag().size()));
        */
        
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
