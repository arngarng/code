package com.cokastore;

import java.io.File;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cokastore.res.DBAction;

public class ConsumerViewActivity extends Activity {

	private Map userMap;
	private String userId = "";
	private String picPath = "";
	private LinearLayout galleryLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consumer_view);
		Bundle params = getIntent().getExtras();
		userMap = DBAction.consumerQueryById(this, params.getString("consumerId"));
		
		setting();
		  
	}
	
	public void setting() {
		galleryLayout = (LinearLayout)findViewById(R.id.phtoGallery);
		//欄位個人資料bind
		((TextView)findViewById(R.id.consumerDate)).setText(userMap.get("date").toString());
		((TextView)findViewById(R.id.consumerPrice)).setText(userMap.get("price").toString());
		((TextView)findViewById(R.id.consumerDescribe)).setText(userMap.get("describe").toString());
		((TextView)findViewById(R.id.consumerName)).setText(userMap.get("name").toString());
		((TextView)findViewById(R.id.consumerContent)).setText(userMap.get("content").toString());
		
		//讀圖
		if (!"".equals(userMap.get("pic").toString())) {
			File file = new File(userMap.get("pic").toString());
			if (file.isDirectory() && file.listFiles().length > 0) {
				for (File image : file.listFiles()) {
					imageSetting(image);
				}
			}
		}
	}
	
	public void imageSetting(File image) {
		Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(bitmap);
		imageView.setTag(image.getPath());
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
		
		galleryLayout.addView(imageView);
		LayoutParams params = imageView.getLayoutParams();
		params.height = (int) getResources().getDimension(R.dimen.imageview_height);
		params.width = (int) getResources().getDimension(R.dimen.imageview_width);
	}
	
	public void showPopupWindow(View view) {
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
