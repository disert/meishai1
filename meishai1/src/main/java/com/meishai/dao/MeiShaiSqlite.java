package com.meishai.dao;

import java.util.ArrayList;
import java.util.List;

import com.meishai.entiy.UserTaoBaoInfo;
import com.meishai.util.DebugLog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MeiShaiSqlite {
    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name) {
            super(context, name, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            DebugLog.d("onCreate");

            db.execSQL("CREATE TABLE IF NOT EXISTS taobao "
                    + "(id INTEGER PRIMARY KEY, " + "userid VARCHAR,"
                    + "taobaoid VARCHAR," + "taobaousername VARCHAR,"
                    + "isindex INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

    private volatile SQLiteDatabase db;
    private DBHelper helper;

    public MeiShaiSqlite(Context context) {
        helper = new DBHelper(context, DATABASE_NAME);
        db = helper.getWritableDatabase();
    }

    private void openDB() {
        /*
		 * if (null == db || !db.isOpen()) { db = helper.getWritableDatabase();
		 * }
		 */
    }

    private void closeDB() {
		/*
		 * if (db != null) db.close();
		 */
    }

    public void insertTaoBaoUserList(List<UserTaoBaoInfo> list) {
        openDB();
        db.beginTransaction();
        try {
            for (int i = 0; i < list.size(); i++) {
                UserTaoBaoInfo item = list.get(i);
                db.execSQL(
                        "insert into taobao values(NULL,?, ?, ?, ?)",
                        new Object[]{item.getUserID(),
                                item.getTaobaoUserID(),
                                item.getTaobaoUserName(),
                                item.isIndex() == true ? 1 : 0});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        closeDB();
    }

    public void insertTaoBaoUser(UserTaoBaoInfo item) {
        openDB();

        db.beginTransaction();

        try {
            db.execSQL("insert or ignore into taobao values(NULL, ?, ?, ?,?)",
                    new Object[]{item.getUserID(), item.getTaobaoUserID(),
                            item.getTaobaoUserName(),
                            item.isIndex() == true ? 1 : 0});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        closeDB();
    }

    public List<UserTaoBaoInfo> getTaobaoUserfList() {
        List<UserTaoBaoInfo> list = new ArrayList<UserTaoBaoInfo>();
        openDB();

        Cursor cur = db
                .rawQuery(
                        "select id, userid, taobaoid, taobaousername, isindex from taobao;",
                        null);
        while (cur.moveToNext()) {
            UserTaoBaoInfo item = new UserTaoBaoInfo();
            item.setUserID(cur.getString(1));
            item.setTaobaoUserID(cur.getString(2));
            item.setTaobaoUserName(cur.getString(3));
            item.setIndex(cur.getInt(4) == 1 ? true : false);
            list.add(item);
        }
        cur.close();
        closeDB();
        return list;
    }

}
