package com.cokastore.res;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.cokastore.R;
import com.cokastore.splash.SplashScreen;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	
	private List<Map<String,Object>> birthList = new ArrayList<Map<String,Object>>();;
	private List<Map<String,Object>> longTimeList = new ArrayList<Map<String,Object>>();;
	private CokaUtil cokaUtil;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i("onReceive", context.toString());
		
		Bundle data = intent.getExtras();
		
		//執行待辦通知、本月壽星和逾期名單
		if ("play_notify".equals(data.get("msg"))) {
			
			//註冊下次alarm通知
			cokaUtil = CokaUtil.getCokaUtil();
			Map config = DBAction.queryConfig(context);
			if ("Y".equals(config.get("notify"))) {
				// +24為設定明日的提醒
				//時間隔式為 00:00
				String time = config.get("time").toString();
				String[] timeSplit = time.split(":");
				//timeSplit[0] hour ; timeSplit[1] minute
				cokaUtil.setAlarmManager(context,Integer.parseInt(timeSplit[0]) + 24,Integer.parseInt(timeSplit[1]));
			}
			
/*			//判所本日是否為本月第一天，抓取本月壽星清單
			Calendar calendar = Calendar.getInstance();
			if (calendar.get(Calendar.DAY_OF_MONTH) == 1 ) {
				birthList = DBAction.queryBirth(context);
			}*/
			
			birthList = DBAction.queryBirth(context);
			
			longTimeList = cokaUtil.getLongTimeList(context);
			
			//將資料顯示於畫面
			if (birthList.size() > 0 || longTimeList.size() > 0) {
				//改動notification
				cokaUtil.setNotification(context,birthList,longTimeList);
			}
		}
		
	}
	
}
