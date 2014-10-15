package com.cokastore.res;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAction extends Activity {
	
	static private DBHelper dh = null;
	final private static String DB1 = "userData";
	final private static String DB2 = "systemConfig";
	final private static String DB3 = "codeData";
	final private static String DB4 = "consumerData";
	final private static String[] DB1_Column = new String[]{"_id","_NAME","_PHONE","_BIRTH","_MAIL","_PIC","_HABIT","_PS","_PERIOD"};
	final private static String[] DB2_Column = new String[]{"_BIRTH_TEXT","_LONGTIME_TEXT","_YN_NOTIFICATION","_YN_AUTO_TEXT","_NOTIFICATION_TIME","_LOGIN_TIME"};
	final private static String[] DB3_Column = new String[]{"_TITLE","_VALUE","_CODE"};
	final private static String[] DB4_Column = new String[]{"_id","_USER_ID","_CONSUMER_NAME","_CONSUMER_DATE"
		,"_CONSUMER_CONTENT","_CONSUMER_DESCRIBE","_CONSUMER_PRICE","_CONSUMER_PIC"};
	
	public static long addUser(Context context ,String name,String phone,String birth,String mail,String habit,String ps,int period) {
		DBHelper.getInstance(context);
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_NAME", name);
		values.put("_PHONE", phone);
		values.put("_DATE", dateTime());
		values.put("_BIRTH", birth);
		values.put("_MAIL", mail);
		values.put("_HABIT", habit);
		values.put("_PIC", "");
		values.put("_PS", ps);
		values.put("_PERIOD", period);
		values.put("_FLAG", 0);
		long id = db.insert(DB1, null, values);
		return id;
	}
	
	public static long addConsumer(Context context ,String userId ,String name,String date,String price,String content,String describe) {
		
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_USER_ID", userId);
		values.put("_CONSUMER_NAME", name);
		values.put("_CONSUMER_DATE", date);
		values.put("_CONSUMER_CONTENT", content);
		values.put("_CONSUMER_DESCRIBE", describe);
		values.put("_CONSUMER_PRICE", price);
		values.put("_CONSUMER_PIC", "");
		long id = db.insert(DB4, null, values);
		return id;
	}
	
	public static List<Map<String,Object>> userQuery(Context context,String condition) {
		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
		Map<String,Object> userMap;
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		//查詢清單名稱
		Cursor cursor = db.rawQuery("SELECT t1._id,_NAME,_PHONE,_PIC,t2._CONSUMER_DATE FROM userData t1 " 
				+ "LEFT JOIN (select max(_CONSUMER_DATE) as _CONSUMER_DATE,_USER_ID FROM consumerData group by _USER_ID ) t2 ON t1._id = t2._USER_ID"
				+ " ORDER BY t1._id", null);
		while (cursor.moveToNext()) {
			userMap = new HashMap<String,Object>();
			userMap.put("id", cursor.getInt(0));
			userMap.put("name",cursor.getString(1));
			userMap.put("phone",cursor.getString(2));
			userMap.put("pic",cursor.getString(3));
			userMap.put("date",cursor.getString(4));
			userList.add(userMap);
		}
		
		return userList;		
	}
	
	public static List<Map<String,Object>> consumerQuery(Context context,String condition) {
		List<Map<String,Object>> consumerList = new ArrayList<Map<String,Object>>();
		Map<String,Object> consumerMap;
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		//查詢清單名稱
		Cursor cursor = db.query(DB4, DB4_Column, null, null, null, null, "_id desc");
		while (cursor.moveToNext()) {
			consumerMap = new HashMap<String,Object>();
			consumerMap.put("id", cursor.getInt(0));
			consumerMap.put("name",cursor.getString(2));
			consumerMap.put("date",cursor.getString(3));
			consumerMap.put("content",cursor.getString(4));
			consumerMap.put("price",cursor.getString(6));
			consumerList.add(consumerMap);
		}
		
		return consumerList;		
	}
	
	public static Map<String,Object> userQueryById(Context context,String id) {
		Map<String,Object> userMap = new HashMap<String,Object>();
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		//查詢清單名稱
		Cursor cursor = db.query(DB1, DB1_Column, "_id=" + id, null, null, null, "_id asc");
		while (cursor.moveToNext()) {
			userMap = new HashMap<String,Object>();
			userMap.put("id", cursor.getInt(0));
			userMap.put("name",cursor.getString(1));
			userMap.put("phone",cursor.getString(2));
			userMap.put("birth",cursor.getString(3));
			userMap.put("mail",cursor.getString(4));
			userMap.put("pic",cursor.getString(5));
			userMap.put("habit",cursor.getString(6));
			userMap.put("ps",cursor.getString(7));
			userMap.put("period",cursor.getInt(8));
		}
		
		return userMap;		
	}
	
	public static Map<String,Object> consumerQueryById(Context context,String id) {
		Map<String,Object> userMap = null;
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		//查詢清單名稱
		Cursor cursor = db.query(DB4, DB4_Column, "_id=" + id, null, null, null, "_id asc");
		while (cursor.moveToNext()) {
			userMap = new HashMap<String,Object>();
			userMap.put("id", cursor.getInt(0));
			userMap.put("name",cursor.getString(2));
			userMap.put("date",cursor.getString(3));
			userMap.put("content",cursor.getString(4));
			userMap.put("describe",cursor.getString(5));
			userMap.put("price",cursor.getString(6));
			userMap.put("pic",cursor.getString(7));
		}
		
		return userMap;		
	}
	
	public static List<Map<String,Object>> consumerQueryByUserId(Context context,String id) {
		List<Map<String,Object>> consumerList = new ArrayList<Map<String,Object>>();
		Map<String,Object> consumerMap;
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		//查詢清單名稱
		Cursor cursor = db.rawQuery("SELECT t1._id,_CONSUMER_DATE,_CONSUMER_CONTENT,_CONSUMER_PRICE FROM consumerData t1 LEFT JOIN userData t2 ON t1._USER_ID = t2._id where t2._id =" + id + " ORDER BY t1._id DESC", null);
		while (cursor.moveToNext()) {
			consumerMap = new HashMap<String,Object>();
			consumerMap.put("id", cursor.getInt(0));
			consumerMap.put("date",cursor.getString(1));
			consumerMap.put("content",cursor.getString(2));
			consumerMap.put("price",cursor.getString(3));
			consumerList.add(consumerMap);
		}
		
		return consumerList;		
	}
	
	public static int updateUser(Context context, String id, String name, String phone, String birth, String mail, String habit, String ps, String pic, int period) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_NAME", name);
		values.put("_PHONE", phone);
		values.put("_BIRTH", birth);
		values.put("_MAIL", mail);
		values.put("_HABIT", habit);
		values.put("_PS", ps);
		values.put("_PIC", pic);
		values.put("_PERIOD", period);
		return db.update(DB1, values, "_id=" + id, null);
	}
	
	public static int updateUserPic(Context context, long id, String path) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_PIC", path);
		return db.update(DB1, values, "_id=" + id, null);
	}
	
	public static int  updateUserFlag(Context context, String id) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_FLAG", 0);
		return db.update(DB1, values, "_id=" + id, null);
	}
	
	public static int updateConsumer(Context context, String id, String date, String content, String describe, String price, String path) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_CONSUMER_DATE", date);
		values.put("_CONSUMER_CONTENT", content);
		values.put("_CONSUMER_DESCRIBE", describe);
		values.put("_CONSUMER_PRICE", price);
		values.put("_CONSUMER_PIC", path);
		return db.update(DB4, values, "_id=" + id, null);
	}
	
	public static int  updateConsumerPic(Context context, long id, String path) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_CONSUMER_PIC", path);
		return db.update(DB4, values, "_id=" + id, null);
	}
	
	public static void updateUserFlag(Context context, String id, int type) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		db.execSQL("UPDATE userData SET _FLAG = _FLAG +" + type + " WHERE _id = '" + id + "'");
		
	}
	
	public static int updateConfig(Context context, String ynNotify, String time, String birthText, String longTimeText, int loginTime) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_YN_NOTIFICATION", ynNotify);
		values.put("_NOTIFICATION_TIME", time);
		values.put("_BIRTH_TEXT", birthText);
		values.put("_LONGTIME_TEXT", longTimeText);
		values.put("_LOGIN_TIME", loginTime);
		return db.update(DB2, values, null, null);
		
	}
	
	public static void userDelete (Context context,String userId) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		db.delete(DB1, "_id =" + userId, null);
	}
	
	public static void consumerDelete (Context context,String consumerId) {
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		db.delete(DB4, "_id =" + consumerId, null);
	}
	
	//查詢設定檔資料
	public static Map queryConfig(Context context) {
		Map<String,Object> configMap = null;
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		//查詢清單名稱
		Cursor cursor = db.query(DB2, DB2_Column, null, null, null, null,null);
		while (cursor.moveToNext()) {
			configMap = new HashMap<String,Object>();
			configMap.put("BirthText", cursor.getString(0));
			configMap.put("LongTimeText", cursor.getString(1));
			configMap.put("notify", cursor.getString(2));
			configMap.put("auto",cursor.getString(3));
			configMap.put("time",cursor.getString(4));
			configMap.put("loginTime", cursor.getInt(5));
		}
		
		return configMap;
	}
	
	//提醒待辦-本月壽星
	public static List<Map<String,Object>> queryBirth(Context context) {
		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
		Map<String,Object> userMap;
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		//查詢清單名稱
		Cursor cursor = db.rawQuery("SELECT t1._id,_NAME,_PHONE,_BIRTH,t2._CONSUMER_DATE FROM userData t1 " 
				+ "LEFT JOIN (select max(_CONSUMER_DATE) as _CONSUMER_DATE,_USER_ID FROM consumerData group by _USER_ID ) t2 ON t1._id = t2._USER_ID"
				+ " WHERE substr(t1._BIRTH,6,2) = '" + dateTime().substring(4,6) + "' ORDER BY t1._BIRTH ASC", null);
		while (cursor.moveToNext()) {
			userMap = new HashMap<String,Object>();
			userMap.put("id", cursor.getInt(0));
			userMap.put("name",cursor.getString(1));
			userMap.put("phone",cursor.getString(2));
			userMap.put("birth",cursor.getString(3));
			userMap.put("date",cursor.getString(4));
			userList.add(userMap);
		}
		
		return userList;		
	}
	//提醒待辦-逾期名單
	public static List<Map<String,Object>> queryLongTime(Context context) {
		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
		Map<String,Object> userMap;
		dh = DBHelper.getInstance(context);
		SQLiteDatabase db = dh.getReadableDatabase();
		//查詢清單名稱
		Cursor cursor = db.rawQuery("SELECT t1._id,_NAME,_PHONE,_PERIOD,t2._CONSUMER_DATE,_FLAG FROM userData t1 " 
				+ "LEFT JOIN (select max(_CONSUMER_DATE) as _CONSUMER_DATE,_USER_ID FROM consumerData group by _USER_ID ) t2 ON t1._id = t2._USER_ID"
				+ " WHERE _FLAG = 0 ORDER BY _CONSUMER_DATE ASC", null);
		while (cursor.moveToNext()) {
			userMap = new HashMap<String,Object>();
			userMap.put("id", cursor.getInt(0));
			userMap.put("name",cursor.getString(1));
			userMap.put("phone",cursor.getString(2));
			userMap.put("period",cursor.getString(3));
			userMap.put("date",cursor.getString(4));
			userMap.put("flag",cursor.getInt(5));
			userList.add(userMap);
		}
		
		return userList;		
	}
	public static String dateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sdf.format(new Date());
		return str;
	}
	
	public static int pattern(String str) {
		Pattern PhonePattern = Pattern.compile("[0-9]*");
		Pattern DatePattern = Pattern.compile("[0-9]*\\/?[0-9]*\\/?[0-9]*");
		if (str == null || "".equals(str.trim())) {
			return 0;
		} else if (PhonePattern.matcher(str).matches()) {
			return 1;
		} else if (DatePattern.matcher(str).matches()){
			return 2;
		} else {
			return 3;
		}
	}
	
}
