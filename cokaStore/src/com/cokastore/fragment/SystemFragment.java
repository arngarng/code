package com.cokastore.fragment;

import java.io.File;
import java.util.Map;

import com.cokastore.R;
import com.cokastore.res.CokaUtil;
import com.cokastore.res.DBAction;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SystemFragment extends Fragment {
	
	private final int VIEW = 0;
	private final int UPLOAD = 1;
	private final int DELETE = 2;
	private final int PICK_OK = 111;
	private final String logoPath = "/logo.jpg";
	private CokaUtil cokaUtil;
	private Map<String,Object> config = null;
	private CheckBox notify;
	private EditText birth,longTime,notifyTime,loginTime;
	private TextView textSetting,programSetting,notifySetting;
	private LinearLayout textContentLayout,programContentLayout,notifyContentLayout;
	private ImageView logo;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
						Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_system, container,false);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		cokaUtil = CokaUtil.getCokaUtil();
		setting();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		initValue();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// 1.查看 2.通知
		menu.add(0, VIEW, 0, "查看");
		menu.add(0, UPLOAD, 1, "上傳");
		menu.add(0, DELETE, 2, "刪除");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case VIEW:
				cokaUtil.showPopupWindow(getActivity(),logo);
				break;
			case UPLOAD:
				pickPhoto();
				break;
			case DELETE:
				deletePhoto();
				break;
		}
		
		return false;
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PICK_OK:
				if (data != null) {
					//取得檔案Uri
					Uri uri = data.getData();
					String path = cokaUtil.getRealImagePathFromURI(uri, getActivity());
					Log.i("Photo path", path);
					cokaUtil.setPhoto(path, logo,getActivity());
					//將檔案復製到logoPath
					if (cokaUtil.isExtStorageWritable()) {
						String uploadPath = getActivity().getExternalFilesDir(null) + logoPath;
						File file = new File(uploadPath);
						if (file.exists()) {
							file.delete();
						}
						file = new File(path);
						cokaUtil.copyFile(file, uploadPath);
					}
				}
				break;
				
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setting() {
		
		notifyTime = (EditText) getActivity().findViewById(R.id.notifyTime);
		loginTime = (EditText) getActivity().findViewById(R.id.loginTime);
		notify = (CheckBox) getActivity().findViewById(R.id.YnNotify);
		birth = (EditText) getActivity().findViewById(R.id.birthText);
		longTime = (EditText) getActivity().findViewById(R.id.longTimeText);
		textSetting = (TextView) getActivity().findViewById(R.id.textSetting);
		programSetting = (TextView) getActivity().findViewById(R.id.programSetting);
		notifySetting = (TextView) getActivity().findViewById(R.id.notifySetting);
		textContentLayout = (LinearLayout) getActivity().findViewById(R.id.textContentLayout);
		notifyContentLayout = (LinearLayout) getActivity().findViewById(R.id.notifyContentLayout);
		programContentLayout = (LinearLayout) getActivity().findViewById(R.id.programContentLayout);
		logo = (ImageView) getActivity().findViewById(R.id.logoImage);
		
		//初始值
		initValue();
		logoImageSetting();
		
		//綁定事件
		cokaUtil.setTimeView(getActivity(), notifyTime);
		birth.setOnFocusChangeListener(focus);
		birth.setOnKeyListener(onKey);
		longTime.setOnFocusChangeListener(focus);
		longTime.setOnKeyListener(onKey);
		loginTime.setOnFocusChangeListener(focus);
		loginTime.setOnKeyListener(onKey);
		notifyTime.addTextChangedListener(watcher);
		((View)textSetting).setOnClickListener(onClick);
		((View)programSetting).setOnClickListener(onClick);
		((View)notifySetting).setOnClickListener(onClick);
		registerForContextMenu(logo);
		
	}
	
	public void setNotifyCheck(CheckBox chk) {
		//初始值設定
		if ("Y".equals(config.get("notify").toString())) {
			chk.setChecked(true);
		} else {
			chk.setChecked(false);
		}
		
		chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//啟動提醒
					String time = notifyTime.getText().toString();
					String[] timeSplit = time.split(":");
					//timeSplit[0] hour ; timeSplit[1] minute
					cokaUtil.setAlarmManager(getActivity().getApplicationContext(), Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]));
					Toast.makeText(getActivity(), "提醒開啟", Toast.LENGTH_SHORT).show();
					
				} else {
					//關閉提醒
					cokaUtil.cancelAlarmManager(getActivity().getApplicationContext());
					Toast.makeText(getActivity(), "提醒關閉", Toast.LENGTH_SHORT).show();
				}
				
				updateConfig();
			}
		});
	}
	
	//修改值後存檔
	TextWatcher watcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			updateConfig();
		}
	};
	
	//按反回鍵退出EditText
	OnKeyListener onKey = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN 
					&& keyCode == KeyEvent.KEYCODE_BACK) {
				v.clearFocus();
				return true;
			}
			return false;
		}
	};
	
	//FocusOut 儲存資料
	OnFocusChangeListener focus = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			
			if (hasFocus) {
				
			} else {
				updateConfig();
			}
		}
	};
	
	View.OnClickListener onClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			toggleContents(v);
		}
	};
	
	public void updateConfig() {
		String ynNotify = notify.isChecked() ? "Y" : "N";
		String time = notifyTime.getText().toString();
		String birthText = birth.getText().toString();
		String longTimeText = longTime.getText().toString();
		String loginDelay = loginTime.getText().toString();
		loginDelay = "".equals(loginDelay.trim()) ? "0" : loginDelay;
		DBAction.updateConfig(getActivity(), ynNotify, time, birthText, longTimeText, Integer.parseInt(loginDelay));
	}
	
	public void toggleContents(View view) {
		//expandable
		switch (view.getId()) {
			case R.id.textSetting :
				textContentLayout.setVisibility(textContentLayout.isShown() 
						? View.GONE
						: View.VISIBLE);
				break;
			case R.id.programSetting :
				programContentLayout.setVisibility(programContentLayout.isShown() 
						? View.GONE
						: View.VISIBLE);
				break;
			case R.id.notifySetting :
				notifyContentLayout.setVisibility(notifyContentLayout.isShown() 
						? View.GONE
						: View.VISIBLE);
				break;
				
		}
	}
	
	public void pickPhoto() {
		Uri imageUri = Uri.parse("file://" + getActivity().getExternalFilesDir(null) + logoPath);
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_PICK);
		//intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, PICK_OK);
	}
	
	public void initValue() {
		
		config = DBAction.queryConfig(getActivity());
		//初始值
		notifyTime.setText(config.get("time").toString());
		birth.setText(config.get("BirthText").toString());
		longTime.setText(config.get("LongTimeText").toString());
		loginTime.setText(config.get("loginTime").toString());
		setNotifyCheck(notify);
		
		
		//圖片初始
		if (cokaUtil.isExtStorageWritable()) {
			String path = getActivity().getExternalFilesDir(null) + logoPath;
			if (new File(path).exists()) {
				cokaUtil.setPhoto(path, logo,getActivity());
			}
		}
		
	}
	
	public void logoImageSetting() {
		
		logo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().openContextMenu(logo);
			}
		});
	}
	
	public void deletePhoto() {
		//將圖刪除
		if (cokaUtil.isExtStorageWritable()) {
			File file = new File(getActivity().getExternalFilesDir(null) + logoPath);
			if (file.exists()) {
				file.delete();
				logo.setImageResource(R.drawable.ic_action_new_picture);
			}
		}
	}

}
