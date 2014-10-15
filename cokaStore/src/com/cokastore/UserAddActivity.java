package com.cokastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

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

public class UserAddActivity extends Activity {

	private static final String image_file_loaction = "/tmp.jpg";
	private final String TAG = "UserAddActivity";
	private Uri imageUri;
	private String path = "";
	private ImageView imageView;
	private CokaUtil cokaUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit);
		cokaUtil = CokaUtil.getCokaUtil();
		final EditText birth = (EditText)findViewById(R.id.birth);
		cokaUtil.setDateView(this, birth);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		File file = new File(getExternalFilesDir(null) + image_file_loaction);
		if (file.exists()) {
			cokaUtil.deleteFile(file);
		}
		
	}
	
	public void save(View view) {
		
		String name = ((TextView)findViewById(R.id.userName)).getText().toString();
		String phone =((TextView)findViewById(R.id.userPhone)).getText().toString();
		String birth =((TextView)findViewById(R.id.birth)).getText().toString();
		String mail =((TextView)findViewById(R.id.mail)).getText().toString();
		String habit =((TextView)findViewById(R.id.userHabit)).getText().toString();
		String ps =((TextView)findViewById(R.id.userPs)).getText().toString();
		String period = ((TextView)findViewById(R.id.period)).getText().toString();
		
		if ("".equals(name.trim()) || "".equals(phone.trim())) {
			
			Toast.makeText(this, "姓名和手機不可為空", Toast.LENGTH_SHORT).show();
			
		} else {
			
			//存入DB
			long id = DBAction.addUser(this, name, phone, birth, mail,habit,ps,Integer.parseInt(period));
			
			//判斷是否有產生圖檔
			if (!"".equals(path)) {
				File file = new File(path);
				if (file.exists()) {
					String newPath = file.getParent() + "/usr/" + id + ".jpg";
					File newFile = new File(newPath);
					if (!newFile.getParentFile().exists()) {
						newFile.getParentFile().mkdirs();
					}
					cokaUtil.copyFile(file,newPath);
					DBAction.updateUserPic(this,id,newPath);
				}
			}
			
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
		System.out.println("onActivity");
	     switch(requestCode) {
	     	case 0 :
	     		Log.d(TAG, "Photo taking :" + data);
		        File file = new File(path);
		        if (file.exists()) {
		        	cropImageUri(imageUri, 400, 400, 1);
		        }
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
	
}
