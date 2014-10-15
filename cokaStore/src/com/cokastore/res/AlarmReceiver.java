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
		
		//����ݿ�q���B����جP�M�O���W��
		if ("play_notify".equals(data.get("msg"))) {
			
			//���U�U��alarm�q��
			cokaUtil = CokaUtil.getCokaUtil();
			Map config = DBAction.queryConfig(context);
			if ("Y".equals(config.get("notify"))) {
				// +24���]�w���骺����
				//�ɶ��j���� 00:00
				String time = config.get("time").toString();
				String[] timeSplit = time.split(":");
				//timeSplit[0] hour ; timeSplit[1] minute
				cokaUtil.setAlarmManager(context,Integer.parseInt(timeSplit[0]) + 24,Integer.parseInt(timeSplit[1]));
			}
			
/*			//�P�ҥ���O�_������Ĥ@�ѡA�������جP�M��
			Calendar calendar = Calendar.getInstance();
			if (calendar.get(Calendar.DAY_OF_MONTH) == 1 ) {
				birthList = DBAction.queryBirth(context);
			}*/
			
			birthList = DBAction.queryBirth(context);
			
			longTimeList = cokaUtil.getLongTimeList(context);
			
			//�N�����ܩ�e��
			if (birthList.size() > 0 || longTimeList.size() > 0) {
				//���notification
				cokaUtil.setNotification(context,birthList,longTimeList);
			}
		}
		
	}
	
}
