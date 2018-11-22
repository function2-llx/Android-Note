package com.se.npe.androidnote.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.ListActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.editor.PictureLoader;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.models.TableOperate;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapt NoteCollection to list-like View
 *
 * @author llx
 */
public class NoteAdapter extends UltimateViewAdapter<NoteAdapter.ViewHolder> {
    private ListActivity activity;
    private List<Note> noteList;
    private Comparator<Note> comparator;
    private String currentGroup;

    public NoteAdapter(ListActivity activity) {
        this.activity = activity;
        this.comparator = TableConfig.Sorter.getSorterFieldToComparator(TableOperate.getSearchConfig());
        this.currentGroup = "";
    }

    /* ViewHolder */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = getItem(position);
        Note.PreviewData data = note.getPreview();
        holder.setTitle(data.title);
        holder.setText(data.text);
        holder.setCreateDate(data.startTime);
        holder.setGroup(data.groupName);
        holder.setModifyDate(data.modifyTime);
        holder.setImage(data.picturePath);
    }

    @Override
    public int getAdapterItemCount() {
        if (noteList == null)
            return 0;
        return this.noteList.size();
    }

    /* header */

    @Override
    public long generateHeaderId(int position) {
        Note note = getItem(position);
        if (note == null)
            return -1;

        return note.getPreview().title.charAt(0);
    }

    @Override
    public ViewHolder newHeaderHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
        return new RecyclerView.ViewHolder(v) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.itemView.findViewById(R.id.stick_text);
        textView.setText(String.valueOf(generateHeaderId(position)));
    }

    /* footer */

    @Override
    public ViewHolder newFooterHolder(View view) {
        return new ViewHolder(view);
    }

    /* Actions of Note in NoteCollection */

    // Search for notes
    public void updateSearchList(String param, List<String> tags) {
        updateList(TableOperate.getInstance().fuzzySearch(param, currentGroup, tags));
    }

    // Group notes
    public void updateGroupNotesList() {
        this.updateList(TableOperate.getInstance().getAllNotes(currentGroup, null));
    }

    private void updateList(@NonNull List<Note> list) {
        noteList = list;
        noteList.sort(comparator);
        this.notifyDataSetChanged();
    }

    public void setSortField(String sortField) {
        TableOperate.setSearchConfig(sortField);
        this.comparator = TableConfig.Sorter.getSorterFieldToComparator(sortField);
        // not calling updateGroupNoteList() to speed up
        noteList.sort(comparator);
        this.notifyDataSetChanged();
    }

    public String getCurrentGroup() {
        return this.currentGroup;
    }

    public void setCurrentGroup(String groupName) {
        this.currentGroup = groupName;
        updateGroupNotesList();
    }

    public void insert(Note note) {
        int position = 0;
        for (Note noteInList : noteList) {
            if (comparator.compare(noteInList, note) > 0)
                break;
            ++position;
        }
        insert(note, position);
    }

    void insert(Note note, int position) {
        TableOperate.getInstance().addNote(note);
        super.insertInternal(this.noteList, note, position);
    }

    public void modify(Note note) {
        // Can we remove & insert to do the same thing?
        TableOperate.getInstance().setNote(note);
        updateGroupNotesList();
    }

    public void remove(int position) {
        TableOperate.getInstance().removeNote(getItem(position));
        super.removeInternal(this.noteList, position);
    }

    public void clear() {
        TableOperate.getInstance().removeAllNotes();
        super.clearInternal(this.noteList);
    }

    Note getItem(int position) {
        // Subtract the first one used by header
        if (this.hasHeaderView())
            position--;
        return this.noteList.get(position);
    }

    List<Note> getItems() {
        return this.noteList;
    }

    public Comparator<Note> getComparator() {
        return this.comparator;
    }

    /**
     * Adapt Note to item-like View
     *
     * @author llx
     */
    public class ViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView title;
        private TextView text;
        private TextView group;

        private TextView createTimeDisplayer;
        private TextView modifyTimeDisplayer;

        private ImageView imageView;

        int screenWidth = -1;
        Bitmap emptyImage = null;

        private int getScreenWidth() {
            if (screenWidth != -1)
                return screenWidth;
            DisplayMetrics outMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            screenWidth = outMetrics.widthPixels;
            return screenWidth;
        }

        private Bitmap getEmptyImage() {
            if (emptyImage != null)
                return emptyImage;
            Bitmap old = BitmapFactory.decodeResource(getResources(), R.drawable.no_image_available);
            int width = old.getWidth();
            int height = old.getHeight();
            float scale = ((float) getScreenWidth() / 3) / width;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            emptyImage = Bitmap.createBitmap(old, 0, 0, width, height, matrix, true);
            return emptyImage;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            int textWidth = getScreenWidth() / 3 * 2;
            // title & text
            this.title = itemView.findViewById(R.id.text_view_title);
            this.title.setWidth(textWidth);
            this.text = itemView.findViewById(R.id.text_view_text);
            this.text.setWidth(textWidth);
            // group the note belongs to
            this.group = itemView.findViewById(R.id.text_view_group);
            this.group.setWidth(textWidth);
            // create & modify time
            this.createTimeDisplayer = itemView.findViewById(R.id.text_view_create_time);
            this.createTimeDisplayer.setWidth(textWidth);
            this.modifyTimeDisplayer = itemView.findViewById(R.id.text_view_modify_time);
            this.modifyTimeDisplayer.setWidth(textWidth);
            // image
            this.imageView = itemView.findViewById(R.id.image_view);

            // click listener
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setTitle(@NonNull String title) {
            if (title.isEmpty())
                this.title.setText(getResources().getString(R.string.note_preview_title));
            else
                this.title.setText(title);
        }

        public void setText(@NonNull String text) {
            if (text.isEmpty())
                this.text.setText(getResources().getString(R.string.note_preview_text));
            else
                this.text.setText(text);
        }

        public void setGroup(@NonNull String group) {
            if (group.isEmpty()) {
                this.group.setVisibility(View.GONE);
            } else {
                this.group.setText(getResources().getString(R.string.note_preview_group, group));
                this.group.setVisibility(View.VISIBLE);
            }
        }

        public void setCreateDate(@NonNull Date date) {
            this.createTimeDisplayer.setText(getResources().getString(R.string.note_preview_create_time, getFormatDate(date)));
        }

        public void setModifyDate(@NonNull Date date) {
            this.modifyTimeDisplayer.setText(getResources().getString(R.string.note_preview_modify_time, getFormatDate(date)));
        }

        public void setImage(@NonNull String imagePath) {
            if (imagePath.isEmpty())
                this.imageView.setImageBitmap(this.getEmptyImage());    //clear the previous image
            else
                new PictureLoader(imageView, getScreenWidth() / 3).execute(imagePath);
        }

        @Override
        public void onClick(@NonNull View v) {
            Note selectedNote = getItem(getAdapterPosition());
            Intent intent = new Intent(activity, EditorActivity.class);
            intent.putExtra(EditorActivity.CURRENT_GROUP, currentGroup);
            intent.putExtra(EditorActivity.INITIAL_NOTE, selectedNote);
            activity.startActivity(intent);
        }

        @Override
        public boolean onLongClick(@NonNull View v) {
            PopupMenu menu = new PopupMenu(activity, v);
            menu.getMenuInflater().inflate(R.menu.list_item_options, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                Note selectedNote = getItem(getAdapterPosition());
                switch (item.getItemId()) {
                    case R.id.delete:
                        remove(getAdapterPosition());
                        break;

                    case R.id.preview:
                        activity.startActivity(new Intent(activity, EditorActivity.class)
                                .putExtra(EditorActivity.VIEW_ONLY, true)
                                .putExtra(EditorActivity.CURRENT_GROUP, currentGroup)
                                .putExtra(EditorActivity.INITIAL_NOTE, selectedNote));
                        break;

                    case R.id.set_group:
                        List<String> groupName = TableOperate.getInstance().getAllGroups();
                        if (groupName.isEmpty()) {
                            Toast.makeText(activity, "no group available", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("set group");
                        final int[] selected = {0};
                        builder.setPositiveButton("confirm", (dialog, which) -> {
                            selectedNote.setGroupName(groupName.get(selected[0]));
                            TableOperate.getInstance().modifyNote(selectedNote);
                            updateGroupNotesList();
                        });
                        builder.setNegativeButton("cancel", null);
                        builder.setSingleChoiceItems(groupName.toArray(new String[0]), selected[0],
                                (dialog, which) -> selected[0] = which);
                        builder.show();

                        break;

                    case R.id.remove_from_current_group:
                        selectedNote.setGroupName("");
                        TableOperate.getInstance().modifyNote(selectedNote);
                        updateGroupNotesList();
                        break;

                    default:
                        break;
                }
                return true;
            });
            menu.show();
            return true;
        }

        private String getFormatDate(@NonNull Date date) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return format.format(date);
        }
    }
}
