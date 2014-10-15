package com.cokastore.fragment;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cokastore.ConsumerAddActivity;
import com.cokastore.ConsumerEditActivity;
import com.cokastore.ConsumerViewActivity;
import com.cokastore.R;
import com.cokastore.res.CokaUtil;
import com.cokastore.res.ConsumerListAdapter;
import com.cokastore.res.DBAction;

public class ConsumerFragment extends Fragment{
	
	private ListView listView;
	private String consumerId;
	private String consumerContent;
	private ConsumerListAdapter adapter;
	private static final int ACTION_ADD_CODE = 1;
	private static final int ACTION_EDIT_CODE = 2;
	private static final int ACTION_VIEW_CODE = 3;
	private static final int EDIT = 101;
	private static final int DELETE = 102;
	CokaUtil cokaUtil;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
						Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_consumer, container,false);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		cokaUtil = CokaUtil.getCokaUtil();
		listView = (ListView)getActivity().findViewById(R.id.consumerListView);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				consumerId = ((TextView)view.findViewById(R.id.list_consumerId)).getText().toString();
				view();				
			}
			
		});
		
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view,
					int position, long id) {
				consumerId = ((TextView)view.findViewById(R.id.list_consumerId)).getText().toString();
				consumerContent = ((TextView)view.findViewById(R.id.list_consumerDate)).getText().toString()
						+ "  " + ((TextView)view.findViewById(R.id.list_consumerName)).getText().toString();;
				return false;
			}
			
		});

		
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View view,
					ContextMenuInfo menuInfo) {
				// 1.預約 2.編輯 3.刪除
				menu.add(0, EDIT, 1, "編輯");
				menu.add(0, DELETE, 2, "刪除");
			}
		});
		/**
		 *	1.DB查詢客戶資料
		 *	2.塞入ListAdapter
		 *	3.顯示
		 * */
		List<Map<String,Object>> userList = DBAction.consumerQuery(getActivity(), null);
		adapter = new ConsumerListAdapter(getActivity(),listView, userList);
		listView.setAdapter(adapter);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.consumer_activity_bar, menu);
		
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
		
		((SearchView)menu.findItem(R.id.consumer_search).getActionView()).setQueryHint("輸入姓名或日期");
		((SearchView)menu.findItem(R.id.consumer_search).getActionView()).setOnQueryTextListener(queryTextListener);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
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
		
			case R.id.consumer_add:
				Intent i = new Intent(getActivity(), ConsumerAddActivity.class);
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
				adapter = new ConsumerListAdapter(getActivity(),listView, DBAction.consumerQuery(getActivity(), null));
				listView.setAdapter(adapter);
				break;
			case ACTION_EDIT_CODE:
				adapter = new ConsumerListAdapter(getActivity(),listView, DBAction.consumerQuery(getActivity(), null));
				listView.setAdapter(adapter);
				break;
			default:
				break;
				
		}
		
	}
	
	public void view() {
		Intent i = new Intent(getActivity(), ConsumerViewActivity.class);
		i.putExtra("consumerId", consumerId);
		startActivityForResult(i,ACTION_VIEW_CODE);
	}
	
	public void edit() {
		Intent i = new Intent(getActivity(), ConsumerEditActivity.class);
		i.putExtra("consumerId", consumerId);
		startActivityForResult(i,ACTION_EDIT_CODE);
	}
	
	public void alert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle("是否刪除此筆消費紀錄？");
		alertDialog.setMessage(consumerContent);
		alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DBAction.consumerDelete(getActivity(),consumerId);
				fileDelete(consumerId);
				//更新新的資料
				adapter = new ConsumerListAdapter(getActivity(),listView, DBAction.consumerQuery(getActivity(), null));
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
	
	public void fileDelete(String id) {
		String path = getActivity().getExternalFilesDir(null) + "/consu/" + id;
		File file = new File(path);
		cokaUtil.deleteFile(file);
		file.delete();
	}
}
