package com.se.npe.androidnote.models;

import com.se.npe.androidnote.R;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TableConfig {
    public static String SAVE_PATH = ""; // initialized in TableOperate.init()
    public static final String TABLE_NAME = "AndroidNote";

    // no constructor
    private TableConfig() {

    }

    public static class Note {
        // no constructor
        private Note() {

        }

        public static final String NOTE_ID = "id";
        public static final String NOTE_TITLE = "note_title";
        public static final String NOTE_CONTENT = "note_content";
        public static final String NOTE_START_TIME = "note_start_time";
        public static final String NOTE_MODIFY_TIME = "note_modify_time";
        public static final String NOTE_TAG = "note_tag";
        public static final String NOTE_GROUP = "note_group";
    }

    public static final String GROUP_TABLE = "GroupTable";

    public static class Group {
        // no constructor
        private Group() {

        }

        public static final String GROUP_NAME = "group_name";
    }

    public static class FileSave {
        // no constructor
        private FileSave() {

        }

        public static final String LIST_SEPARATOR = "Sep"+(char)29;
        public static final String LINE_SEPARATOR = "Sep"+(char)31;
    }

    public static class Sorter {
        // no constructor
        private Sorter() {

        }

        public static final String []SORTER_FIELDS = {"sort_title", "sort_create_time", "sort_modify_time"};
        public static final Map<Integer, String> SORTER_OPTION_TO_FIELD = new HashMap<>();

        public static final Map<String, Comparator<com.se.npe.androidnote.models.Note>>
                SORTER_FIELD_TO_COMPARATOR = new HashMap<>();

        static {
            SORTER_OPTION_TO_FIELD.put(R.id.sort_title, SORTER_FIELDS[0]);
            SORTER_OPTION_TO_FIELD.put(R.id.sort_created_time, SORTER_FIELDS[1]);
            SORTER_OPTION_TO_FIELD.put(R.id.sort_modified_time, SORTER_FIELDS[2]);
            SORTER_FIELD_TO_COMPARATOR.put(SORTER_FIELDS[0], Comparator.comparing(com.se.npe.androidnote.models.Note::getTitle));
            SORTER_FIELD_TO_COMPARATOR.put(SORTER_FIELDS[1], Comparator.comparing(com.se.npe.androidnote.models.Note::getStartTime));
            SORTER_FIELD_TO_COMPARATOR.put(SORTER_FIELDS[2], Comparator.comparing(com.se.npe.androidnote.models.Note::getModifyTime));
        }
    }
}