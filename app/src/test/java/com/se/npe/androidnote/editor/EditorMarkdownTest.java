package com.se.npe.androidnote.editor;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;
import com.yydcdut.markdown.MarkdownEditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class EditorMarkdownTest {
    private static final String[] INPUT = {"hello", "new\nline", "1 2 3 4 5"};

    private EditorActivity activity;
    private HorizontalEditScrollView markdownController;
    private MarkdownEditText editText;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        SortRichEditor editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
        markdownController = activity.findViewById(R.id.scroll_edit);
        assertNotNull(markdownController);
        editor.emptyView.performClick();
        editText = (MarkdownEditText) (editor.lastFocusEdit);
    }

    private void click(int id, String input) {
        editText.setText(input);
        for (int i = 0; i < Math.min(10, input.length()); ++i) {
            editText.setSelection(0, i);
            markdownController.onClick(activity.findViewById(id));
        }
    }

    private void longClick(int id, String input) {
        editText.setText(input);
        for (int i = 0; i < Math.min(10, input.length()); ++i) {
            editText.setSelection(0, i);
            markdownController.onLongClick(activity.findViewById(id));
        }
    }

    @Test
    public void testBlockQuotesController() {
        for (String input : INPUT) {
            click(R.id.img_block_quote, input);
            longClick(R.id.img_block_quote, input);
        }
    }

    @Test
    public void testStyleController() {
        for (String input : INPUT) {
            click(R.id.img_bold, input);
            click(R.id.img_italic, input);
        }
    }

    @Test
    public void testStrikeThroughController() {
        for (String input : INPUT) {
            click(R.id.img_strike_through, input);
        }
    }

    @Test
    public void testCodeController() {
        String code = "#include<cstdio>\n"
                + "int main()\n"
                + "{\n"
                + "    printf(\"hello world\\n\");\n"
                + "}\n";
        editText.setText(code);
        editText.setSelection(0, code.length());
        markdownController.onClick(activity.findViewById(R.id.img_code));
        editText.setSelection(0, 0);
        markdownController.onClick(activity.findViewById(R.id.img_code));
        editText.setSelection(0, code.length());
        markdownController.onClick(activity.findViewById(R.id.img_inline_code));
        editText.setSelection(0, 0);
        markdownController.onClick(activity.findViewById(R.id.img_inline_code));
        code = "print('hello world')";
        editText.setText(code);
        editText.setSelection(0, code.length());
        markdownController.onClick(activity.findViewById(R.id.img_inline_code));
    }

    @Test
    public void testListController() {
        for (String input : INPUT) {
            click(R.id.img_unorder_list, input);
            click(R.id.img_order_list, input);
        }
    }
}
