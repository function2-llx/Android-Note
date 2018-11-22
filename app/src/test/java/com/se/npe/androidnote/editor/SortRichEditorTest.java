package com.se.npe.androidnote.editor;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donkingliang.labels.LabelsView;
import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.util.ReturnValueEater;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class SortRichEditorTest {
    private SortRichEditor editor;
    private static final String MEDIA_PATH = "tmp";

    @Before
    public void setUp() {
        EditorActivity activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);

        // create medias for insertion
        File f = new File(MEDIA_PATH);
        try {
            ReturnValueEater.eat(f.createNewFile());
        } catch (IOException e) {
            // no-op
        }
        try (FileOutputStream out = new FileOutputStream(f)) {
            byte[] b = new byte[1000];
            out.write(b, 0, b.length);
        } catch (IOException e) {
            // no-op
        }
    }

    @Test
    public void changeIsMarkdown() {
        editor.changeIsMarkdown();
        editor.changeIsMarkdown();
    }

    @Test
    public void testComputeScroll() {
        editor.computeScroll();
    }

    @Test
    public void testUse() {
        SoundPlayer.isUnderTest = true;
        editor.addSound(MEDIA_PATH);
        editor.addPicture(MEDIA_PATH);
        editor.addVideo(MEDIA_PATH);
        editor.destroy();
        LinearLayout containerLayout = editor.containerLayout;
        for (int i = 0; i < containerLayout.getChildCount(); ++i) {
            if (containerLayout.getChildAt(i) instanceof RelativeLayout) {
                RelativeLayout media = (RelativeLayout) containerLayout.getChildAt(i);
                View delete = media.getChildAt(1);
                delete.performClick();
            }
        }
        for (int i = 0; i < containerLayout.getChildCount(); ++i) {
            if (containerLayout.getChildAt(i) instanceof ImageView) {
                ImageView placeholder = (ImageView) containerLayout.getChildAt(i);
                placeholder.performClick();
            }
        }
        editor.sort();
        editor.sort();
        View first = containerLayout.getChildAt(0);
        editor.viewDragHelperCallBack.tryCaptureView(first, 0);
        editor.viewDragHelperCallBack.clampViewPositionHorizontal(first, 0, 0);
        editor.viewDragHelperCallBack.clampViewPositionVertical(first, 0, 0);
        editor.viewDragHelperCallBack.onViewPositionChanged(first, 0, 0, 0, 0);
        try {
            editor.viewDragHelperCallBack.onViewReleased(first, 0, 0);
        } catch (Exception e) {
            // no-op
        }
        editor.sort();
        editor.viewDragHelperCallBack.resetChildPosition();
        editor.sort();
        SoundPlayer.isUnderTest = false;
    }

    @Test
    public void testClickEmptyView() {
        editor.emptyView.performClick();

    }

    @Test
    public void modifyTags() {
        LabelsView tags = editor.tags;
        int size = tags.getLabels().size();
        TextView hackTextView = new TextView(editor.getContext());
        hackTextView.setTag(R.id.tag_key_position, size - 2);
        tags.onClick(hackTextView);
        hackTextView.setTag(R.id.tag_key_position, size - 1);
        tags.onClick(hackTextView);
        editor.onAddTag("hello");
        editor.onAddTag(""); // empty
        editor.onAddTag("hello"); // duplicate
    }

    @After
    public void tearDown() {
        File f = new File(MEDIA_PATH);
        ReturnValueEater.eat(f.delete());
    }
}