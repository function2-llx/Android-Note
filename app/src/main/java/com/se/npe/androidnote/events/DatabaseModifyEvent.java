package com.se.npe.androidnote.events;

public class DatabaseModifyEvent {
    private String info;

    public DatabaseModifyEvent(String info)
    {
        this.info = info;
    }

    String getInfo()
    {
        return this.info;
    }
}