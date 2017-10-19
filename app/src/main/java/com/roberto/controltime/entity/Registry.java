package com.roberto.controltime.entity;


import java.util.Date;

public class Registry  {
    private final int id;
    private final Date date;
    private final boolean entry;

    public Registry(int id, Date date, boolean entry) {
        this.id = id;
        this.date = date;
        this.entry = entry;
    }

    public boolean isEntry() {
        return entry;
    }

    public Date getDate() {
        return date;
    }



}
