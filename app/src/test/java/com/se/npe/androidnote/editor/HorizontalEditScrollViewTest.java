package com.se.npe.androidnote.editor;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class HorizontalEditScrollViewTest {
    private HorizontalEditScrollView markdownController = null;
    private EditorActivity activity = null;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        SortRichEditor editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
        markdownController = activity.findViewById(R.id.scroll_edit);
        assertNotNull(markdownController);
    }

    @Test
    public void onClick() {
        markdownController.onClick(activity.findViewById(R.id.img_header1));
        markdownController.onClick(activity.findViewById(R.id.img_header2));
        markdownController.onClick(activity.findViewById(R.id.img_header3));
        markdownController.onClick(activity.findViewById(R.id.img_header4));
        markdownController.onClick(activity.findViewById(R.id.img_header5));
        markdownController.onClick(activity.findViewById(R.id.img_header6));
        markdownController.onClick(activity.findViewById(R.id.img_bold));
        markdownController.onClick(activity.findViewById(R.id.img_italic));
        markdownController.onClick(activity.findViewById(R.id.img_center_align));
        markdownController.onClick(activity.findViewById(R.id.img_horizontal_rules));
        markdownController.onClick(activity.findViewById(R.id.img_todo));
        markdownController.onClick(activity.findViewById(R.id.img_todo_done));
        markdownController.onClick(activity.findViewById(R.id.img_strike_through));
        markdownController.onClick(activity.findViewById(R.id.img_inline_code));
        markdownController.onClick(activity.findViewById(R.id.img_code));
        markdownController.onClick(activity.findViewById(R.id.img_block_quote));
        markdownController.onClick(activity.findViewById(R.id.img_unorder_list));
        markdownController.onClick(activity.findViewById(R.id.img_order_list));
        markdownController.onClick(activity.findViewById(R.id.img_link));
    }

    @Test
    public void onLongClick() {
        markdownController.onLongClick(activity.findViewById(R.id.img_block_quote));
    }
}