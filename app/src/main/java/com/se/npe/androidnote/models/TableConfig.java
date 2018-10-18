package com.se.npe.androidnote.models;

public class TableConfig {
    public static final String TABLE_NAME = "Notetest";

    public static class Note{
        public static final String NOTE_ID="id";
        public static final String NOTE_TITLE="note_title";
        public static final String NOTE_CONTENT="note_content";
        public static final String NOTE_STARTTIME="note_starttime";
        public static final String NOTE_MODIFYTIME="note_modifytime";
    }

    public static class Filesave
    {
        public static final String LIST_SEPERATOR="qwert";
        public static final String LINE_SEPERATOR="asdfg";
    }
}