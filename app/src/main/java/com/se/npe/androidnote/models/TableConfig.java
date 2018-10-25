package com.se.npe.androidnote.models;

public class TableConfig {
    public static String SAVE_PATH = "build/save";
    public static final String TABLE_NAME = "AndroidNote";

    public static class Note {
        public static final String NOTE_ID = "id";
        public static final String NOTE_TITLE = "note_title";
        public static final String NOTE_CONTENT = "note_content";
        public static final String NOTE_START_TIME = "note_start_time";
        public static final String NOTE_MODIFY_TIME = "note_modify_time";
        public static final String NOTE_TAG = "note_tag";
    }

    public static class FileSave {
        public static final String LIST_SEPARATOR = "qwert";
        public static final String LINE_SEPARATOR = "asdfg";
    }
}