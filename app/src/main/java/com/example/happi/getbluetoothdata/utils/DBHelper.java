package com.example.happi.getbluetoothdata.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.happi.getbluetoothdata.activity.YunDataActivity;


public class DBHelper extends SQLiteOpenHelper {

	 private SQLiteDatabase mSqliteDatabase;

	    public DBHelper(Context context) {
	        /**
	         * 1-->数据库版本
	         */
	        super(context,"bluetooth_data.db",null,2);
	        mSqliteDatabase = getWritableDatabase();
	    }

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		// TODO Auto-generated method stub
		//用户信息表
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 皮尔丹顿");
        sqLiteDatabase.execSQL("create table 皮尔丹顿(_id integer primary key autoincrement,value text not null,id text not null,datetime text)");
        
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 德利服装");
        sqLiteDatabase.execSQL("create table 德利服装(_id integer primary key autoincrement,value text not null,id text not null,datetime text)");
        
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 卡尔丹顿服装");
        sqLiteDatabase.execSQL("create table 卡尔丹顿服装(_id integer primary key autoincrement,value text not null,id text not null,datetime text)");


        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onCreate(db);
	}

	/**
	 * 插入数据
	 * @param rec
     */
	public void addPierData(YunDataActivity.Records rec) {
		mSqliteDatabase = getReadableDatabase();

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("insert into 皮尔丹顿(value,id,datetime) values('").append(rec.value).append("','").append(rec.id).append("','").append(rec.datetime).append("')");
            mSqliteDatabase.execSQL(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mSqliteDatabase.close();
        }
	}

	public void addDeliData(YunDataActivity.Records rec) {
		mSqliteDatabase = getReadableDatabase();

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into 德利服装(value,id,datetime) values('").append(rec.value).append("','").append(rec.id).append("','").append(rec.datetime).append("')");
			mSqliteDatabase.execSQL(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mSqliteDatabase.close();
		}
	}

	public void addKaerData(YunDataActivity.Records rec) {
		mSqliteDatabase = getReadableDatabase();

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into 卡尔丹顿服装(value,id,datetime) values('").append(rec.value).append("','").append(rec.id).append("','").append(rec.datetime).append("')");
			mSqliteDatabase.execSQL(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mSqliteDatabase.close();
		}
	}


	/**
	 * 删除一条数据
	 * @param id id 如果为空  这删除表中所有数据
	 */
	public void deleteData(String id) {

		try {
			StringBuilder sql = new StringBuilder();
			if (id == null || "".equals(id)) {
				sql.append("delete from 皮尔丹顿");
			} else  {
				sql.append("delete from 皮尔丹顿 where id=").append("'").append(id).append("'");
			}
			mSqliteDatabase.execSQL(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mSqliteDatabase.close();
		}


	}
	

}
