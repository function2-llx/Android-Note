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

import android.support.annotation.Nullable;
import android.text.style.AlignmentSpan;
import android.widget.EditText;

import com.yydcdut.markdown.span.MDOrderListSpan;
import com.yydcdut.markdown.span.MDUnOrderListSpan;
import com.yydcdut.markdown.utils.TextHelper;
import com.yydcdut.rxmarkdown.RxMDEditText;

public class Utils {
    /**
     * find '\n' from "start" position
     *
     * @param s     text
     * @param start start position
     * @return the '\n' position
     */
    public static int findNextNewLineChar(CharSequence s, int start) {
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                return i;
            }
        }
        return -1;
    }

    /**
     * find '\n' before "start" position
     *
     * @param s     text
     * @param start start position
     * @return the '\n' position
     */
    public static int findBeforeNewLineChar(CharSequence s, int start) {
        for (int i = start - 1; i > 0; i--) {
            if (s.charAt(i) == '\n') {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public static <T> T getSpans(EditText editText, int start, int end, Class<T> clazz) {
        T[] ts = editText.getText().getSpans(start, end, clazz);
        if (ts != null && ts.length > 0) {
            return ts[0];
        }
        return null;
    }

    public static boolean hasCenterSpan(EditText editText, int start, int end) {
        AlignmentSpan.Standard centerSpan = Utils.getSpans(editText, start, end, AlignmentSpan.Standard.class);
        return centerSpan != null;
    }

    public static boolean hasOrderListSpan(RxMDEditText rxMDEditText, int start, int end) {
        MDOrderListSpan orderListSpan = Utils.getSpans(rxMDEditText, start, end, MDOrderListSpan.class);
        return orderListSpan != null;
    }

    public static boolean hasUnOrderListSpan(RxMDEditText rxMDEditText, int start, int end) {
        MDUnOrderListSpan unOrderListSpan = Utils.getSpans(rxMDEditText, start, end, MDUnOrderListSpan.class);
        return unOrderListSpan != null;
    }


    public static boolean hasTodoDone(RxMDEditText rxMDEditText, int start) {
        CharSequence charSequence = rxMDEditText.getText().subSequence(start, start + "- [x] ".length());
        return charSequence.toString().equalsIgnoreCase("- [x] ");
    }

    public static int safePosition(int position, CharSequence s) {
        return TextHelper.safePosition(position, s);
    }
}
