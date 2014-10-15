package com.cokastore.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.cokastore.ConsumerAddActivity;
import com.cokastore.MainActivity;
import com.cokastore.R;
import com.cokastore.splash.SplashScreen;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification.Builder;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;

public class CokaUtil extends Activity {
	
	private static CokaUtil cokaUtil = null;
	private Calendar today;
	private final int NOTIFICATIONID = 86888;
	
	public static CokaUtil getCokaUtil() {
		if (cokaUtil == null) {
			cokaUtil = new CokaUtil();
		}
		return cokaUtil;
	}
	
	public void setAlarmManager(Context context , int hour , int minute) {
		//app onCreate時，設定喚醒時間為隔日的系繰設定時間
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, hour >= 24 ? 1 : 0);
		calendar.set(Calendar.HOUR_OF_DAY, hour >= 24 ? (hour-24) : hour);
		calendar.set(Calendar.MINUTE,minute);
		calendar.set(Calendar.SECOND,0);
		Intent i = new Intent(context,AlarmReceiver.class);
		i.putExtra("msg", "play_notify");
		//FLAG_UPDATE_CURRENT 如果已存在 PendingIntent, 則更新 extra data.
		PendingIntent pi = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_ONE_SHOT);
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
	}
	
	public void cancelAlarmManager(Context context) {
		
		Intent i = new Intent(context,AlarmReceiver.class);

		//receiver class 與傳給 PendingIntent 的 request code相同
		PendingIntent pi = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pi);
	}
	
	public void setNotification(Context context , List<Map<String,Object>> birthList , List<Map<String,Object>> longTimeList) {
		//建立Notification物件
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		Builder builder = new Builder(context);
		PendingIntent contentIndent = PendingIntent.getActivity(context, 0, new Intent(context,SplashScreen.class),PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIndent)
			.setSmallIcon(R.drawable.ic_launcher)
			.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher))
			.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
			.setLights(0xff00ff00, 300, 100)
			.setTicker("CokaStore訊息通知")
			.setWhen(System.currentTimeMillis())
			.setContentTitle("CokaStore訊息通知")
			.setContentText("本月壽星("+birthList.size()+")，久未光顧("+longTimeList.size()+")");
		Notification notification = builder.getNotification();
		//點擊即關閉通知
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NOTIFICATIONID, notification);
	}
	
	public void cancelNotification(Context context) {
		NotificationManager notMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notMgr.cancel(NOTIFICATIONID);
	}
	
	public void setTimeView(final Context context , final EditText time ) {
		time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				if (hasFocus) {
					String alarmTime = time.getText().toString();
					int alarmHour,alarmMinute;
					if (alarmTime.length() < 5) {
						alarmHour = 12;
						alarmMinute = 00;
					} else {
						String[] alarmTimeSplit = alarmTime.split(":");
						alarmHour = Integer.parseInt(alarmTimeSplit[0]);
						alarmMinute = Integer.parseInt(alarmTimeSplit[1]);
					}
					new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
						
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							NumberFormat nf = new DecimalFormat("00");
                        	time.setText(nf.format(hourOfDay) + ":" + nf.format(minute));  
							
						}
					}, alarmHour, alarmMinute, true).show();
					
				}
				
			}
		});
		
		time.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String alarmTime = time.getText().toString();
				int alarmHour,alarmMinute;
				if (alarmTime.length() < 5) {
					alarmHour = 12;
					alarmMinute = 00;
				} else {
					String[] alarmTimeSplit = alarmTime.split(":");
					alarmHour = Integer.parseInt(alarmTimeSplit[0]);
					alarmMinute = Integer.parseInt(alarmTimeSplit[1]);
				}
				new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						NumberFormat nf = new DecimalFormat("00");
                    	time.setText(nf.format(hourOfDay) + ":" + nf.format(minute));  
						
					}
				}, alarmHour, alarmMinute, true).show();
			}
		});
	}
		
	public void setDateView(final Context context , final EditText date) {
		date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if(hasFocus){  
					String birthDay = date.getText().toString();
					int year,month,day;
                    if (!"".equals(birthDay)) {
                    	String[] birthDaySplit = birthDay.split("/");
                    	year = Integer.parseInt(birthDaySplit[0]);
                    	month = Integer.parseInt(birthDaySplit[1])-1;
                    	day = Integer.parseInt(birthDaySplit[2]);
                    } else {
                    	Calendar c = Calendar.getInstance();
                    	year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH);
                        day = c.get(Calendar.DAY_OF_MONTH);
                    }
					
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {  
                          
                        @Override  
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
                            // TODO Auto-generated method stub  
                        	NumberFormat nf = new DecimalFormat("00");
                        	date.setText(year+"/"+nf.format((monthOfYear+1))+"/"+nf.format(dayOfMonth));  
                        }  
                    }, year, month, day).show();  
                 
                }  
			}
		});
		
		date.setOnClickListener(new View.OnClickListener() {  
            
            @Override  
            public void onClick(View v) {  
            	String birthDay = date.getText().toString();
				int year,month,day;
                if (!"".equals(birthDay)) {
                	String[] birthDaySplit = birthDay.split("/");
                	year = Integer.parseInt(birthDaySplit[0]);
                	month = Integer.parseInt(birthDaySplit[1])-1;
                	day = Integer.parseInt(birthDaySplit[2]);
                } else {
                	Calendar c = Calendar.getInstance();
                	year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                }  
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {  
                      
                    @Override  
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
                        // TODO Auto-generated method stub  
                    	NumberFormat nf = new DecimalFormat("00");
                    	date.setText(year + "/" + nf.format((monthOfYear+1)) + "/" + nf.format(dayOfMonth));  
                    }  
                }, year, month, day).show();  
             
            }  
        });
	}
	
	public void copyFile(File src, String dstPath) {
	    InputStream in;
	    OutputStream out;
		try {
			if (src.isDirectory()) {
				for (File tmpFile : src.listFiles()) {
					File destFile = new File(dstPath + "/" + tmpFile.getName());
					if (destFile.exists()) {
						int index = Integer.parseInt(tmpFile.getName().substring(0,tmpFile.getName().indexOf(".")));
						do {
							index++;
						} while ((destFile = new File(dstPath + "/" + index + ".jpg")).exists());
					}
					in = new FileInputStream(tmpFile);
					out = new FileOutputStream(destFile);
				    byte[] buf = new byte[1024];
				    int len;
				    while ((len = in.read(buf)) > 0) {
				        out.write(buf, 0, len);
				    }
				    in.close();
				    out.close();
				}
			} else {
				in = new FileInputStream(src);
				out = new FileOutputStream(dstPath);
			    byte[] buf = new byte[1024];
			    int len;
			    while ((len = in.read(buf)) > 0) {
			        out.write(buf, 0, len);
			    }
			    in.close();
			    out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getLongTimeList(Context context) {
		
		today = Calendar.getInstance();
		
		List<Map<String,Object>> overdueList = new ArrayList<Map<String,Object>>();
		//逾期名單，先抓取全部資料再做運算
		List<Map<String,Object>> list = DBAction.queryLongTime(context);
		for(Map<String,Object> map : list) {
			//逾期判斷:如超出期間以及未做過寄送標記
			if (overDue(map.get("date").toString(),Integer.parseInt(map.get("period").toString()))
					&& !"1".equals(map.get("flag").toString())) {
				overdueList.add(map);
			}
		}
		return overdueList;
	}
	
	public boolean overDue(String date,int period) {
		
		try {
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Calendar date_start = Calendar.getInstance();
			date_start.setTime(sdf.parse(date));
			int diffYear = (today.get(Calendar.YEAR) - date_start.get(Calendar.YEAR)) * 365;
			int diffDay = today.get(Calendar.DAY_OF_YEAR) - date_start.get(Calendar.DAY_OF_YEAR);
			
			//逾期
			if ((diffYear + diffDay) > period * 7 ) {
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		return false;
	}
	
	public void sendText(Context context,String id,String name,String phone,String type) {

		//簡訊內容
		String content = name + " ";
		
		Map<String,Object> config = DBAction.queryConfig(context);
		
		if("BIRTH".equals(type)) {
			//抓取生日簡訊預設內容	
			content += config.get("BirthText").toString();
			
			//修改狀態
			DBAction.updateUserFlag(context, id, 2);
		} else if ("LONGTIME".equals(type)) {
			//抓取通知來店簡訊預設內容
			content += config.get("LongTimeText").toString();
			
			//修改狀態
			DBAction.updateUserFlag(context, id, 1);
		}
		
		Log.i("SEND TEXT", content);
		
		//寄發簡訊
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
		smsManager.sendTextMessage(phone, null, content, pendingIntent, null);
	}
	
	public void showPopupWindow(Context context , View view) {
		View popView = ((Activity) context).getLayoutInflater().inflate(R.layout.photoimageview, null);
		ImageView imgV = (ImageView)popView.findViewById(R.id.photoImageView);
		setPhoto((String)view.getTag(),imgV,context);
		PopupWindow popupWindow = new PopupWindow(popView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		//退出popWindow
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
	}
	
	public void setPhoto(String path, ImageView image, Context context) {
		int windowW = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
		int windowH = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
		int targetW = image.getWidth() == 0 ? windowW : image.getWidth();
		int targetH = image.getHeight() == 0 ? windowH : image.getHeight();
		//Get dimensions
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int imageH = bmOptions.outHeight;
		int imageW = bmOptions.outWidth;
		//Determine how much to scale down the image
		int scale = Math.max(imageH/targetH, imageW/targetW);
		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scale;
		bmOptions.inPurgeable = true;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
		image.setImageBitmap(bitmap);
		image.setTag(path);
	}
	
	public boolean activityState(Context context) {
		boolean isRunning = false;
		Intent intent = new Intent(context,MainActivity.class);
		ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
		//activity 已存在系統中
		if (cmpName != null) {
			ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
			List<RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
			
			for (RunningTaskInfo taskInfo : taskInfoList) {
				//activity 已啟動
				if (taskInfo.baseActivity.equals(cmpName)) {
					isRunning = true;
				}
			}
		}
		
		return isRunning;
	}
	
	public String getRealImagePathFromURI(Uri contentURI,Context context) {
		Cursor cursor = context.getContentResolver().query(contentURI, null,
				null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		String name = cursor.getString(idx);
		cursor.close();
		return name;
	}
	
	public void deleteFile(File file) {
		if (file.isDirectory()) {
			for (File tmpFile : file.listFiles()) {
				deleteFile(tmpFile);
			}
		} else {
			file.delete();
		}
	}
	
	public boolean isExtStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
