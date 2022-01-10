package com.bihe0832.android.base.m3u8.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.bihe0832.android.base.m3u8.bean.M3U8Info;
import com.bihe0832.android.lib.sqlite.BaseDBHelper;
import com.bihe0832.android.lib.sqlite.BaseTableModel;

import java.util.ArrayList;
import java.util.List;

class M3U8InfoTableModel extends BaseTableModel {
    static final String TABLE_NAME = "m3u8_info";

    private static final String col_id = "id";
    private static final String col_url = "url";
    private static final String col_base_url = "baseurl";
    private static final String col_create_at = "create_at";
    private static final String col_update_at = "update_at";

    static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS ["
            + TABLE_NAME
            + "] ("
            + "[" + col_id + "] INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "[" + col_url + "] NVARCHAR(128)  NULL,"
            + "[" + col_base_url + "] VARCHAR(256)  NULL,"
            + "[" + col_create_at + "] TIMESTAMP  NULL,"
            + "[" + col_update_at + "] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL"
            + ")";
    static final String TABLE_DROP_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

    static int deleteAll(BaseDBHelper helper) {
        return deleteAll(helper, TABLE_NAME);
    }

    private static ContentValues data2CV(M3U8Info info) {
        ContentValues cv = new ContentValues();
        if (null != info) {
            putValues(cv, col_url, info.getM3u8URL());
            putValues(cv, col_base_url, info.getBaseURL());
        }
        putValues(cv, col_update_at, System.currentTimeMillis());
        return cv;
    }

    private static M3U8Info getColumnData(Cursor cursor) {
        M3U8Info info = new M3U8Info();
        try {
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                info.setM3u8URL(getStringByName(cursor, col_url));
                info.setBaseURL(getStringByName(cursor, col_base_url));
                info.setDownloadTime(getLongByName(cursor, col_update_at));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    static List<M3U8Info> getAllData(BaseDBHelper helper) {
        ArrayList<M3U8Info> dataList = new ArrayList<>();
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = " `" + col_update_at + "` DESC ";
        String limit = null;
        Cursor cursor = helper.queryInfo(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        try {
            while (cursor.moveToNext()) {
                M3U8Info info = new M3U8Info();
                info.setM3u8URL(getStringByName(cursor, col_url));
                info.setBaseURL(getStringByName(cursor, col_base_url));
                info.setDownloadTime(getLongByName(cursor, col_update_at));
                dataList.add(info);
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataList;

    }

    static M3U8Info getData(BaseDBHelper helper, String key) {
        String[] columns = null;
        String selection = " " + col_url + " = ? ";
        String[] selectionArgs = {key};
        String groupBy = null;
        String having = null;
        String orderBy = " `" + col_update_at + "` DESC ";
        String limit = " 1 ";
        Cursor cursor = helper.queryInfo(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

        M3U8Info data = getColumnData(cursor);

        if (cursor != null) {
            cursor.close();
        }
        return data;
    }

    private static boolean insertData(BaseDBHelper helper, M3U8Info info) {
        ContentValues values = data2CV(info);
        putValues(values, col_create_at, System.currentTimeMillis());
        long id = helper.insert(TABLE_NAME, null, values);
        return (id != -1);
    }

    private static boolean updateData(BaseDBHelper helper, M3U8Info info) {
        ContentValues values = data2CV(info);
        String whereClause = " `" + col_url + "` = ? ";
        String[] whereArgs = new String[]{info.getM3u8URL()};
        int rows = helper.update(TABLE_NAME, values, whereClause, whereArgs);
        return (rows != 0);
    }

    private static boolean hasData(BaseDBHelper helper, M3U8Info info) {
        String[] columns = null;
        String selection = " " + col_url + " = ? ";
        String[] selectionArgs = {info.getM3u8URL()};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        boolean find = false;
        Cursor cursor = helper.queryInfo(TABLE_NAME, columns,
                selection, selectionArgs, groupBy, having, orderBy, limit);
        try {
            find = (cursor != null && cursor.getCount() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            cursor.close();
        }
        return find;
    }

    static boolean saveData(BaseDBHelper helper, M3U8Info info) {
        boolean success;
        if (hasData(helper, info)) {
            success = updateData(helper, info);
        } else {
            success = insertData(helper, info);
        }

        return success;
    }

    static boolean clearData(BaseDBHelper helper, String info) {
        String whereClause = " `" + col_url + "` = ? ";
        String[] whereArgs = new String[]{info};
        int rows = helper.delete(TABLE_NAME, whereClause, whereArgs);
        return (rows != 0);
    }
}
