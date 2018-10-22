package com.se.npe.androidnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.se.npe.androidnote.models.Note;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Note selectedNote;

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
        super.onCreate(savedInstanceState);
        TableOperate.init(this.getApplicationContext());


        /*
        TableOperate newtable = TableOperate.getInstance();
        newtable.clearTable();
        Note tempnote = new Note("titlex",new ArrayList<>());
        tempnote.setStarttime(new Date(0));
        tempnote.setModifytime(new Date());
        Log.d("debug0001",tempnote.getModifytime().toString());
        newtable.addNote(tempnote);
        Log.d("debug0001",Integer.toString(newtable.getSearchResultFuzzy("title").size()));
        Log.d("debug0001",newtable.getSearchResult("titlex").get(0).getModifytime().toString());
        Log.d("debug0001",new Date().toString());
        */
        /*
        Note tempnote = new Note();
        Log.d("debug0001",tempnote.getStarttime().toString());
        Log.d("debug0001",tempnote.getModifytime().toString());
        tempnote.setStarttime(new Date());
        tempnote.setModifytime(new Date(1));
        Log.d("debug0001",tempnote.getStarttime().toString());
        Log.d("debug0001",tempnote.getModifytime().toString());
        */

        setContentView(R.layout.activity_main);
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }
}
