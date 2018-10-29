package com.se.npe.androidnote.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.editor.PictureLoader;
import com.se.npe.androidnote.events.NoteDeleteEvent;
import com.se.npe.androidnote.events.NoteSelectEvent;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.events.DatabaseModifyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Adapt NoteCollection to list-like View
 *
 * @author llx
 */
public class NoteAdapter extends UltimateViewAdapter<NoteAdapter.ViewHolder> {
    private AppCompatActivity activity;
    private Comparator<Note> comparator = Comparator.comparing(Note::getTitle);

    public List<Note> getSearchResult(String parameter) {
        List<Note> ret = new ArrayList<>();
        for (Note note : this.noteList) {
            if (note.getTitle().contains(parameter))
                ret.add(note);
        }
        return ret;
    }

    public void setComparator(Comparator<Note> comparator) {
        this.comparator = comparator;
        Collections.sort(noteList, comparator);
        this.notifyDataSetChanged();
    }

    public void updateList(List<Note> list) {
        if (list.isEmpty())
            this.clear();
        else {
            noteList.clear();
            noteList.addAll(list);
            Collections.sort(noteList, comparator);
            this.notifyDataSetChanged();
        }
    }

    /**
     * Adapt Note to item-like View
     *
     * @author llx
     */
    public class ViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView title, text;
        private TextView createTimeDisplayer, modifyTimeDisplayer;
        private ImageView imageView;
        int screenWidth = -1;

        private int getScreenWidth()
        {
            if (screenWidth != -1)
                return screenWidth;
            DisplayMetrics outMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            return screenWidth = outMetrics.widthPixels;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            this.title = itemView.findViewById(R.id.text_view_title);
            this.text = itemView.findViewById(R.id.text_view_text);
            this.createTimeDisplayer = itemView.findViewById(R.id.text_view_create_time);

            int textWidth = getScreenWidth() / 3 * 2;
            this.createTimeDisplayer.setWidth(textWidth);
            this.modifyTimeDisplayer = itemView.findViewById(R.id.text_view_modify_time);
            this.modifyTimeDisplayer.setWidth(textWidth);

            this.imageView = itemView.findViewById(R.id.image_view);
            LinearLayout layout = itemView.findViewById(R.id.text_layout);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setText(String text) {
            this.text.setText(text);
        }

        private String getFormatDate(Date date) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            return format.format(date);
        }

        public void setCreateDate(Date date)
        {
            this.createTimeDisplayer.setText("create: " + getFormatDate(date));
        }

        public void setModifyDate(Date date)
        {
            this.modifyTimeDisplayer.setText("modify: " + getFormatDate(date));
        }

        public void setIamge(String imagePath)
        {
            if (imagePath.isEmpty())
                this.imageView.setImageBitmap(null);    //clear the previous image
            else {
                DisplayMetrics outMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
                new PictureLoader(imageView, outMetrics.widthPixels / 3).execute(imagePath);
            }
        }

        @Override
        public void onClick(@NonNull View v) {
            Note selectedNote = getItem(getAdapterPosition());
            EventBus.getDefault().postSticky(new NoteSelectEvent(selectedNote));
            Intent intent = new Intent(activity, EditorActivity.class);
            activity.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            PopupMenu menu = new PopupMenu(activity, v);
            menu.getMenuInflater().inflate(R.menu.activity_list_context_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                int position = getAdapterPosition();
                Note note = getItem(position);
                switch (item.getItemId()) {
                    case R.id.delete: {
                        remove(position);
                        EventBus.getDefault().post(new NoteDeleteEvent(note));
                    }
                }
                return true;
            });
            menu.show();
            return true;
        }
    }

    private List<Note> noteList;

    public NoteAdapter(List<Note> noteList, AppCompatActivity activity) {
        this.noteList = noteList;
        this.activity = activity;
    }

    public void setOnDragStartListener(OnStartDragListener onStartDragListener) {
        super.mDragStartListener = onStartDragListener;
    }

    /* ViewHolder */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        v.setOnClickListener(holder);
        v.setOnLongClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = getItem(position);
        Note.PreviewData data = note.getPreview();
        holder.setTitle(data.title);
        holder.setText(data.text);
        holder.setCreateDate(note.getStarttime());
        holder.setModifyDate(note.getModifytime());
        holder.setIamge(data.picturePath);
    }

    @Override
    public int getAdapterItemCount() {
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
        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.stick_text);
        textView.setText(String.valueOf(getItem(position).getPreview().title.charAt(0)));
        // viewHolder.itemView.setBackgroundColor(Color.parseColor("#AA70DB93"));
    }

    /* footer */

    @Override
    public ViewHolder newFooterHolder(View view) {
        return new ViewHolder(view);
    }

    public Note getItem(int position) throws IndexOutOfBoundsException {
        // Subtract the first one used by header
        if (this.hasHeaderView())
            position--;
        // if (position < this.noteList.size() && position >= 0)
        return this.noteList.get(position);
        // throw new IndexOutOfBoundsException("Note list out of range.");
    }

    /* Actions of Note in NoteCollection */

    public void insert(Note note, int position) {
        super.insertInternal(this.noteList, note, position);
    }

    public void remove(int position) {
        super.removeInternal(this.noteList, position);
    }

    public void swapPositions(int from, int to) {
        super.swapPositions(this.noteList, from, to);
    }

    public void clear() {
        super.clearInternal(this.noteList);
    }

    /* Animation */

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        this.swapPositions(fromPosition, toPosition);
        super.onItemMove(fromPosition, toPosition);
    }

    private void reloadNoteList() {
        this.updateList(TableOperate.getInstance().getAllNotes());
    }

    @Subscribe(sticky = true)
    public void onReceiveNoteChangeSignal(DatabaseModifyEvent signal) {
        this.reloadNoteList();
    }
}
