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

import java.util.List;

public class NoteAdapter extends UltimateViewAdapter<NoteAdapter.ViewHolder> {
    public class ViewHolder extends UltimateRecyclerviewViewHolder {
        TextView textView1, textView2;
        public ViewHolder(View itemView)
        {
            super(itemView);
            this.textView1 = itemView.findViewById(R.id.text_view_1);
            this.textView2 = itemView.findViewById(R.id.text_view_2);
        }
    }

    private List<String> stringList;

    public NoteAdapter(List<String> stringList)
    {
        this.stringList = stringList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView1.setText(this.stringList.get(position));
        holder.textView2.setText(this.stringList.get(position) + "233");
    }

    @Override
    public int getAdapterItemCount() {
        return this.stringList.size();
    }

    @Override
    public long generateHeaderId(int position) {
        String string = this.getItem(position);
        if (string.length() > 0)
            return string.charAt(0);
        return -1;
    }

    @Override
    public ViewHolder newHeaderHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.stick_text);
        textView.setText(String.valueOf(getItem(position).charAt(0)));
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

    String getItem(int position)
    {
        if (this.customHeaderView != null) position--;
        if (position < this.stringList.size())
            return stringList.get(position);
        return "";
    }

    public void insert(String string, int position)
    {
        this.insertInternal(this.stringList, string, position);
    }
}
