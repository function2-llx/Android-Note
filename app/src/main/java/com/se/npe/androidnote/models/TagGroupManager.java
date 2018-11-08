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
        super(context);
    }

    public TagGroupManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagGroupManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean[] checked;

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
        this.setBackgroundColor(getResources().getColor(R.color.white, null));
        this.setRippleAlpha(0);
        this.setVisibility(View.VISIBLE);

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
        List<String> tagList;
        tagList = TableOperate.getInstance().getAllTags();
        this.setTags(tagList);
        checked = new boolean[tagList.size()];
        for (int i = 0; i < checked.length; i++)
            setChecked(i, false);
    }

    public void hide() {
        this.setVisibility(View.INVISIBLE);
        this.removeAllViews();
    }

    public List<String> getCheckedTags() {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < checked.length; i++)
            if (checked[i])
                ret.add(this.getTagText(i));

        return ret;
    }

}
