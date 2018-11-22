package com.se.npe.androidnote.editor;

import android.widget.EditText;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.PictureData;
import com.se.npe.androidnote.models.SoundData;
import com.se.npe.androidnote.models.TextData;
import com.se.npe.androidnote.models.VideoData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class TestEditorLoadNote {
    private static final String DATA = "hello";
    private SortRichEditor editor;

    @Before
    public void setUp() {
        EditorActivity activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
    }

    @Test
    public void loadNote() {
        try {
            SoundPlayer.isUnderTest = true;
            Note note = new Note();
            ArrayList<String> tags = new ArrayList<>();
            tags.add(DATA);
            note.setTag(tags);
            ArrayList<IData> data = new ArrayList<>();
            data.add(new TextData(DATA));
            data.add(new VideoData(DATA));
            data.add(new PictureData(DATA));
            data.add(new SoundData(DATA, DATA));
            note.setContent(data);
            new SortRichEditor.NoteLoader(editor, note).run();
            for (int i = 0; i < 2; ++i)
                editor.insertEditTextAtIndex(editor.containerLayout.getChildCount(), "");
            for (int i = 0; i < 2; ++i)
                editor.onBackspacePress((EditText)
                        (editor.containerLayout.getChildAt(editor.containerLayout.getChildCount() - 2)));
            assertNotNull(editor.buildNote());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SoundPlayer.isUnderTest = false;
        }
    }
}
