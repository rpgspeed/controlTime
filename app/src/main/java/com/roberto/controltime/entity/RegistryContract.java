package com.roberto.controltime.entity;

import android.provider.BaseColumns;


public class RegistryContract {
    public static abstract class RegistryEntry implements BaseColumns {
        public static final String TABLE_NAME ="registry";
        public static final String DATE = "date";
        public static final String ENTER = "enter";
    }
}
