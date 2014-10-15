package com.cokastore.fragment;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cokastore.ConsumerAddActivity;
import com.cokastore.R;
import com.cokastore.UserAddActivity;
import com.cokastore.UserEditActivity;
import com.cokastore.UserViewActivity;
import com.cokastore.res.DBAction;
import com.cokastore.res.UserListAdapter;

public class UserFragment extends Fragment {
	
	private ListView listView;
	private String userId,userName;
	private static final int ACTION_ADD_CODE = 1;
	private static final int ACTION_EDIT_CODE = 2;
	private static final int ACTION_VIEW_CODE = 3;
	private static final int ACTION_CONSUMER_CODE = 4;
	private static final int CONSUMER = 100;
	private static final int EDIT = 101;
	private static final int DELETE = 102;
	private UserListAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
						Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		return inflater.inflate(R.layout.fragment_user, container,false);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		listView = (ListView)getActivity().findViewById(R.id.userListView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				userId = ((TextView)view.findViewById(R.id.list_userId)).getText().toString();
				view();
			}
			
		});
		
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view,
					int position, long id) {
				userId = ((TextView)view.findViewById(R.id.list_userId)).getText().toString();
				userName = ((TextView)view.findViewById(R.id.list_userName)).getText().toString();
				return false;
			}
			
		});

		
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View view,
					ContextMenuInfo menuInfo) {
				// 1.預約 2.編輯 3.刪除
				menu.add(0, CONSUMER, 0, "到店消費");
				menu.add(0, EDIT, 1, "編輯");
				menu.add(0, DELETE, 2, "刪除");
			}
		});
		/**
		 *	1.DB查詢客戶資料
		 *	2.塞入ListAdapter
		 *	3.顯示
		 * */
		List<Map<String,Object>> userList = DBAction.userQuery(getActivity(), null);
		adapter = new UserListAdapter(getActivity(),listView, userList);
		listView.setAdapter(adapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.user_activity_bar, menu);
		
		super.onCreateOptionsMenu(menu,inflater);
		
		final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
		    @Override
		    public boolean onQueryTextChange(String query) {
		    	adapter.getFilter().filter(query);
		        return false;
		    }

		    @Override
		    public boolean onQueryTextSubmit(String query) {
		    	adapter.getFilter().filter(query);
		        return false;
		    }
		};
		((SearchView)menu.findItem(R.id.action_search).getActionView()).setQueryHint("輸入姓名或手機");
		((SearchView)menu.findItem(R.id.action_search).getActionView()).setOnQueryTextListener(queryTextListener);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case CONSUMER:
				consumer();
				break;
			case EDIT:
				edit();
				break;
			case DELETE:
				alert();
				break;
		}
		
		return false;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch(item.getItemId()) {
		
			case R.id.action_add:
				Intent i = new Intent(getActivity(), UserAddActivity.class);
				startActivityForResult(i,ACTION_ADD_CODE);
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,Intent data) {
			
		switch (requestCode) {
			case ACTION_ADD_CODE:
			case ACTION_EDIT_CODE:
			case ACTION_CONSUMER_CODE:
				adapter = new UserListAdapter(getActivity(),listView, DBAction.userQuery(getActivity(), null));
				listView.setAdapter(adapter);
				break;
			default:
				break;
				
		}
		
	}
	
	public void alert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle("是否刪除此筆客戶資料？");
		alertDialog.setMessage(userName);
		alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DBAction.userDelete(getActivity(),userId);
				fileDelete();
				//更新新的資料
				adapter = new UserListAdapter(getActivity(),listView, DBAction.userQuery(getActivity(), null));
				listView.setAdapter(adapter);
			}
		});
		alertDialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		alertDialog.show();
	}
	
	public void fileDelete() {
		 String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) {
		    	String path = getActivity().getExternalFilesDir(null) + "/usr/" + userId + ".jpg";
		    	File file = new File(path);
		    	if (file.exists()) {
		    		System.out.println(file.delete());
		    	}
		    }
	}
	
	public void edit() {
		Intent i = new Intent(getActivity(), UserEditActivity.class);
		i.putExtra("userId", userId);
		startActivityForResult(i,ACTION_EDIT_CODE);
	}
	public void view() {
		Intent i = new Intent(getActivity(), UserViewActivity.class);
		i.putExtra("userId", userId);
		startActivityForResult(i,ACTION_VIEW_CODE);
	}
	public void consumer() {
		Intent i = new Intent(getActivity(),ConsumerAddActivity.class);
		i.putExtra("userId", userId);
		i.putExtra("userName", userName);
		startActivityForResult(i, ACTION_CONSUMER_CODE);
	}

}
