package com.se.npe.androidnote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.adapters.NoteAdapter;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableOperate;
import com.yydcdut.markdown.MarkdownConfiguration;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.syntax.text.TextFactory;
import com.yydcdut.markdown.theme.ThemeSunburst;

import java.util.Comparator;
import java.util.Objects;

/**
 * show a list of the preview of note(data stored in noteCollection)
 * show a search input text(implement it using SearchView)
 *
 * @author llx
 */
public class ListActivity extends AppCompatActivity {
    private LinearLayoutManager layoutManager;
    private NoteAdapter noteAdapter;
    private UltimateRecyclerView ultimateRecyclerView;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    /* Options menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.activity_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        this.configureSearchView(searchView);

        // launch from short cut
        if (getIntent().hasExtra("SEARCH")) {
            searchView.onActionViewExpanded();
        } else if (getIntent().hasExtra("NEW")) {
            Intent intent = new Intent(ListActivity.this, EditorActivity.class);
            this.startActivity(intent);
        }

        SubMenu sortMenu = menu.findItem(R.id.menu_sort).getSubMenu();
        boolean newSortOption = true;
        int sortOptionId = TableOperate.getSearchConfig();
        if (sortOptionId != -1) { // used sort option
            for (int i = 0; i < sortMenu.size(); i++) {
                MenuItem item = sortMenu.getItem(i);
                if (item.getItemId() == sortOptionId) {
                    newSortOption = false;
                    item.setChecked(true);
                    break;
                }
            }
        }
        if (newSortOption) { // initialize sort option
            setSortOption(R.id.sort_title);
            for (int i = 0; i < sortMenu.size(); i++) {
                MenuItem item = sortMenu.getItem(i);
                if (item.getItemId() == R.id.sort_title) {
                    item.setChecked(true);
                    break;
                }
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_note: {
                Intent intent = new Intent(ListActivity.this, EditorActivity.class);
                this.startActivity(intent);
                break;
            }

            case R.id.clear: {
                noteAdapter.clear();
                break;
            }

            case R.id.sort_title: {
                item.setChecked(true);
                setSortOption(R.id.sort_title);
                break;
            }

            case R.id.sort_created_time: {
                item.setChecked(true);
                setSortOption(R.id.sort_created_time);
                break;
            }

            case R.id.sort_modified_time: {
                item.setChecked(true);
                setSortOption(R.id.sort_modified_time);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSortOption(int sortOptionId) {
        TableOperate.setSearchConfig(sortOptionId);

        switch (sortOptionId) {
            case R.id.sort_title: {
                noteAdapter.setComparator(Comparator.comparing(Note::getTitle));
                break;
            }

            case R.id.sort_created_time: {
                noteAdapter.setComparator(Comparator.comparing(Note::getStartTime));
                break;
            }

            case R.id.sort_modified_time: {
                noteAdapter.setComparator(Comparator.comparing(Note::getModifyTime));
                break;
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);
//        this.setTitle(this.getResources().getString(R.string.list_title));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.layoutManager = new LinearLayoutManager(this);
        this.ultimateRecyclerView = this.findViewById(R.id.ultimate_recycler_view);
        this.ultimateRecyclerView.setLayoutManager(layoutManager);

        this.noteAdapter = new NoteAdapter(this);
        this.ultimateRecyclerView.setAdapter(noteAdapter);
        this.noteAdapter.updateAllNotesList();



        this.enableRefresh();
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        }

        initNavigationView();
        initDrawerToggle();
//        MobSDK.init(this);
        //        this.ultimateRecyclerView.reenableLoadmore();
//        this.noteAdapter.setCustomLoadMoreView(LayoutInflater.from(this).inflate(R.layout.custom_bottom_progressbar, null));
//        ultimateRecyclerView.setOnLoadMoreListener((itemsCount, maxLastVisiblePosition) -> {
//            Handler handler = new Handler();
//            handler.postDelayed(() -> {
//                noteList.add(new Note());
//                noteAdapter.notifyDataSetChanged();
//            }, 1000);
//        });

//        this.enableDrag();

    }

    @Override
    protected void onResume() {
        noteAdapter.updateAllNotesList();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initDrawerToggle() {

        this.drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerToggle.syncState();

    }

    void initNavigationView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        SubMenu groupMenu = menu.findItem(R.id.groups).getSubMenu();
        navigationView.setItemIconTintList(null);
        for (String groupName: TableOperate.getInstance().getAllGroup())
            groupMenu.add(groupName);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                drawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.all_notes) {
                    noteAdapter.updateAllNotesList();
                    setTitle(getString(R.string.list_title));
                } else {
                    String groupName = menuItem.getTitle().toString();
                    noteAdapter.updateGroupNotesList(groupName);
                    setTitle(groupName);
                }
                return true;
            }
        });
    }

    /**
     * Configure search view to set hint & listener
     */

//    void setSlidingMenu() {
//        slidingMenu = new SlidingMenu(this);
//        slidingMenu.setMode(SlidingMenu.LEFT);
//        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_behind_witdh);
//        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        slidingMenu.setMenu(R.layout.sliding_menu_list);
//        slidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
//    }
    private void configureSearchView(@NonNull SearchView searchView) {
        searchView.setQueryHint("search by title...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noteAdapter.updateSearchList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.updateSearchList(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            ultimateRecyclerView.setAdapter(noteAdapter);
            return false;
        });
    }

    // refresh the list
    private void enableRefresh() {
        this.ultimateRecyclerView.setDefaultOnRefreshListener(() -> new Handler().postDelayed(() -> {
            noteAdapter.updateAllNotesList();
            ListActivity.this.ultimateRecyclerView.setRefreshing(false);
            layoutManager.scrollToPosition(0);
        }, 500));
    }
}
