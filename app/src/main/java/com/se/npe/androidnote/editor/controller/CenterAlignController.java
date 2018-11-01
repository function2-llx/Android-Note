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

public class CenterAlignController {
    private MarkdownEditText mRxMDEditText;

    public CenterAlignController(MarkdownEditText rxMDEditText) {
        mRxMDEditText = rxMDEditText;
    }

    public void doCenter() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
        int position1 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
        if (position0 == position1) {
            int position2 = Utils.findNextNewLineChar(mRxMDEditText.getText(), end);
            if (position2 == -1) {
                position2 = mRxMDEditText.length();
            }
            Editable editable = mRxMDEditText.getText();
            if ("[".equals(editable.subSequence(Utils.safePosition(position0, editable), Utils.safePosition(position0 + 1, editable)).toString()) &&
                    "]".equals(editable.subSequence(Utils.safePosition(position2 - 1, editable), Utils.safePosition(position2, editable)).toString())) {
                mRxMDEditText.getText().delete(position2 - 1, position2);
                mRxMDEditText.getText().delete(position0, position0 + 1);
            } else {
                mRxMDEditText.getText().insert(position2, "]");
                mRxMDEditText.getText().insert(position0, "[");
            }
        } else {
            Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
        }
    }
}
