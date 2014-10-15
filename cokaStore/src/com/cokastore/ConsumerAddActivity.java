package com.cokastore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cokastore.res.CokaUtil;
import com.cokastore.res.ConsumerAutoCompleteTextView;
import com.cokastore.res.ConsumerUserListAdapter;
import com.cokastore.res.DBAction;

public class ConsumerAddActivity extends Activity {

	private static final String image_file_loaction = "/tmp";
	private final String TAG = "ConsumerAddActivity";
	private final int VIEW = 1;
	private final int DELETE = 2;
	//開敵功能選單時的暫存ImageView
	private ImageView tmpImage;
	private int imageIndex = 0;
	private Uri imageUri;
	private String path = "";
	private ImageView imageView;
	private LinearLayout galleryLayout;
	private CokaUtil cokaUtil;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consumer_edit);
		galleryLayout = (LinearLayout)findViewById(R.id.phtoGallery);
		cokaUtil = CokaUtil.getCokaUtil();
		setting();
		if (getIntent().getExtras() != null) {
			Bundle params = getIntent().getExtras();
			((TextView) findViewById(R.id.consumerName)).setText(params.get("userName").toString());
            ((TextView) findViewById(R.id.consumerUserId)).setText(params.get("userId").toString());
		}
		  
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		File file = new File(getExternalFilesDir(null) + image_file_loaction);
		if (file.exists() && file.list().length > 0) {
			cokaUtil.deleteFile(file);
		}
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		// 1.查看 2.刪除
		menu.add(0, VIEW, 0, "查看");
		menu.add(0, DELETE, 1, "刪除");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case VIEW:
				imageView();
				break;
			case DELETE:
				imageDelete();
				break;
		}
		
		return false;
		
	}
	
	public void save(View view) {
		
		String name = ((TextView)findViewById(R.id.consumerName)).getText().toString();
		String userId = ((TextView)findViewById(R.id.consumerUserId)).getText().toString();
		String price =((TextView)findViewById(R.id.consumerPrice)).getText().toString();
		String date =((TextView)findViewById(R.id.consumerDate)).getText().toString();
		String content =((TextView)findViewById(R.id.consumerContent)).getText().toString();
		String describe =((TextView)findViewById(R.id.consumerDescribe)).getText().toString();
		
		if ("".equals(name.trim()) || "".equals(date.trim()) || "".equals(userId.trim())) {
			if ("".equals(userId.trim())) {
				Toast.makeText(this, "客戶姓名請從選單選取", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "客戶姓名和消費日期不可為空", Toast.LENGTH_SHORT).show();
			}
			
		} else {
			
			//存入DB
			long id = DBAction.addConsumer(this, userId, name, date, price, content,describe);
			
			//判斷是否有產生圖檔
			if (!"".equals(path)) {
				File file = new File(path);
				if (file.exists()) {
					String newPath = getExternalFilesDir(null) + "/consu/" + id;
					File newFile = new File(newPath);
					if (!newFile.exists()) {
						newFile.mkdirs();
					}
					cokaUtil.copyFile(file.getParentFile(),newPath);
					DBAction.updateConsumerPic(this,id,newPath);
				}
			}
			
			//更新user flag 為0
			DBAction.updateUserFlag(this,userId);
			
			finish();
		}

	}
	
	public void uploadPhoto(View view) {
		if (cokaUtil.isExtStorageWritable()) {
	        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        path = getExternalFilesDir(null) + image_file_loaction + "/" + (imageIndex++) + ".jpg";
	        File file = new File(path);
	        if (!file.getParentFile().exists()) {
	        	file.getParentFile().mkdirs();
	        }
	        imageUri = Uri.parse("file://" + path);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); 
	        startActivityForResult(intent, 0);
		} else {
			Toast.makeText(this, "外部儲存體無法寫檔", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void cancel(View view) {
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent data)
	   {
	     switch(requestCode) {
	     	case 0 :
	     		Log.d(TAG, "Photo taking :" + data);
	     		cropImageUri(imageUri, 400, 400, 1);
	     		break;
	     	case 1 :
	     		Log.d(TAG, "Photo cutting :" + data);
				ContentResolver cr = this.getContentResolver();
				Bitmap bitmap = null;
				try {
					bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
				} catch (FileNotFoundException e) {
						e.printStackTrace();
				}
				if (bitmap != null) {
					imageSetting(bitmap);
				}
				break;
	     }
	      super.onActivityResult(requestCode, resultCode, data);
	   }
	
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
	 	    Intent intent = new Intent("com.android.camera.action.CROP");
	 	    intent.setDataAndType(uri, "image/*");
	 	    intent.putExtra("crop", "true");
	 	    intent.putExtra("aspectX", 1);
	 	    intent.putExtra("aspectY", 1);
	 	    intent.putExtra("outputX", outputX);
	 	    intent.putExtra("outputY", outputY);
	 	    intent.putExtra("scale", true);
	 	    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	 	    intent.putExtra("return-data", false);
	 	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	 	    intent.putExtra("noFaceDetection", true); 
	 	    startActivityForResult(intent, requestCode);
	 	}
	
	public void setting() {
		final EditText consumerDate = (EditText)findViewById(R.id.consumerDate);
		cokaUtil.setDateView(this, consumerDate);
		
        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
 
                HashMap<String, Object> map = (HashMap<String, Object>) adapter.getAdapter().getItem(position);
 
                ((TextView) findViewById(R.id.consumerName)).setText(map.get("name").toString());
                ((TextView) findViewById(R.id.consumerUserId)).setText(map.get("id").toString());

            }
        };

        ConsumerAutoCompleteTextView autoText = (ConsumerAutoCompleteTextView) findViewById(R.id.consumerName);
		List<Map<String,Object>> userList = DBAction.userQuery(this, null);
		autoText.setOnItemClickListener(itemClickListener);
		autoText.setAdapter(new ConsumerUserListAdapter(this, userList));
	}
	
	public void imageSetting(Bitmap bitmap) {
		imageView = new ImageView(this);
		imageView.setImageBitmap(bitmap);
		imageView.setTag(path);
		galleryLayout.addView(imageView);
		LayoutParams params = imageView.getLayoutParams();
		params.height = (int) getResources().getDimension(R.dimen.imageview_height);
		params.width = (int) getResources().getDimension(R.dimen.imageview_width);
		//設定imageView刪除
		registerForContextMenu(imageView);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tmpImage = (ImageView)v;
				openContextMenu(v);
			}
		});
	}
	
	public void imageView() {
		View popView = getLayoutInflater().inflate(R.layout.photoimageview, null);
		Bitmap bitmap = BitmapFactory.decodeFile(tmpImage.getTag().toString());
		PopupWindow popupWindow = new PopupWindow(popView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setWidth(getWindowManager().getDefaultDisplay().getWidth());
		popupWindow.setHeight(getWindowManager().getDefaultDisplay().getWidth());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),bitmap));
		popupWindow.showAtLocation(tmpImage, Gravity.CENTER, 0, 0);
	}
	
	public void imageDelete() {
		//移除layout圖片
		galleryLayout.removeView(tmpImage);
		//刪除tmp空間的檔案
		File file = new File(tmpImage.getTag().toString());
		file.delete();
	}
}
