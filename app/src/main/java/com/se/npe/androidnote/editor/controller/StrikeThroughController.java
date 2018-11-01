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

public class StrikeThroughController {
    private MarkdownEditText mRxMDEditText;

    public StrikeThroughController(MarkdownEditText rxMDEditText) {
        mRxMDEditText = rxMDEditText;
    }

    public void doStrikeThrough() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            mRxMDEditText.getText().insert(start, "~~~~");
            mRxMDEditText.setSelection(start + 2, end + 2);
        } else if (end - start > 4) {//选中了4个以上
            int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
            if (position0 != position00) {
                Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
                return;
            }
            Editable editable = mRxMDEditText.getText();
            if ("~~".equals(editable.subSequence(Utils.safePosition(start, editable), Utils.safePosition(start + "~~".length(), editable)).toString()) &&
                    "~~".equals(editable.subSequence(Utils.safePosition(end - "~~".length(), editable), Utils.safePosition(end, editable)).toString())) {
                mRxMDEditText.getText().delete(end - "~~".length(), end);
                mRxMDEditText.getText().delete(start, start + "~~".length());
                mRxMDEditText.setSelection(start, end - "~~".length() * 2);
            } else {
                mRxMDEditText.getText().insert(end, "~~");
                mRxMDEditText.getText().insert(start, "~~");
                mRxMDEditText.setSelection(start, end + "~~".length() * 2);
            }
        } else {
            mRxMDEditText.getText().insert(end, "~~");
            mRxMDEditText.getText().insert(start, "~~");
            mRxMDEditText.setSelection(start, end + "~~".length() * 2);
        }
    }
}
