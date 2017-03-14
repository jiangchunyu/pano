package com.vb.pano.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vb.pano.bean.Pano;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seven on 2016/11/11.
 */

public class PanoHelper {
    public static final String PANO_TABLE="pano";
    private SqliteHelper helper;
    public PanoHelper(Context context)
    {
        helper=new SqliteHelper(context);
    }
    public void insert(Pano pano)
    {
        if(pano!=null)
        {
            SQLiteDatabase db=helper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put("panoId",pano.getPanoId());
            cv.put("thumNail",pano.getThumNail());
            cv.put("name",pano.getName());
            cv.put("address",pano.getAddress());
            cv.put("timeMode",pano.getTimeMode());
            cv.put("time",pano.getTime());
            cv.put("viewMode",pano.getViewMode());
            cv.put("descrip",pano.getDescrip());
            cv.put("qrCode",pano.getQrCode());
            cv.put("upload",pano.isUpload()? 1:0);
            db.insert(PANO_TABLE,null,cv);
        }

    }
    public List<Pano> queryAll()
    {
        List<Pano> panos=new ArrayList<Pano>();
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query(PANO_TABLE,null,null,null,null,null,null);
        while(cursor.moveToNext())
        {
            Pano pano=new Pano();
            pano.setTime(cursor.getString(cursor.getColumnIndex("time")));
            pano.setQrCode(cursor.getString(cursor.getColumnIndex("qrCode")));
            pano.setThumNail(cursor.getString(cursor.getColumnIndex("thumNail")));
            pano.setViewMode(cursor.getString(cursor.getColumnIndex("viewMode")));
            pano.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            pano.setDescrip(cursor.getString(cursor.getColumnIndex("descrip")));
            pano.setName(cursor.getString(cursor.getColumnIndex("name")));
            pano.setPanoId(cursor.getString(cursor.getColumnIndex("panoId")));
            pano.setTimeMode(cursor.getString(cursor.getColumnIndex("timeMode")));
            int upload=cursor.getInt(cursor.getColumnIndex("upload"));
            if(upload==1)
            {
                pano.setUpload(true);
            }
            else
            {
                pano.setUpload(false);
            }
            panos.add(pano);
        }
        return panos;
        //return testqueryAll();
    }
    //测试数据
    public List<Pano> testqueryAll()
    {
        List<Pano> panos=new ArrayList<Pano>();
        int i=0;
        while(i<20)
        {
            Pano pano=new Pano();
            pano.setTime("2016/08/08 3:56:64");
            pano.setQrCode("0x21");
            pano.setThumNail("http://img5.imgtn.bdimg.com/it/u=2513349511,903456436&fm=21&gp=0.jpg");
            pano.setViewMode("");
            pano.setAddress("中国.成都");
            pano.setDescrip("一趟城市的旅行");
            pano.setName("大熊猫");
            pano.setPanoId("1234");
            pano.setTimeMode("晚上");
            if(i%2==0)
            {
                pano.setUpload(true);
            }
            else
            {
                pano.setUpload(false);
            }

            panos.add(pano);
            i++;
        }
        return panos;
    }
}
