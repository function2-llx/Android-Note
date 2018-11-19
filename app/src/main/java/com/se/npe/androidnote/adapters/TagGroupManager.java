package com.se.npe.androidnote.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.se.npe.androidnote.R;
import com.se.npe.androidnote.models.TableOperate;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class TagGroupManager extends TagContainerLayout {

    public TagGroupManager(Context context) {
        this(context, null);
    }

    public TagGroupManager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroupManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private List<String> tagList;
    private boolean[] checked;

    private void setChecked(int position, boolean checkedState) {
        checked[position] = checkedState;
        setCheckedTagView(position, checkedState);
    }

    private void setCheckedTagView(int position, boolean checkedState) {
        TagView tagView = getTagView(position);
        if (checkedState)
            tagView.setTagBorderColor(getResources().getColor(R.color.checked_color, null));
        else
            tagView.setTagBorderColor(getResources().getColor(R.color.unchecked_color, null));
        tagView.invalidate();
    }

    public void switchCheckedState(int position) {
        setChecked(position, !checked[position]);
        if (checked[checked.length - 1]) // with checked tag-all
            if (position != checked.length - 1) // check other tag with checked tag-all
                setChecked(checked.length - 1, false); // uncheck tag-all
            else // check tag-all with checked tag-all
                for (int i = 0; i < checked.length - 1; i++) // uncheck all other tags
                    if (checked[i])
                        setChecked(i, false);
    }

    public void init() {
        // initialize
        tagList = TableOperate.getInstance().getAllTags();
        checked = new boolean[tagList.size() + 1]; // initialized false (not checked)
        // the final tag: all checked
        tagList.add("all");
        checked[checked.length - 1] = true; // initialized true
        // set tags
        this.setTags(tagList);
        for (int i = 0; i < checked.length; i++)
            setCheckedTagView(i, checked[i]);
    }

    public void show() {
        init();
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.INVISIBLE);
        this.removeAllViews();
    }

    public List<String> getCheckedTags() {
        List<String> ret = new ArrayList<>();

        if (checked[checked.length - 1]) // all checked
            for (int i = 0; i < checked.length - 1; i++)
                ret.add(tagList.get(i));
        else
            for (int i = 0; i < checked.length - 1; i++)
                if (checked[i])
                    ret.add(tagList.get(i));
        return ret;
    }
}
