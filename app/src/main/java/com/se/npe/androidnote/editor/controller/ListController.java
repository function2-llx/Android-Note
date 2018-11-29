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

import android.widget.Toast;

import com.yydcdut.markdown.MarkdownEditText;
import com.yydcdut.markdown.span.MDOrderListSpan;
import com.yydcdut.markdown.span.MDUnOrderListSpan;

public class ListController {
    private MarkdownEditText mRxMDEditText;

    public ListController(MarkdownEditText rxMDEditText) {
        mRxMDEditText = rxMDEditText;
    }

    public void doUnOrderList() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            MDUnOrderListSpan mdUnOrderListSpan = Utils.getSpans(mRxMDEditText, start, end, MDUnOrderListSpan.class);
            int position = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            if (mdUnOrderListSpan != null) {
                if (mdUnOrderListSpan.getNested() == 0) {
                    mRxMDEditText.getText().delete(position, position + "* ".length());
                    return;
                }
                mRxMDEditText.getText().delete(position, position + 1);
                return;
            }
            mRxMDEditText.getText().insert(position, "* ");
        } else {
            int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
            if (position0 != position00) {
                Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
                return;
            }
            MDUnOrderListSpan mdUnOrderListSpan = Utils.getSpans(mRxMDEditText, start, end, MDUnOrderListSpan.class);
            if (mdUnOrderListSpan != null) {
                if (mdUnOrderListSpan.getNested() == 0) {
                    mRxMDEditText.getText().delete(position0, position0 + "* ".length());
                    return;
                }
                mRxMDEditText.getText().delete(position0, position0 + 1);
                return;
            }
            mRxMDEditText.getText().insert(position0, "* ");
        }
    }

    private void doOrderListHandleStartEqEnd(int start, int end) {
        MDOrderListSpan mdOrderListSpan = Utils.getSpans(mRxMDEditText, start, end, MDOrderListSpan.class);
        int position = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
        if (mdOrderListSpan != null) {
            mRxMDEditText.getText().delete(position, position + mdOrderListSpan.getNested() + (mdOrderListSpan.getNumber() / 10 + 1) + ". ".length());
            return;
        }
        if (position == 0) {
            mRxMDEditText.getText().insert(position, "1. ");
        } else {
            MDOrderListSpan mdBeforeLineOrderListSpan = Utils.getSpans(mRxMDEditText, position - 1, position - 1, MDOrderListSpan.class);
            if (mdBeforeLineOrderListSpan != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mdBeforeLineOrderListSpan.getNested(); i++) {
                    sb.append(" ");
                }
                sb.append((mdBeforeLineOrderListSpan.getNumber() + 1)).append(". ");
                mRxMDEditText.getText().insert(position, sb.toString());
            } else {
                mRxMDEditText.getText().insert(position, "1. ");
            }
        }
    }

    private void doOrderListHandleStartNeEnd(int start, int end) {
        int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
        int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
        if (position0 != position00) {
            Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
            return;
        }
        MDOrderListSpan mdOrderListSpan = Utils.getSpans(mRxMDEditText, start, end, MDOrderListSpan.class);
        if (mdOrderListSpan != null) {
            if (mdOrderListSpan.getNested() == 0) {
                int deleteLength = position0 + mdOrderListSpan.getNested() + (mdOrderListSpan.getNumber() / 10 + 1) + ". ".length();
                mRxMDEditText.getText().delete(position0, deleteLength);
                return;
            }
            mRxMDEditText.getText().delete(position0, position0 + 1);
            return;
        }
        if (position0 == 0) {
            mRxMDEditText.getText().insert(position0, "1. ");
        } else {
            MDOrderListSpan mdBeforeLineOrderListSpan = Utils.getSpans(mRxMDEditText, position0 - 1, position0 - 1, MDOrderListSpan.class);
            if (mdBeforeLineOrderListSpan != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mdBeforeLineOrderListSpan.getNested(); i++) {
                    sb.append(" ");
                }
                sb.append((mdBeforeLineOrderListSpan.getNumber() + 1)).append(". ");
                mRxMDEditText.getText().insert(position0, sb.toString());
            } else {
                mRxMDEditText.getText().insert(position0, "1. ");
            }
        }
    }

    public void doOrderList() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            doOrderListHandleStartEqEnd(start, end);
        } else {
            doOrderListHandleStartNeEnd(start, end);
        }
    }
}
