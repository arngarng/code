package com.cokastore.res;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmInitReceiver extends BroadcastReceiver {
	
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	List<Map<String,Object>> birthList,overdueList;
	private CokaUtil cokaUtil;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(ACTION)) {
			Log.i("cokaStoreActivity","開機完成");
			//註冊下次alarm通知
			cokaUtil = CokaUtil.getCokaUtil();
			Map config = DBAction.queryConfig(context);
			if ("Y".equals(config.get("notify"))) {
				//時間隔式為 00:00
				String time = config.get("time").toString();
				String[] timeSplit = time.split(":");
				//timeSplit[0] hour ; timeSplit[1] minute
				cokaUtil.setAlarmManager(context,Integer.parseInt(timeSplit[0]),Integer.parseInt(timeSplit[1]));
			}
		}
	}

}
