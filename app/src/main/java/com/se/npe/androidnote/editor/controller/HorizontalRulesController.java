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

package com.se.npe.androidnote.editor.controller;

import android.text.Editable;
import android.widget.Toast;

import com.yydcdut.markdown.MarkdownEditText;
import com.yydcdut.markdown.span.MDHorizontalRulesSpan;

public class HorizontalRulesController {
    private MarkdownEditText mRxMDEditText;

    public HorizontalRulesController(MarkdownEditText rxMDEditText) {
        mRxMDEditText = rxMDEditText;
    }

    public void doHorizontalRules() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
        int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
        if (position0 != position00) {
            Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
            return;
        }
        MDHorizontalRulesSpan mdHorizontalRulesSpan = Utils.getSpans(mRxMDEditText, start, end, MDHorizontalRulesSpan.class);
        if (mdHorizontalRulesSpan != null) {
            Editable editable = mRxMDEditText.getText();
            int spanStart = editable.getSpanStart(mdHorizontalRulesSpan);
            int spanEnd = editable.getSpanEnd(mdHorizontalRulesSpan);
            mRxMDEditText.getText().removeSpan(mdHorizontalRulesSpan);
            mRxMDEditText.getText().delete(spanStart, spanEnd);
        } else {
            char c0 = mRxMDEditText.getText().charAt(start <= 0 ? 0 : start - 1);
            char c1 = mRxMDEditText.getText().charAt(end >= mRxMDEditText.length() - 1 ? mRxMDEditText.length() - 1 : end + 1);
            StringBuilder sb = new StringBuilder();
            if (c0 != '\n' && start != 0) {
                sb.append("\n");
            }
            sb.append("---");
            if (c1 != '\n' || end >= mRxMDEditText.length()) {
                sb.append("\n");
            }
            mRxMDEditText.getText().insert(start, sb.toString());
        }
    }
}
