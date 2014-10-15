package com.cokastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Map;

import com.cokastore.res.CokaUtil;
import com.cokastore.res.DBAction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserEditActivity extends Activity {

	private static final String image_file_loaction = "/tmp.jpg";
	private Map userMap;
	private String userId = "";
	private String picPath = "";
	private final String TAG = "UserEidtActivity";
	private Uri imageUri;
	private String path = "";
	private ImageView imageView;
	private CokaUtil cokaUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit);
		cokaUtil = CokaUtil.getCokaUtil();
		Bundle params = getIntent().getExtras();
		userMap = DBAction.userQueryById(this, params.getString("userId"));
		System.out.println(userMap);
		setting();
	}
	
	public void save(View view) {
		
		String name = ((TextView)findViewById(R.id.userName)).getText().toString();
		String phone =((TextView)findViewById(R.id.userPhone)).getText().toString();
		String birth =((TextView)findViewById(R.id.birth)).getText().toString();
		String mail =((TextView)findViewById(R.id.mail)).getText().toString();
		String habit =((TextView)findViewById(R.id.userHabit)).getText().toString();
		String ps =((TextView)findViewById(R.id.userPs)).getText().toString();
		String period =((TextView)findViewById(R.id.period)).getText().toString();
		
		if ("".equals(name.trim()) || "".equals(phone.trim())) {
			
			Toast.makeText(this, "姓名和手機不可為空", Toast.LENGTH_SHORT).show();
			
		} else {
			
			if (!"".equals(path)) {
				File file = new File(path);
				if (file.exists()) {
					picPath = file.getParent() + "/usr/" + userId + ".jpg";
					File newFile = new File(picPath);
					if (!newFile.getParentFile().exists()) {
						newFile.getParentFile().mkdirs();
					}
					cokaUtil.copyFile(file,picPath);
				}
			}
			
			//存入DB
			long id = DBAction.updateUser(this,userId, name, phone, birth, mail, habit, ps, picPath,Integer.parseInt(period));
			
			//判斷是否有產生圖檔
			
			finish();
		}

	}
	
	public void uploadPhoto(View view) {
		if (cokaUtil.isExtStorageWritable()) {
	        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        path = getExternalFilesDir(null) + image_file_loaction;
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
					imageView = (ImageView) findViewById(R.id.photoView);
					imageView.setImageBitmap(bitmap);
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
		
		//欄位bind
		userId = userMap.get("id").toString();
		((TextView)findViewById(R.id.userName)).setText(userMap.get("name").toString());
		((TextView)findViewById(R.id.userPhone)).setText(userMap.get("phone").toString());
		((TextView)findViewById(R.id.birth)).setText(userMap.get("birth").toString());
		((TextView)findViewById(R.id.mail)).setText(userMap.get("mail").toString());
		((TextView)findViewById(R.id.userHabit)).setText(userMap.get("habit").toString());
		((TextView)findViewById(R.id.userPs)).setText(userMap.get("ps").toString());
		((TextView)findViewById(R.id.period)).setText(userMap.get("period").toString());
		if (!"".equals(userMap.get("pic"))) {
			picPath = userMap.get("pic").toString();
			Uri pic = Uri.parse("file://" + picPath);
			ContentResolver cr = this.getContentResolver();
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(cr.openInputStream(pic));
			} catch (FileNotFoundException e) {
					e.printStackTrace();
			}
			((ImageView)findViewById(R.id.photoView)).setImageBitmap(bitmap);
		}
		
		
		
		final EditText birth = (EditText)findViewById(R.id.birth);
		cokaUtil.setDateView(this, birth);
	}
}
