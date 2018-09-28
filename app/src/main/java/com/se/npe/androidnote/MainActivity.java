package com.se.npe.androidnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.se.npe.androidnote.models.Note;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListener();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // receive the note sent back from EditorActivity
    @Subscribe
    public void getNoteFromEditor(Note note) {
        if (note != null) {
            Toast.makeText(this, "get a note " + note.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "get a null note", Toast.LENGTH_SHORT).show();
        }
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
                Note note = generateNoteForTest();
                EventBus.getDefault().postSticky(note);
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    private Note generateNoteForTest() {
        return new Note("hello from MainActivity", null);
    }
}
