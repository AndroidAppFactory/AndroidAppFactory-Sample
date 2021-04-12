package com.bihe0832.android.base.m3u8.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bihe0832.android.lib.sqlite.BaseDBHelper;


class M3U8InfoDBHelper extends BaseDBHelper {
    private static final String DB_NAME = "zixie_m3u8";

    private static final int DB_VERSION = 2;

    M3U8InfoDBHelper(Context ctx) {
        super(ctx, DB_NAME, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(M3U8InfoTableModel.TABLE_CREATE_SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(M3U8InfoTableModel.TABLE_DROP_SQL);
            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Override")
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(M3U8InfoTableModel.TABLE_DROP_SQL);
            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
