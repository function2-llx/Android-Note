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
import com.se.npe.androidnote.ListActivity;
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

public class GroupAdapter extends UltimateViewAdapter<GroupAdapter.ViewHolder> {
    private List<String> groupList;

    /**
     * Adapt Note to item-like View
     *
     * @author llx
     */

    ListActivity context;

    public GroupAdapter(ListActivity context) {
        this.context = context;
    }

    public class ViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener{
        TextView groupName;

        public ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_view_group_name);
        }

        private String getGroupName() {
            return this.groupName.getText().toString();
        }

        void setGroup(String group) {
            this.groupName.setText(group);
        }

        @Override
        public void onClick(View v) {
            if (getGroupName().equals(context.getString(R.string.group_all_notes))) {
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_menu_group_item, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getAdapterItemCount() {
        return this.groupList.size();
    }

    /* header */

    @Override
    public long generateHeaderId(int position) {
        String tag = getItem(position);
        if (tag == null)
            return -1;

        return tag.charAt(0);
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

        // viewHolder.itemView.setBackgroundColor(Color.parseColor("#AA70DB93"));
    }

    /* footer */

    @Override
    public ViewHolder newFooterHolder(View view) {
        return new ViewHolder(view);
    }

    public String getItem(int position) throws IndexOutOfBoundsException {
        // Subtract the first one used by header
        if (this.hasHeaderView())
            position--;
        // if (position < this.noteList.size() && position >= 0)
        return this.groupList.get(position);
        // throw new IndexOutOfBoundsException("Note list out of range.");
    }

    /* Actions of Note in NoteCollection */

    public void insert(String groupName, int position) {
        super.insertInternal(this.groupList, groupName, position);

    }

    public void remove(int position) {
        super.removeInternal(this.groupList, position);
    }

    public void swapPositions(int from, int to) {
        super.swapPositions(this.groupList, from, to);
    }

    public void clear() {
        super.clearInternal(this.groupList);
    }

    /* Animation */

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        this.swapPositions(fromPosition, toPosition);
        super.onItemMove(fromPosition, toPosition);
    }

}