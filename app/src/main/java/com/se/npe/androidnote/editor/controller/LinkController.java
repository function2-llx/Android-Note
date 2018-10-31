package com.se.npe.androidnote.editor.controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.se.npe.androidnote.R;
import com.yydcdut.markdown.MarkdownEditText;

/**
 * Created by yuyidong on 16/7/21.
 */
public class LinkController {
    public class LinkDialogView extends LinearLayout {
        private EditText mDescriptionEditText;
        private EditText mLinkEditText;

        public LinkDialogView(Context context) {
            super(context);
            init(context);
        }

        public LinkDialogView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public LinkDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            View v = LayoutInflater.from(context).inflate(R.layout.dialog_link, this, true);
            mDescriptionEditText = (EditText) v.findViewById(R.id.edit_description_link);
            mLinkEditText = (EditText) v.findViewById(R.id.edit_link);
        }

        public void clear() {
            mDescriptionEditText.setText("");
            mLinkEditText.setText("http://");
        }

        public String getDescription() {
            return mDescriptionEditText.getText().toString();
        }

        public String getLink() {
            return mLinkEditText.getText().toString();
        }
    }

    private LinkDialogView mLinkDialogView;
    private MarkdownEditText mRxMDEditText;

    private AlertDialog mAlertDialog;

    public LinkController(MarkdownEditText rxMDEditText) {
        mRxMDEditText = rxMDEditText;
        mLinkDialogView = new LinkDialogView(rxMDEditText.getContext());
        mLinkDialogView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void doImage() {
        if (mAlertDialog == null) {
            initDialog();
        }
        mLinkDialogView.clear();
        mAlertDialog.show();
    }

    private void initDialog() {
        mAlertDialog = new AlertDialog.Builder(mRxMDEditText.getContext())
                .setView(mLinkDialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String description = mLinkDialogView.getDescription();
                        String link = mLinkDialogView.getLink();
                        doRealLink(description, link);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Link")
                .setCancelable(false)
                .create();
    }

    private void doRealLink(String description, String link) {
        int start = mRxMDEditText.getSelectionStart();
        if (TextUtils.isEmpty(description)) {
            mRxMDEditText.getText().insert(start, "[](" + link + ")");
            mRxMDEditText.setSelection(start + 2);
        } else {
            mRxMDEditText.getText().insert(start, "[" + description + "](" + link + ")");
        }
    }
}
