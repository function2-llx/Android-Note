package com.se.npe.androidnote.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.models.Note;

import java.util.List;

public class NoteAdapter extends UltimateViewAdapter<NoteAdapter.ViewHolder> {
    public class ViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener{
        private TextView title, text;

        int click_cnt;

        public ViewHolder(View itemView)
        {
            super(itemView);
            this.click_cnt = 0;
            this.title = itemView.findViewById(R.id.text_view_title);
            this.text = itemView.findViewById(R.id.text_view_text);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setText(String text)
        {
            this.text.setText(text);
        }

        @Override
        public void onClick(View v) {
            this.setTitle(String.format("%d clicked!", ++this.click_cnt));
        }
    }

    private List<Note> noteList;

    public NoteAdapter(List<Note> noteList)
    {
        this.noteList = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        v.setOnClickListener(holder);
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
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.stick_text);
        textView.setText(String.valueOf(getItem(position).getPreview().title.charAt(0)));
//        viewHolder.itemView.setBackgroundColor(Color.parseColor("#AA70DB93"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stick_header_item, parent, false);
        return new RecyclerView.ViewHolder(v) {};
    }

    @Override
    public ViewHolder newFooterHolder(View view) {
        return new ViewHolder(view);
    }

    Note getItem(int position)
    {
        if (this.customHeaderView != null)
            position--;
        if (position < this.noteList.size())
            return this.noteList.get(position);
        return null;
    }

    public void insert(Note note, int position)
    {
        super.insertInternal(this.noteList, note, position);
    }

    public void remove(int position)
    {
        super.removeInternal(this.noteList, position);
    }

    public void swapPositions(int from, int to)
    {
        super.swapPositions(this.noteList, from, to);
    }

    public void clear()
    {
        super.clearInternal(this.noteList);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        this.swapPositions(fromPosition, toPosition);
        super.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        this.remove(position);
        super.onItemDismiss(position);
    }
}
