package com.roberto.controltime.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RegistryDbHelper extends SQLiteOpenHelper {

    public static final String REGISTRY_DB = "Registry.db";
    public static final int VERSION = 1;
    public static final int WORK_DAY = 525 * 60 * 1000;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private SimpleDateFormat simpleDateFormatQuery = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatDateTimeUI = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat simpleDateFormatTimeUI = new SimpleDateFormat("HH:mm:ss");


    public RegistryDbHelper(Context context) {
        super(context, REGISTRY_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RegistryContract.RegistryEntry.TABLE_NAME + "(" +
                    RegistryContract.RegistryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RegistryContract.RegistryEntry.DATE + " STRING NOT NULL ," +
                RegistryContract.RegistryEntry.ENTER + " STRING NOT NULL"+ ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Registry getLastAccess() throws ParseException {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + RegistryContract.RegistryEntry.TABLE_NAME +
                " ORDER BY " + RegistryContract.RegistryEntry._ID +
                " DESC LIMIT 1", new String[0]);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return getRegistryByCursor(cursor);
        }else {
            return null;
        }

    }

    public String getLastAccessDate() throws ParseException {
        Registry lastAccess = getLastAccess();
        return simpleDateFormatDateTimeUI.format(lastAccess.getDate());
    }


    public Registry insertRegistry(boolean entry) throws ParseException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RegistryContract.RegistryEntry.DATE,simpleDateFormat.format(new Date()));
        contentValues.put(RegistryContract.RegistryEntry.ENTER, Boolean.toString(entry));
        database.insert(RegistryContract.RegistryEntry.TABLE_NAME,null,contentValues);
        database.close();

        return getLastAccess();
    }

    public void deleteDatabase() {
        deleteDatabase();
    }


    public String getRemainingDay() throws ParseException {
        Date today = new Date();

        String query = "SELECT * FROM " + RegistryContract.RegistryEntry.TABLE_NAME
                + " WHERE strftime('%Y-%m-%d'," + RegistryContract.RegistryEntry.DATE + ") = '" + simpleDateFormatQuery.format(today) +"'"
                + " ORDER BY " + RegistryContract.RegistryEntry.DATE + " ASC";
        Cursor cursor = getReadableDatabase().rawQuery(query , new String[0]);

        if (cursor.getCount() > 0) {
            final List<Registry> registryList = getRegistryListByCursor(cursor);
            return calculateRemainingDay(registryList);
        }else {
            return null;
        }
    }

    private String calculateRemainingDay(List<Registry> registryList) {
        long totalTime = 0;
        Date date = new Date();

        for (Registry registry : registryList) {
            if (registry.isEntry()) {
                boolean calculated = false;
                final Date entryDate = registry.getDate();
                for (Registry registryExit : registryList) {
                    final Date exitDate = registryExit.getDate();
                    if(!registryExit.isEntry() && (exitDate.getTime() > entryDate.getTime())){
                        totalTime = totalTime + (exitDate.getTime() - entryDate.getTime());
                        calculated = true;
                        break;
                    }
                }
                if (!calculated) {
                    totalTime = totalTime + (date.getTime() - entryDate.getTime());
                }
            }
        }

        if (totalTime > WORK_DAY) {
            return "+" + simpleDateFormatTimeUI.format(new Date(totalTime - WORK_DAY));
        } else {
            return "" + simpleDateFormatTimeUI.format(new Date(WORK_DAY - totalTime));
        }
    }

    private List<Registry> getRegistryListByCursor(final Cursor cursor) throws ParseException {
        List<Registry> registryList = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            registryList.add(getRegistryByCursor(cursor));
            cursor.moveToNext();
        }

        return registryList;
    }

    private Registry getRegistryByCursor(Cursor cursor) throws ParseException {
        if (cursor.isNull(0)) {
            cursor.moveToNext();
        }
        int id = cursor.getInt(0);
        Date date = simpleDateFormat.parse(cursor.getString(1));
        boolean entry = Boolean.valueOf(cursor.getString(2));
        return new Registry(id,date,entry);
    }
}
