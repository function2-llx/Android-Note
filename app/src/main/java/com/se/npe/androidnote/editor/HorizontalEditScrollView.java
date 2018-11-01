/*
 * Copyright (C) 2018 yydcdut (yuyidong2015@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.se.npe.androidnote.editor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.se.npe.androidnote.R;
import com.se.npe.androidnote.editor.controller.BlockQuotesController;
import com.se.npe.androidnote.editor.controller.CenterAlignController;
import com.se.npe.androidnote.editor.controller.CodeController;
import com.se.npe.androidnote.editor.controller.HeaderController;
import com.se.npe.androidnote.editor.controller.HorizontalRulesController;
import com.se.npe.androidnote.editor.controller.LinkController;
import com.se.npe.androidnote.editor.controller.ListController;
import com.se.npe.androidnote.editor.controller.StrikeThroughController;
import com.se.npe.androidnote.editor.controller.StyleController;
import com.se.npe.androidnote.editor.controller.TodoController;
import com.yydcdut.markdown.MarkdownConfiguration;
import com.yydcdut.markdown.MarkdownEditText;

public class HorizontalEditScrollView extends FrameLayout implements View.OnClickListener,
        View.OnLongClickListener {
    private MarkdownEditText mMarkdownEditText;

    private HeaderController mHeaderController;
    private StyleController mStyleController;
    private CenterAlignController mCenterAlignController;
    private HorizontalRulesController mHorizontalRulesController;
    private TodoController mTodoController;
    private StrikeThroughController mStrikeThroughController;
    private CodeController mCodeController;
    private BlockQuotesController mBlockQuotesController;
    private ListController mListController;
    private LinkController mLinkController;

    public HorizontalEditScrollView(Context context) {
        this(context, null);
    }

    public HorizontalEditScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalEditScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_horizontal_scroll, this, true);
    }

    public void setEditTextAndConfig(@NonNull MarkdownEditText markdownEditText,
                                     @NonNull MarkdownConfiguration markdownConfiguration) {
        mMarkdownEditText = markdownEditText;
        mHeaderController = new HeaderController(markdownEditText, markdownConfiguration);
        mStyleController = new StyleController(markdownEditText);
        mCenterAlignController = new CenterAlignController(markdownEditText);
        mHorizontalRulesController = new HorizontalRulesController(markdownEditText);
        mTodoController = new TodoController(markdownEditText);
        mStrikeThroughController = new StrikeThroughController(markdownEditText);
        mCodeController = new CodeController(markdownEditText);
        mBlockQuotesController = new BlockQuotesController(markdownEditText);
        mListController = new ListController(markdownEditText);
        mLinkController = new LinkController(markdownEditText);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.img_header1).setOnClickListener(this);
        findViewById(R.id.img_header2).setOnClickListener(this);
        findViewById(R.id.img_header3).setOnClickListener(this);
        findViewById(R.id.img_header4).setOnClickListener(this);
        findViewById(R.id.img_header5).setOnClickListener(this);
        findViewById(R.id.img_header6).setOnClickListener(this);
        findViewById(R.id.img_bold).setOnClickListener(this);
        findViewById(R.id.img_italic).setOnClickListener(this);
        findViewById(R.id.img_center_align).setOnClickListener(this);
        findViewById(R.id.img_horizontal_rules).setOnClickListener(this);
        findViewById(R.id.img_todo).setOnClickListener(this);
        findViewById(R.id.img_todo_done).setOnClickListener(this);
        findViewById(R.id.img_strike_through).setOnClickListener(this);
        findViewById(R.id.img_inline_code).setOnClickListener(this);
        findViewById(R.id.img_code).setOnClickListener(this);
        findViewById(R.id.img_block_quote).setOnClickListener(this);
        findViewById(R.id.img_block_quote).setOnLongClickListener(this);
        findViewById(R.id.img_unorder_list).setOnClickListener(this);
        findViewById(R.id.img_order_list).setOnClickListener(this);
        findViewById(R.id.img_link).setOnClickListener(this);
        findViewById(R.id.img_photo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mMarkdownEditText == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.img_header1:
                mHeaderController.doHeader(1);
                break;
            case R.id.img_header2:
                mHeaderController.doHeader(2);
                break;
            case R.id.img_header3:
                mHeaderController.doHeader(3);
                break;
            case R.id.img_header4:
                mHeaderController.doHeader(4);
                break;
            case R.id.img_header5:
                mHeaderController.doHeader(5);
                break;
            case R.id.img_header6:
                mHeaderController.doHeader(6);
                break;
            case R.id.img_bold:
                mStyleController.doBold();
                break;
            case R.id.img_italic:
                mStyleController.doItalic();
                break;
            case R.id.img_center_align:
                mCenterAlignController.doCenter();
                break;
            case R.id.img_horizontal_rules:
                mHorizontalRulesController.doHorizontalRules();
                break;
            case R.id.img_todo:
                mTodoController.doTodo();
                break;
            case R.id.img_todo_done:
                mTodoController.doTodoDone();
                break;
            case R.id.img_strike_through:
                mStrikeThroughController.doStrikeThrough();
                break;
            case R.id.img_inline_code:
                mCodeController.doInlineCode();
                break;
            case R.id.img_code:
                mCodeController.doCode();
                break;
            case R.id.img_block_quote:
                mBlockQuotesController.doBlockQuotes();
                break;
            case R.id.img_unorder_list:
                mListController.doUnOrderList();
                break;
            case R.id.img_order_list:
                mListController.doOrderList();
                break;
            case R.id.img_link:
                mLinkController.doImage();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.img_block_quote) {
            mBlockQuotesController.addNestedBlockQuotes();
        }
        return true;
    }
}
