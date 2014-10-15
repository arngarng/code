package com.cokastore.splash;

import java.io.File;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.cokastore.AlarmNotifyActivity;
import com.cokastore.MainActivity;
import com.cokastore.R;
import com.cokastore.res.CokaUtil;
import com.cokastore.res.DBAction;
import com.cokastore.res.DBHelper;

public class SplashScreen extends Activity {

	//Splash screen timer
	private int SPLASH_TIME_OUT = 0;
	private DBHelper dbh = null;
	private ImageView logo;
	private final String logoPath = "/logo.jpg";
	private CokaUtil cokaUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		cokaUtil = CokaUtil.getCokaUtil();
		
		//讀DB取得延遲時間
		Map<String,Object> config = DBAction.queryConfig(this);
		SPLASH_TIME_OUT = Integer.parseInt(config.get("loginTime").toString());
		
		//設定logo
		logo = (ImageView)findViewById(R.id.splashImage);
		if (cokaUtil.isExtStorageWritable()) {
			String path = getExternalFilesDir(null) + logoPath;
			if (new File(path).exists()) {
				cokaUtil.setPhoto(path, logo, this);
			}
		}
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				
				Intent i = new Intent(SplashScreen.this, AlarmNotifyActivity.class);
				startActivity(i);
				
				finish();
				
			}
			
		},SPLASH_TIME_OUT * 1000);
	}
}
