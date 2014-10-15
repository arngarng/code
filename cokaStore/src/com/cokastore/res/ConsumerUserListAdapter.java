package com.cokastore.res;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cokastore.R;


public class ConsumerUserListAdapter extends SimpleAdapter{
	
	private ArrayFilter mFilter;
	private LayoutInflater mInflater;
	private List<Map<String,Object>> data;
	private ArrayList<Map<String,Object>> originalData;
	private Bitmap loadingIcon;      
	
	public ConsumerUserListAdapter(Context context,List<Map<String,Object>>data) {
		super(context
				,data
				,R.layout.consumer_name_list
				,new String[]{"name","pic","id"}
				,new int[]{R.id.list_consumerUserName,R.id.list_consumerUserPic,R.id.list_consumerUserId});
		loadingIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dark_action_person);
		mInflater = LayoutInflater.from(context);
		this.data = data; 
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	public View getView(int position, View convertView, ViewGroup parent)
    {

		if(convertView == null)
        {
        	convertView = mInflater.inflate(R.layout.consumer_name_list, null);
        }
        if (data.size() > 0) {
        	
        	((TextView) convertView.findViewById(R.id.list_consumerUserId)).setText(data.get(position).get("id").toString());
    		((TextView) convertView.findViewById(R.id.list_consumerUserName)).setText(data.get(position).get("name").toString());
    		ImageView imageView = (ImageView)convertView.findViewById(R.id.list_consumerUserPic);
        	Bitmap bitmap = BitmapFactory.decodeFile(data.get(position).get("pic").toString());
        	if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
        	} else {
                imageView.setImageBitmap(loadingIcon); //顯示預設的圖片
        	}
	         
        }
        return convertView;
    }
	
	@Override
	public Filter getFilter(){
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;		
	}
	
	private class ArrayFilter extends Filter {
		private Object lock = new Object();
		
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			
			if (originalData == null) {
				synchronized(lock) {
					originalData = new ArrayList<Map<String,Object>>(data);
				}
			}
			
			if (prefix == null || prefix.length() == 0) {
				synchronized (lock) {
					ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>(originalData);
					results.values = list;
					results.count = list.size();
				}
			} else {
				final String prefixString = prefix.toString().toLowerCase();
				
				ArrayList<Map<String,Object>> values = originalData;
				int count = values.size();
				
				ArrayList<Map<String,Object>> newValues = new ArrayList<Map<String,Object>>();
				System.out.println(prefix);
				for (int i = 0; i < count; i++) {
					String item = values.get(i).get("name").toString();
					if (item.toLowerCase().contains(prefixString)) {
						newValues.add(values.get(i));
					}
				}
				
				results.values = newValues;
				results.count = newValues.size();
			}
			
			return results;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			
			if (results.values != null) {
				data = (ArrayList<Map<String,Object>>) results.values;
			} else {
				data = new ArrayList<Map<String,Object>>();
			}
			
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
						
		}
		
	}
}
