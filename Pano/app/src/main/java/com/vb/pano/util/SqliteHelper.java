package com.vb.pano.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by seven on 2016/11/11.
 */

public class SqliteHelper extends SQLiteOpenHelper{

    public static final String PANO_TABLE="pano";
    public static final String DB_NAME="vbpano.db";
    public static final int DB_VERSION=1;
    public SqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + PANO_TABLE);
        onCreate(db);
        Log. e("Database" ,"onUpgrade" );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table pano(id integer primary key autoincrement,panoId varchar(50) not null,thumNail varchar(50),name varchar(20),address varchar(50),timeMode varchar(20),viewMode varchar(20),descrip varchar(200),qrCode varchar(20),time varchar(50),upload integer)";
        db.execSQL(sql);
    }
}
