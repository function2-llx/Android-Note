package com.se.npe.androidnote;

import android.os.Bundle;
import android.os.Trace;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;

import com.se.npe.androidnote.interfaces.IEditor;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.temperary.TestEditor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class EditorActivity extends AppCompatActivity {
    IEditor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        this.setTitle(this.getResources().getString(R.string.editor_title));
        EventBus.getDefault().register(this);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void getNoteFromOld(Note note) {
        if (note != null) {
            Toast.makeText(this, "get a note, title =  " + note.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "get a null note", Toast.LENGTH_SHORT).show();
        }
    }
}
