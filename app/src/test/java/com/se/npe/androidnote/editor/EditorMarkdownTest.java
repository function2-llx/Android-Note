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

    @Test
    public void testBlockQuotesController() {
        editText.setText("hello");
        editText.setSelection(0, 5);
        markdownController.onLongClick(activity.findViewById(R.id.img_block_quote));
        editText.setSelection(0, 0);
        markdownController.onLongClick(activity.findViewById(R.id.img_block_quote));
        editText.setSelection(0, 5);
        markdownController.onClick(activity.findViewById(R.id.img_block_quote));
        editText.setSelection(0, 0);
        markdownController.onClick(activity.findViewById(R.id.img_block_quote));
    }

    @Test
    public void testStyleController() {
        editText.setText("hello");
        editText.setSelection(0, 5);
        markdownController.onClick(activity.findViewById(R.id.img_bold));
        editText.setSelection(0, 1);
        markdownController.onClick(activity.findViewById(R.id.img_bold));
        editText.setSelection(0, 0);
        markdownController.onClick(activity.findViewById(R.id.img_bold));
        editText.setSelection(0, 5);
        markdownController.onClick(activity.findViewById(R.id.img_italic));
        editText.setSelection(0, 1);
        markdownController.onClick(activity.findViewById(R.id.img_italic));
        editText.setSelection(0, 0);
        markdownController.onClick(activity.findViewById(R.id.img_italic));
    }

    @Test
    public void testStrikeThroughController() {

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

    }
}
