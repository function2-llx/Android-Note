package com.se.npe.androidnote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.se.npe.androidnote.adapters.NoteAdapter;
import com.se.npe.androidnote.interfaces.INoteFileConverter;
import com.se.npe.androidnote.models.FileOperate;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.NotePdfConverter;
import com.se.npe.androidnote.models.NoteZipConverter;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.models.TagGroupManager;

import java.util.List;
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
    private Toolbar toolbar;
    private SubMenu groupMenu;
    private String currentGroup = "";
    private TagGroupManager tagGroupManager;

    public String getCurrentGroup() {
        return currentGroup;
    }

    /* Options menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // search
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

        // sort
        SubMenu sortMenu = menu.findItem(R.id.menu_sort).getSubMenu();

        String sortField = TableOperate.getSearchConfig();
        for (int i = 0; i < sortMenu.size(); i++) {
            MenuItem item = sortMenu.getItem(i);
            if (TableConfig.Sorter.SORTER_OPTION_TO_FIELD.get(item.getItemId()).equals(sortField))
                item.setChecked(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    //Can only use lower 16 bits for requestCode
    private static final int REQUEST_FILE_CHOOSE = 23333;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_FILE_CHOOSE && resultCode == RESULT_OK) {
            assert data != null;
            final Uri uri = data.getData();
            String path = FileUtils.getPath(this, uri);
            INoteFileConverter noteFileConverter;
            assert path != null;
            switch (FileOperate.getSuffix(path)) {
                case "note":
                    noteFileConverter = new NoteZipConverter();
                    break;
                case "pdf":
                    noteFileConverter = new NotePdfConverter();
                    break;
                default:
                    noteFileConverter = new NoteZipConverter();
                    break;
            }
            noteFileConverter.importNoteFromFile((Note note) ->
                            TableOperate.getInstance().addNote(note)
                    , path);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_note:
                startActivity(new Intent(ListActivity.this, EditorActivity.class)
                        .putExtra(EditorActivity.CURRENT_GROUP, currentGroup));
                break;

            case R.id.menu_open:
                startActivityForResult(
                        Intent.createChooser(FileUtils.createGetContentIntent(), "Select a file")
                        , REQUEST_FILE_CHOOSE);
                break;

            case R.id.clear:
                noteAdapter.clear();
                break;

            case R.id.sort_title:
                item.setChecked(true);
                noteAdapter.setSortField(TableConfig.Sorter.SORTER_OPTION_TO_FIELD.get(R.id.sort_title));
                break;

            case R.id.sort_created_time:
                item.setChecked(true);
                noteAdapter.setSortField(TableConfig.Sorter.SORTER_OPTION_TO_FIELD.get(R.id.sort_created_time));
                break;

            case R.id.sort_modified_time:
                item.setChecked(true);
                noteAdapter.setSortField(TableConfig.Sorter.SORTER_OPTION_TO_FIELD.get(R.id.sort_modified_time));
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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

        //new note button
        findViewById(R.id.new_note_button).setOnClickListener(v -> startActivity(new Intent(ListActivity.this, EditorActivity.class)
                .putExtra(EditorActivity.CURRENT_GROUP, currentGroup)));

        setNavigationView();
        initDrawerToggle();
    }

    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }

    private DrawerLayout drawerLayout;

    private void initDrawerToggle() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerToggle.syncState();
    }

    private void refreshGroups() {
        groupMenu.removeGroup(R.id.group_groups);
        List<String> allGroups = TableOperate.getInstance().getAllGroup();

        for (String groupName : allGroups)
            groupMenu.add(R.id.group_groups, Menu.NONE, Menu.NONE, groupName);
    }

    private void handleGroupManage(@NonNull MenuItem menuItem) {
        List<String> allGroups = TableOperate.getInstance().getAllGroup();
        String[] allGroupsArray = allGroups.toArray(new String[0]);
        switch (menuItem.getItemId()) {
            case R.id.new_group: {
                EditText editText = new EditText(ListActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                builder.setTitle("New group");
                builder.setPositiveButton("add", null);
                builder.setNegativeButton("cancel", null);
                builder.setView(editText);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String groupName = editText.getText().toString();
                    if (groupName.isEmpty())
                        Toast.makeText(ListActivity.this, "input something?", Toast.LENGTH_SHORT).show();
                    else if (allGroups.contains(groupName))
                        Toast.makeText(ListActivity.this, groupName + " already exist", Toast.LENGTH_SHORT).show();
                    else {
                        TableOperate.getInstance().addGroup(groupName);
                        refreshGroups();
                        dialog.cancel();
                    }
                });
                break;
            }

            case R.id.manage_group: {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                builder.setTitle(getString(R.string.manage_groups));
                boolean selected[] = new boolean[allGroupsArray.length];
                builder.setMultiChoiceItems(allGroupsArray, new boolean[allGroupsArray.length], (dialog, which, isChecked) -> selected[which] = isChecked);
                builder.setNegativeButton("cancel", null);
                builder.setPositiveButton("confirm", (dialog, which) -> {
                    for (int i = 0; i < allGroupsArray.length; i++)
                        if (selected[i])
                            TableOperate.getInstance().removeGroup(allGroupsArray[i]);
                    refreshGroups();
                });
                builder.show();
                break;
            }

            default:
                break;
        }
    }

    public void setNavigationView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        this.groupMenu = menu.findItem(R.id.groups).getSubMenu();
        navigationView.setItemIconTintList(null);

        refreshGroups();

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getGroupId()) {
                case R.id.group_all_notes: {
                    noteAdapter.updateAllNotesList();
                    currentGroup = "";
                    setTitle(getString(R.string.list_title));
                    drawerLayout.closeDrawers();
                    break;
                }

                case R.id.group_groups: {
                    String groupName = menuItem.getTitle().toString();
                    noteAdapter.updateGroupNotesList(groupName);
                    currentGroup = groupName;
                    setTitle(groupName);
                    drawerLayout.closeDrawers();
                    break;
                }

                case R.id.group_operations: {
                    handleGroupManage(menuItem);

                    break;
                }
                default:
                    break;
            }

            return true;
        });
    }


    /**
     * Configure search view to set hint & listener
     */

    private void showList() {
        ultimateRecyclerView.setVisibility(View.VISIBLE);
        updateList();
    }

    private void hideList() {
        ultimateRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void configureSearchView(@NonNull SearchView searchView) {
        tagGroupManager = findViewById(R.id.tag_group_manager);

        searchView.setQueryHint("search for your note");

        searchView.setSubmitButtonEnabled(true);
        ImageView goButton = searchView.findViewById(R.id.search_go_btn);
        goButton.setOnClickListener(v -> {
            tagGroupManager.hide();
            showList();
            String query = searchView.getQuery().toString();
            List<String> tags = tagGroupManager.getCheckedTags();
            noteAdapter.updateSearchList(query, currentGroup, tags);
        });

        // open event
        searchView.setOnSearchClickListener(v -> {
            tagGroupManager.updateTags();
            tagGroupManager.show();
            hideList();
            disableRefresh();
        });

        // close event
        searchView.setOnCloseListener(() -> {
            tagGroupManager.hide();
            showList();
            enableRefresh();
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                goButton.callOnClick();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    hideList();
                    tagGroupManager.show();
                } else
                    goButton.callOnClick();
                return true;
            }
        });


    }

    public void updateList() {
        if (currentGroup.isEmpty())
            noteAdapter.updateAllNotesList();
        else
            noteAdapter.updateGroupNotesList(currentGroup);
    }

    // refresh the list
    private void enableRefresh() {
        this.ultimateRecyclerView.setDefaultOnRefreshListener(() -> new Handler().postDelayed(() -> {
            updateList();
            ListActivity.this.ultimateRecyclerView.setRefreshing(false);
            layoutManager.scrollToPosition(0);
        }, 500));
    }

    private void disableRefresh() {
        ultimateRecyclerView.mSwipeRefreshLayout.setEnabled(false);
    }
}
