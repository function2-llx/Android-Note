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

    public void show() {
        this.setVisibility(View.VISIBLE);
        List<String> tagList = new ArrayList<>();
//        for (int i = 0; i < 10; i++)
//            tagList.add("tag" + i);

        tagList = TableOperate.getInstance().getAllTags();

        this.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                checked[position] = !checked[position];
                TagView tagView = getTagView(position);

                if (checked[position])
                    tagView.setBackgroundColor(getResources().getColor(R.color.checked_color, null));
                else
                    tagView.setBackgroundColor(getResources().getColor(R.color.unchecked_color, null));
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
        this.setTags(tagList);
        checked = new boolean[tagList.size()];
        for (int i = 0; i < checked.length; i++) {
            getTagView(i).setBackgroundColor(getResources().getColor(R.color.unchecked_color, null));
            checked[i] = false;
        }
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
