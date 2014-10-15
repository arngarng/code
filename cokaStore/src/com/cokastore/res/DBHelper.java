package com.cokastore.res;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static int _DBVersion = 3;
	private final static String _DBName = "cokastore.db";
	private final static String _TableName1 = "userData";
	private final static String _TableName2 = "systemConfig";
	private final static String _TableName3 = "codeData";
	private final static String _TableName4 = "consumerData";
	private static DBHelper mInstance = null;
	
	public DBHelper(Context context) {
		super(context, _DBName, null, _DBVersion);
		// TODO Auto-generated constructor stub
	}

	public static DBHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DBHelper(context);
		}
		return mInstance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		final String SQL1 = String
				.format("CREATE TABLE IF NOT EXISTS '%S' (_id INTEGER PRIMARY KEY AUTOINCREMENT, _NAME VARCHAR, _PHONE VARCHAR, _BIRTH VARCHAR, _MAIL VARCHAR, _PIC VARCHAR, _HABIT VARCHAR, _PS VARCHAR, _DATE VARCHAR, _PERIOD INTEGER, _FLAG INTEGER);",
						_TableName1);
		final String SQL2 = String
				.format("CREATE TABLE IF NOT EXISTS '%S' (_YN_NOTIFICATION VARCHAR, _YN_AUTO_TEXT VARCHAR, _NOTIFICATION_TIME VARCHAR, _BIRTH_TEXT VARCHAR, _LONGTIME_TEXT VARCHAR, _LOGIN_TIME VARCHAR);",
						_TableName2);
		final String SQL3 = String
				.format("CREATE TABLE IF NOT EXISTS '%S' (_VALUE INTEGER PRIMARY KEY AUTOINCREMENT, _CODE VARCHAR, _TITLE VARCHAR);",
						_TableName3);
		final String SQL4 = String
				.format("CREATE TABLE IF NOT EXISTS '%S' (_id INTEGER PRIMARY KEY AUTOINCREMENT, _USER_ID VARCHAR, _CONSUMER_NAME VARCHAR, _CONSUMER_DATE VARCHAR, _CONSUMER_CONTENT VARCHAR, _CONSUMER_DESCRIBE VARCHAR, _CONSUMER_PRICE VARCHAR, _CONSUMER_PIC VARCHAR);"
						, _TableName4);
		db.execSQL(SQL1);
		db.execSQL(SQL2);
		db.execSQL(SQL3);
		db.execSQL(SQL4);
		//初始值
		db.execSQL("INSERT INTO " + _TableName2 + " VALUES('N','N','12:00','您好，慶祝本月壽星，來店消費將有意想不到的驚喜!!','您好，歡迎來本店消費!!',2)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.beginTransaction();// 建立交易

			boolean success = false;// 判斷參數

			// 由之前不用的版本，可做不同的動作
			switch (oldVersion) {
			case 1:
				db.execSQL("DROP TABLE systemConfig");
				db.execSQL(String
						.format("CREATE TABLE IF NOT EXISTS '%S' (_YN_NOTIFICATION VARCHAR, _YN_AUTO_TEXT VARCHAR, _NOTIFICATION_TIME VARCHAR, _BIRTH_TEXT VARCHAR, _LONGTIME_TEXT VARCHAR);",
						_TableName2));
				db.execSQL("INSERT INTO " + _TableName2 + " VALUES('N','N','12:00','您好，慶祝本月壽星，來店消費將有意想不到的驚喜!!','您好，歡迎來本店消費!!')");
				oldVersion++;
				success = true;
				break;
			case 2:
				db.execSQL("ALTER TABLE systemConfig ADD COLUMN _LOGIN_TIME INTEGER");
				db.execSQL("UPDATE systemConfig set _LOGIN_TIME = 2");
				oldVersion++;
				success = true;
				break;
			case 3:
				oldVersion++;
				success = true;
				break;
			}

			if (success) {
				db.setTransactionSuccessful();// 正確交易才成功
			}
			db.endTransaction();
		} else {
			onCreate(db);
		}
		
	}
	
}
