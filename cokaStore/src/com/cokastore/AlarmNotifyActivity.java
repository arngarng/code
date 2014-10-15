package com.cokastore;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.graphics.Color;

import com.cokastore.res.CokaUtil;
import com.cokastore.res.DBAction;



public class AlarmNotifyActivity extends Activity {

	private List<Map<String,Object>> birthList;
	private List<Map<String,Object>> longTimeList;
	private ListView birthLv,longTimeLv;
	private CokaUtil cokaUtil;
	private final int VIEW = 0;
	private final int NOTIFY = 1;
	private View userView;
	private String userId = "";
	private String userName = "";
	private String userPhone = "";
	private String notifyType = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_list);
		cokaUtil = CokaUtil.getCokaUtil();
		setting();
		
		//����notification
		cokaUtil.cancelNotification(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		terminate();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// 1.�d�� 2.�q��
		menu.add(0, VIEW, 0, "�d��");
		menu.add(0, NOTIFY, 1, "�q��");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case VIEW:
				viewUser();
				break;
			case NOTIFY:
				//²�T�q��
				notifyUser();
				break;
		}
		
		return false;
		
	}
	
	OnItemClickListener onClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			userId = ((TextView)view.findViewById(R.id.list_notifyId)).getText().toString();
			userName = ((TextView)view.findViewById(R.id.list_notifyName)).getText().toString();
			userPhone = ((TextView)view.findViewById(R.id.list_notifyPhone)).getText().toString();
			userView = view;
			notifyType = adapter.getTag().toString();
			openContextMenu(view);
		}
	};
	
	public void viewUser() {
		//�d��user��� UserViewActivity
		Intent intent = new Intent(this, UserViewActivity.class);
		intent.putExtra("userId", userId);
		startActivity(intent);
	}
	
	public void notifyUser() {
		//���o����A�H�o²�T
		cokaUtil.sendText(this, userId, userName, userPhone, notifyType);
		//�ק惡item���I���C��
		userView.setBackgroundColor(Color.GRAY);
		
		Toast.makeText(this, "�w�q��" + userName, Toast.LENGTH_SHORT).show();
	}
	
	public void confirm(View view) {
		terminate();
	}
	
	public void setting() {
		//�]�wlistView�Badapter
		birthLv = (ListView)findViewById(R.id.monthBirth);
		longTimeLv = (ListView)findViewById(R.id.longTime);
		//�d�ߥͤ�M��
		birthList = DBAction.queryBirth(this);
		birthLv.setAdapter(
				new SimpleAdapter(this
						, birthList
						, R.layout.notify_list
						, new String[]{"id","name","phone","birth","date"}
						, new int[]{R.id.list_notifyId,R.id.list_notifyName
							,R.id.list_notifyPhone,R.id.list_notifyType
							,R.id.list_notifyRecentDate}
				));
		birthLv.setOnItemClickListener(onClickListener);
		birthLv.setTag("BIRTH");
		//�d�ߦn�[�����M��
		longTimeList = cokaUtil.getLongTimeList(this);
		longTimeLv.setAdapter(
				new SimpleAdapter(this
						, longTimeList
						, R.layout.notify_list
						, new String[]{"id","name","phone","period","date"}
						, new int[]{R.id.list_notifyId,R.id.list_notifyName
								,R.id.list_notifyPhone,R.id.list_notifyType
								,R.id.list_notifyRecentDate}
				));
		longTimeLv.setOnItemClickListener(onClickListener);
		longTimeLv.setTag("LONGTIME");
		registerForContextMenu(longTimeLv);
		registerForContextMenu(birthLv);
	}
	
	public void terminate() {

		//activity�|���ҰʡA�h�}��
		if (!cokaUtil.activityState(this)) {
			//�ɨ�D�e��
			Intent i = new Intent(AlarmNotifyActivity.this, MainActivity.class);
			startActivity(i);
		}
		finish();
	}
}
