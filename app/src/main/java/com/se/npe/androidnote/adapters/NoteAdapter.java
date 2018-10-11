package com.se.npe.androidnote.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.events.NoteDeleteEvent;
import com.se.npe.androidnote.events.NoteSelectEvent;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.events.DatabaseModifyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adapt NoteCollection to list-like View
 *
 * @author llx
 */
public class NoteAdapter extends UltimateViewAdapter<NoteAdapter.ViewHolder>{
    private AppCompatActivity activity;



    /**
     * Implement INoteCollection
     */

    public List<Note> getSearchResult(String parameter) {
        List<Note> ret = new ArrayList<>();
        for (Note note : this.noteList) {
            if (note.getTitle().contains(parameter))
                ret.add(note);
        }
        return ret;
    }

    public void sortByTitle()
    {
        Collections.sort(this.noteList, Comparator.comparing(Note::getTitle));
        this.notifyDataSetChanged();
    }

    public void updateList(List<Note> list)
    {
        if (list.isEmpty())
            this.clear();
        else {
            this.noteList.clear();
            for (Note note: list) {
                noteList.add(note);
            }
            this.notifyDataSetChanged();
        }
    }

    // public void updateList(List<Note> list) {
    //     if (list.isEmpty())
    //         this.clear();
    //     else {
    //         this.noteList.clear();
    //         for (Note note : list) {
    //             noteList.add(note);
    //         }
    //         this.notifyDataSetChanged();
    //     }
    // }

    /**
     * Adapt Note to item-like View
     *
     * @author llx
     */
    public class ViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
        private TextView title, text;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.text_view_title);
            this.text = itemView.findViewById(R.id.text_view_text);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setText(String text) {
            this.text.setText(text);
        }

        @Override
        public void onClick(@NonNull View v) {
            Note selectedNote = getItem(getAdapterPosition());
            EventBus.getDefault().postSticky(new NoteSelectEvent(selectedNote));
            Intent intent = new Intent(activity, EditorActivity.class);
            activity.startActivity(intent);
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
        ViewHolder holder = new ViewHolder(v);
        v.setOnClickListener(holder);
        Button btn = v.findViewById(R.id.list_item_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(activity, v);
                popupMenu.getMenuInflater().inflate(R.menu.list_item_options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.preview: {
                                break;
                            }
                            case R.id.delete: {
                                System.err.println(holder.getAdapterPosition());
                                NoteAdapter.this.remove(holder.getAdapterPosition());
                                break;
                            }
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note.PreviewData data = getItem(position).getPreview();
        holder.setTitle(data.title);
        holder.setText(data.text);
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

    Note getItem(int position) throws IndexOutOfBoundsException {
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
        Note note = getItem(position);
        super.removeInternal(this.noteList, position);
        EventBus.getDefault().post(new NoteDeleteEvent(note));
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

    private void reloadNoteList()
    {
        this.updateList(TableOperate.getInstance().getAllNotes());
    }

    @Subscribe(sticky = true)
    void onReceiveNoteChangeSignal(DatabaseModifyEvent signal)
    {
        this.reloadNoteList();
    }
}
