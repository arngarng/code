package com.cokastore;

import java.io.FileNotFoundException;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cokastore.res.CokaUtil;
import com.cokastore.res.DBAction;

public class UserViewActivity extends Activity {

	private ListView listView;
	private Map userMap;
	private String userId = "";
	private String picPath = "";
	private final String TAG = "UserViewActivity";
	private CokaUtil cokaUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_view);
		
		Bundle params = getIntent().getExtras();
		userMap = DBAction.userQueryById(this, params.getString("userId"));
		setting();
	}
	
	public void setting() {
		
		//欄位個人資料bind
		userId = userMap.get("id").toString();
		((TextView)findViewById(R.id.userName)).setText(userMap.get("name").toString());
		((TextView)findViewById(R.id.birth)).setText(userMap.get("birth").toString());
		((TextView)findViewById(R.id.mail)).setText(userMap.get("mail").toString());
		((TextView)findViewById(R.id.userHabit)).setText(userMap.get("habit").toString());
		((TextView)findViewById(R.id.userPs)).setText(userMap.get("ps").toString());
		((TextView)findViewById(R.id.period)).setText(userMap.get("period").toString());
		final TextView phone = (TextView)findViewById(R.id.userPhone);
		phone.setText(userMap.get("phone").toString());
		//設定電話播出
		phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phoneNumber = phone.getText().toString();
				Intent i = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + phoneNumber));
				startActivity(i);
			}
		});
		
		//設定圖片
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_dark_action_person);
		if (!"".equals(userMap.get("pic"))) {
			picPath = userMap.get("pic").toString();
			Uri pic = Uri.parse("file://" + picPath);
			ContentResolver cr = this.getContentResolver();
			try {
				bitmap = BitmapFactory.decodeStream(cr.openInputStream(pic));
			} catch (FileNotFoundException e) {
					e.printStackTrace();
			}
			
		}
		ImageView imageView = (ImageView)findViewById(R.id.photoView);
		imageView.setImageBitmap(bitmap);
		imageView.setTag(picPath);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewPhoto(v);				
			}
		});
		
		//消費記錄bind
		listView = (ListView)findViewById(R.id.user_consumerListView);
		listView.setAdapter(new SimpleAdapter(
				this
				, DBAction.consumerQueryByUserId(this,userId)
				,R.layout.user_consumerlist
				,new String[]{"id","date","content","price"}
				, new int[] {
				R.id.list_consumerId,R.id.list_consumerDate
				,R.id.list_consumerContent,R.id.list_consumerPrice }));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				Intent i = new Intent(getBaseContext(),ConsumerViewActivity.class);
				i.putExtra("consumerId", ((TextView)view.findViewById(R.id.list_consumerId)).getText().toString());
				startActivity(i);
			}
			
		});
	}
	
	public void viewPhoto(View view) {
		View popView = getLayoutInflater().inflate(R.layout.photoimageview, null);
		Bitmap bitmap = BitmapFactory.decodeFile((String)view.getTag());
		PopupWindow popupWindow = new PopupWindow(popView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setWidth(getWindowManager().getDefaultDisplay().getWidth());
		popupWindow.setHeight(getWindowManager().getDefaultDisplay().getWidth());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),bitmap));
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
	}
}
