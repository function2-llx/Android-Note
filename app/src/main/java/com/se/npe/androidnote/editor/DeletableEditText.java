package com.se.npe.androidnote.editor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.se.npe.androidnote.EditorActivity;
import com.yydcdut.markdown.MarkdownConfiguration;
import com.yydcdut.markdown.MarkdownEditText;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.callback.OnTodoClickCallback;
import com.yydcdut.markdown.loader.MDImageLoader;
import com.yydcdut.markdown.syntax.edit.EditFactory;
import com.yydcdut.markdown.syntax.text.TextFactory;
import com.yydcdut.markdown.theme.ThemeSunburst;

/**
 * 处理软键盘回删按钮backSpace时回调OnKeyListener
 */
public class DeletableEditText extends MarkdownEditText {
    private TextView md;
    private MarkdownProcessor textViewProcessor;
    MarkdownConfiguration editTextMarkdownConfiguration;

    public DeletableEditText(Context context) {
        super(context);
        setPadding(0, 0, 0, 0);
        md = new TextView(context);
        md.setGravity(Gravity.TOP);
        md.setCursorVisible(true);
        md.setBackgroundResource(android.R.color.transparent);
        md.setTextColor(Color.parseColor("#333333"));
        md.setTextSize(14);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 45;
        lp.leftMargin = 45;
        lp.rightMargin = 45;
        md.setLayoutParams(lp);
        md.setVisibility(GONE);
        setEditTextMarkdown(context);
        setTextViewMarkdown(context);
    }

    private void setEditTextMarkdown(Context context) {
        editTextMarkdownConfiguration = new MarkdownConfiguration.Builder(context)
                .setDefaultImageSize(50, 50)
                .setBlockQuotesLineColor(0xff33b5e5)
                .setHeader1RelativeSize(1.6f)
                .setHeader2RelativeSize(1.5f)
                .setHeader3RelativeSize(1.4f)
                .setHeader4RelativeSize(1.3f)
                .setHeader5RelativeSize(1.2f)
                .setHeader6RelativeSize(1.1f)
                .setHorizontalRulesColor(0xff99cc00)
                .setCodeBgColor(0xffff4444)
                .setTodoColor(0xffaa66cc)
                .setTodoDoneColor(0xffff8800)
                .setUnOrderListColor(0xff00ddff)
                .build();
        MarkdownProcessor editTextProcessor = new MarkdownProcessor(context);
        editTextProcessor.config(editTextMarkdownConfiguration);
        editTextProcessor.factory(EditFactory.create());
        editTextProcessor.live(this);
    }

    private void setTextViewMarkdown(Context context) {
        MarkdownConfiguration textViewMarkdownConfiguration = new MarkdownConfiguration.Builder(context)
                .setDefaultImageSize(50, 50)
                .setBlockQuotesLineColor(0xff33b5e5)
                .setHeader1RelativeSize(1.6f)
                .setHeader2RelativeSize(1.5f)
                .setHeader3RelativeSize(1.4f)
                .setHeader4RelativeSize(1.3f)
                .setHeader5RelativeSize(1.2f)
                .setHeader6RelativeSize(1.1f)
                .setHorizontalRulesColor(0xff99cc00)
                .setCodeBgColor(0xffff4444)
                .setTodoColor(0xffaa66cc)
                .setTodoDoneColor(0xffff8800)
                .setUnOrderListColor(0xff00ddff)
                .setHorizontalRulesHeight(1)
                .setLinkFontColor(Color.BLUE)
                .showLinkUnderline(false)
                .setTheme(new ThemeSunburst())
                .setOnTodoClickCallback((view, line, lineNumber) -> md.getText())
                .build();
        textViewProcessor = new MarkdownProcessor(context);
        textViewProcessor.factory(TextFactory.create());
        textViewProcessor.config(textViewMarkdownConfiguration);
    }

    public void render(boolean isRender) {
        if (isRender) {
            md.setText(textViewProcessor.parse(getText().toString()));
            md.setVisibility(VISIBLE);
            this.setVisibility(GONE);
        } else {
            md.setVisibility(GONE);
            this.setVisibility(VISIBLE);
        }
    }

    public void setController(HorizontalEditScrollView controller) {
        controller.setEditTextAndConfig(this, editTextMarkdownConfiguration);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new DeleteInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    private class DeleteInputConnection extends InputConnectionWrapper {

        public DeleteInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    public TextView getMd() {
        return md;
    }
}