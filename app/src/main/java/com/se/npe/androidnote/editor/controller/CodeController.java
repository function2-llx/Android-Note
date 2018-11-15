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

public class CodeController {
    private MarkdownEditText mRxMDEditText;
    private static final String CODE_BEGIN = "```\n";
    private static final String CODE_END = "\n```";
    private static final String CODE_END_NEW_LINE = "\n```\n";

    public CodeController(MarkdownEditText rxMDEditText) {
        mRxMDEditText = rxMDEditText;
    }

    public void doInlineCode() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            mRxMDEditText.getText().insert(start, "``");
            mRxMDEditText.setSelection(start + 1, end + 1);
        } else if (end - start > 2) {//选中了4个以上
            int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
            if (position0 != position00) {
                Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
                return;
            }
            Editable editable = mRxMDEditText.getText();
            if ("`".equals(editable.subSequence(Utils.safePosition(start, editable), Utils.safePosition(start + "`".length(), editable)).toString()) &&
                    "`".equals(editable.subSequence(Utils.safePosition(end - "`".length(), editable), Utils.safePosition(end, editable)).toString())) {
                mRxMDEditText.getText().delete(end - "`".length(), end);
                mRxMDEditText.getText().delete(start, start + "`".length());
                mRxMDEditText.setSelection(start, end - "`".length() * 2);
            } else {
                mRxMDEditText.getText().insert(end, "`");
                mRxMDEditText.getText().insert(start, "`");
                mRxMDEditText.setSelection(start, end + "`".length() * 2);
            }
        } else {
            mRxMDEditText.getText().insert(end, "`");
            mRxMDEditText.getText().insert(start, "`");
            mRxMDEditText.setSelection(start, end + "`".length() * 2);
        }
    }

    public void doCode() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            int position1 = Utils.findNextNewLineChar(mRxMDEditText.getText(), end);
            if (position1 == -1) {
                position1 = mRxMDEditText.length();
            }
            Editable editable = mRxMDEditText.getText();
            if (position0 >= 4 && position1 < mRxMDEditText.length() - 4) {
                boolean begin = "```".equals(editable.subSequence(Utils.safePosition(position0 - 1 - "```".length(), editable), Utils.safePosition(position0 - 1, editable)).toString());
                if (begin && CODE_BEGIN.equals(editable.subSequence(Utils.safePosition(position1 + 1, editable), Utils.safePosition(position1 + 1 + CODE_BEGIN.length(), editable)).toString())) {
                    mRxMDEditText.getText().delete(position1 + 1, position1 + 1 + CODE_BEGIN.length());
                    mRxMDEditText.getText().delete(position0 - CODE_END.length(), position0);
                    return;
                }
            }

            int selectedStart = mRxMDEditText.getSelectionStart();
            char c = mRxMDEditText.getText().charAt(position1 >= mRxMDEditText.length() ? mRxMDEditText.length() - 1 : position1);
            if (c == '\n') {
                mRxMDEditText.getText().insert(position1, CODE_END);
            } else {
                mRxMDEditText.getText().insert(position1, CODE_END_NEW_LINE);
            }
            mRxMDEditText.getText().insert(position0, CODE_BEGIN);
            mRxMDEditText.setSelection(selectedStart + CODE_BEGIN.length(), selectedStart + CODE_BEGIN.length());
        } else if (end - start > 6) {
            Editable editable = mRxMDEditText.getText();
            if ("```".equals(editable.subSequence(Utils.safePosition(start, editable), Utils.safePosition(start + "```".length(), editable)).toString()) &&
                    "```".equals(editable.subSequence(Utils.safePosition(end - "```".length(), editable), Utils.safePosition(end, editable)).toString())) {
                int selectedStart = mRxMDEditText.getSelectionStart();
                int selectedEnd = mRxMDEditText.getSelectionEnd();
                mRxMDEditText.getText().delete(end - CODE_END.length(), end);
                mRxMDEditText.getText().delete(start, start + CODE_BEGIN.length());
                mRxMDEditText.setSelection(selectedStart, selectedEnd - 8);
                return;
            }

            code(start, end);
        } else {
            code(start, end);
        }
    }

    private void code(int start, int end) {
        int selectedStart = mRxMDEditText.getSelectionStart();
        int selectedEnd = mRxMDEditText.getSelectionEnd();
        int endAdd = 0;
        char c = mRxMDEditText.getText().charAt(end >= mRxMDEditText.length() ? mRxMDEditText.length() - 1 : end);
        if (c == '\n') {
            mRxMDEditText.getText().insert(end, CODE_END);
            endAdd += 4;
        } else {
            mRxMDEditText.getText().insert(end, CODE_END_NEW_LINE);
            endAdd += 5;
            selectedStart = selectedStart + 1;
        }
        char c1 = mRxMDEditText.getText().charAt(start - 1 < 0 ? 0 : start - 1);
        if (c1 == '\n' || start - 1 < 0) {
            mRxMDEditText.getText().insert(start, CODE_BEGIN);
            endAdd += 4;
        } else {
            mRxMDEditText.getText().insert(start, CODE_END_NEW_LINE);
            endAdd += 4;
        }
        mRxMDEditText.setSelection(selectedStart, selectedEnd + endAdd);
    }
}
