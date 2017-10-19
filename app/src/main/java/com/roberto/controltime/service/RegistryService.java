package com.roberto.controltime.service;


import android.content.Context;

import com.roberto.controltime.entity.Registry;
import com.roberto.controltime.entity.RegistryDbHelper;

import java.text.ParseException;

public class RegistryService {

    private static final boolean ENTRY = true;
    private static final boolean EXIT = false;

    private final RegistryDbHelper registryDbHelper;

    public RegistryService(Context context) {
        registryDbHelper = new RegistryDbHelper(context);
    }

    public void deleteDatabase() {
        registryDbHelper.deleteDatabase();
    }

    public Registry getLastAccess() {
        Registry registry = null;
        try {
            registry = registryDbHelper.getLastAccess();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return registry;
    }

    public Registry registryEntry() {
        try {
            return registryDbHelper.insertRegistry(ENTRY);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Registry registryExit() {
        try {
            return registryDbHelper.insertRegistry(EXIT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLastAccessDate() {
        try {
            return registryDbHelper.getLastAccessDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRemainingDay() {
        try {
            return registryDbHelper.getRemainingDay();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
