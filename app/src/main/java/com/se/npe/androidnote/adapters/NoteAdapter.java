package com.se.npe.androidnote.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.ListActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.editor.PictureLoader;
import com.se.npe.androidnote.events.NoteSelectEvent;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.models.TableOperate;

import org.greenrobot.eventbus.EventBus;

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

    public NoteAdapter(ListActivity activity) {
        this.activity = activity;
        this.comparator = TableConfig.Sorter.SORTER_FIELD_TO_COMPARATOR.get(TableOperate.getSearchConfig());
    }

    /* ViewHolder */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        v.setOnClickListener(holder);
        v.setOnLongClickListener(holder);

        //把文字部分设置为屏幕宽度的2/3
        LinearLayout textLayout = v.findViewById(R.id.text_layout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textLayout.getLayoutParams();
        params.width = getScreenWidth() / 3 * 2;
        textLayout.setLayoutParams(params);
        return holder;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stick_header_item, parent, false);
        return new RecyclerView.ViewHolder(v) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.itemView.findViewById(R.id.stick_text);
        textView.setText(String.valueOf(generateHeaderId(position)));
        // viewHolder.itemView.setBackgroundColor(Color.parseColor("#AA70DB93"));
    }

    /* footer */

    @Override
    public ViewHolder newFooterHolder(View view) {
        return new ViewHolder(view);
    }

    /* Actions of Note in NoteCollection */

    // All notes
    public void updateAllNotesList() {
        updateList(TableOperate.getInstance().getAllNotes("",null));
    }

    // Search for notes
    //ultimate version
    public void updateSearchList(String param, String group, List<String> tags) {
        Toast.makeText(activity, param + group + tags.toString(), Toast.LENGTH_SHORT).show();
        updateList(TableOperate.getInstance().fuzzySearch(param, group, tags));
    }


    // Group notes
    public void updateGroupNotesList(String groupName) {
        this.updateList(TableOperate.getInstance().getAllNotes(groupName,null));
    }

    private void updateList(@NonNull List<Note> list) {
        noteList = list;
        noteList.sort(comparator);
        this.notifyDataSetChanged();
    }

    public void setSortField(String sortField) {
        TableOperate.setSearchConfig(sortField);
        this.comparator = TableConfig.Sorter.SORTER_FIELD_TO_COMPARATOR.get(sortField);
        noteList.sort(comparator);
        this.notifyDataSetChanged();
    }

    public void insert(Note note, int position) {
        TableOperate.getInstance().addNote(note);
        super.insertInternal(this.noteList, note, position);
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

    private int getScreenWidth() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
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

        private int getScreenWidth() {
            if (screenWidth != -1)
                return screenWidth;
            DisplayMetrics outMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            return screenWidth = outMetrics.widthPixels;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            // title & text
            this.title = itemView.findViewById(R.id.text_view_title);
            this.text = itemView.findViewById(R.id.text_view_text);

            //group the note belongs to
            this.group = itemView.findViewById(R.id.text_view_group);

            // create & modify time
            int textWidth = getScreenWidth() / 3 * 2;
            this.createTimeDisplayer = itemView.findViewById(R.id.text_view_create_time);
            this.createTimeDisplayer.setWidth(textWidth);
            this.modifyTimeDisplayer = itemView.findViewById(R.id.text_view_modify_time);
            this.modifyTimeDisplayer.setWidth(textWidth);
            // image
            this.imageView = itemView.findViewById(R.id.image_view);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setText(String text) {
            this.text.setText(text);
        }

        public void setGroup(String group) {
            if (group == null || group.isEmpty())
                this.group.setText("this note does not belong to any group");
            else
                this.group.setText("group: " + group);
        }

        public void setCreateDate(Date date) {
            this.createTimeDisplayer.setText(getResources().getString(R.string.note_create_time, getFormatDate(date)));
        }

        public void setModifyDate(Date date) {
            this.modifyTimeDisplayer.setText(getResources().getString(R.string.note_modify_time, getFormatDate(date)));
        }

        public void setImage(String imagePath) {
            if (imagePath.isEmpty())
                this.imageView.setImageBitmap(null);    //clear the previous image
            else
                new PictureLoader(imageView, getScreenWidth() / 3).execute(imagePath);
        }

        @Override
        public void onClick(@NonNull View v) {
            Note selectedNote = getItem(getAdapterPosition());
            EventBus.getDefault().postSticky(new NoteSelectEvent(selectedNote));
            Intent intent = new Intent(activity, EditorActivity.class);
            intent.putExtra(EditorActivity.CURRENT_GROUP, activity.getCurrentGroup());
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
                        EventBus.getDefault().postSticky(new NoteSelectEvent(selectedNote));
                        activity.startActivity(new Intent(activity, EditorActivity.class)
                                .putExtra(EditorActivity.VIEW_ONLY, true)
                                .putExtra(EditorActivity.CURRENT_GROUP, activity.getCurrentGroup()));
                        break;

                    case R.id.set_group:
                        List<String> groupName = TableOperate.getInstance().getAllGroup();

                        View dialogView = View.inflate(activity, R.layout.set_group_dialog, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("set group");
                        builder.setView(dialogView);
                        final int[] selected = {0};
                        builder.setPositiveButton("confirm", (dialog, which) -> {
                            selectedNote.setGroupName(groupName.get(selected[0]));
                            TableOperate.getInstance().modify(selectedNote);
                            activity.updateList();
                        });
                        builder.setNegativeButton("cancel", null);
                        builder.setSingleChoiceItems(groupName.toArray(new String[0]), selected[0],
                                (dialog, which) -> selected[0] = which);
                        builder.show();

                        break;

                    case R.id.remove_from_current_group:
                        selectedNote.setGroupName("");
                        TableOperate.getInstance().modify(selectedNote);
                        activity.updateList();
                        break;

                    default:
                        break;
                }
                return true;
            });
            menu.show();
            return true;
        }

        private String getFormatDate(Date date) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault());
            return format.format(date);
        }
    }
}
