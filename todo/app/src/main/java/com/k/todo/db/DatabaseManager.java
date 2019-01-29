package com.k.todo.db;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.k.todo.BuildConfig;

import java.util.concurrent.atomic.AtomicInteger;

public final class DatabaseManager {
    private AtomicInteger openCounter = new AtomicInteger();
    private static SQLiteOpenHelper databaseHelper;
    private static DatabaseManager instance;
    private SQLiteDatabase database;

    public static void initializeInstance(SQLiteOpenHelper helper) {
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                instance = new DatabaseManager();
                databaseHelper = helper;
            }
        }
    }

    public static DatabaseManager getInstance() {
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                throw new IllegalStateException(DatabaseManager.class.getSimpleName() + "" +
                        " is not initialized, call initializeInstance method first");
            }
            return instance;
        }
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (openCounter.incrementAndGet() == 1) {
            // Opening new database
            database = databaseHelper.getWritableDatabase();
        }
        return database;
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        if (openCounter.incrementAndGet() == 1) {
            // Opening new database
            database = databaseHelper.getWritableDatabase();
        }
        return database;
    }

    public synchronized SQLiteDatabase getReadableDatabase() {
        if (openCounter.incrementAndGet() == 1) {
            // Opening new database
            database = databaseHelper.getReadableDatabase();
        }
        return database;
    }

    public synchronized void closeCursorQuietly(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            try {
                cursor.close();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void closeDatabase() {
        if (openCounter.decrementAndGet() == 0) {
            // Closing database
            if (database != null && database.isOpen()) {
                try {
                    if (database.inTransaction()) {
                        database.endTransaction();
                    }
                    database.close();
                } catch (Exception ingore) {
                }
            }
        }
    }

}
