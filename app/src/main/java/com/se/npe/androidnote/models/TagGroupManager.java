package com.se.npe.androidnote.models;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.se.npe.androidnote.R;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class TagGroupManager extends TagContainerLayout{

    public TagGroupManager(Context context) {
        this(context, null);
    }

    public TagGroupManager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroupManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                switchCheckedState(position);
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
        this.setBackgroundColor(getResources().getColor(R.color.white, null));
        this.setRippleAlpha(0);
        this.setVisibility(View.INVISIBLE);
    }

    private boolean[] checked;
    List<String> tagList;

    private void setChecked(int position, boolean checkedState) {
        checked[position] = checkedState;
        TagView tagView = getTagView(position);
        if (checkedState)
            tagView.setTagBorderColor(getResources().getColor(R.color.checked_color, null));
        else
            tagView.setTagBorderColor(getResources().getColor(R.color.unchecked_color, null));
    }

    void switchCheckedState(int position) {
        this.setChecked(position, !checked[position]);
    }

    public void show() {
        this.setTags(tagList);
        for (int i = 0; i < checked.length; i++)
            setChecked(i, checked[i]);
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.INVISIBLE);
        this.removeAllViews();
    }

    public void updateTags() {
        tagList = TableOperate.getInstance().getAllTags();
        checked = new boolean[tagList.size()];
    }

    public List<String> getCheckedTags() {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < checked.length; i++)
            if (checked[i])
                ret.add(tagList.get(i));

        return ret;
    }

}
