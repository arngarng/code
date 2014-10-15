package com.cokastore.res;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cokastore.R;


public class ConsumerListAdapter extends SimpleAdapter {
	
	private LayoutInflater mInflater;
	private List<Map<String,Object>> data;
    private Filter mFilter;
	private ArrayList<Map<String,Object>> originalData;
	
	public ConsumerListAdapter(Context context,ListView listView,List<Map<String,Object>>data) {
		super(context
				,data
				,R.layout.consumer_list
				,new String[]{"id","date","name","content","price"}
				, new int[] {
				R.id.list_consumerId, R.id.list_consumerDate,
				R.id.list_consumerName, R.id.list_consumerContent,
				R.id.list_consumerPrice });
		mInflater = LayoutInflater.from(context);
		this.data = data; 
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
    {

		if(convertView == null)
        {
        	convertView = mInflater.inflate(R.layout.consumer_list, null);
        }
        
		((TextView) convertView.findViewById(R.id.list_consumerId)).setText(data.get(position).get("id").toString());
		((TextView) convertView.findViewById(R.id.list_consumerDate)).setText(data.get(position).get("date").toString());
		((TextView) convertView.findViewById(R.id.list_consumerName)).setText(data.get(position).get("name").toString());
		((TextView) convertView.findViewById(R.id.list_consumerContent)).setText(data.get(position).get("content").toString());
		((TextView) convertView.findViewById(R.id.list_consumerPrice)).setText(data.get(position).get("price").toString());


        
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
				String filter_string = "";
				//判斷輸入的字串為姓名或日期
				switch (pattern(prefixString)) {
					case 1:
					case 2:
						filter_string = "date";
						break;
					default:
						filter_string = "name";
						break;
				}
				for (int i = 0; i < count; i++) {
					String item = values.get(i).get(filter_string).toString();
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
	
	public int pattern(String str) {
		Pattern PhonePattern = Pattern.compile("[0-9]*");
		Pattern DatePattern = Pattern.compile("[0-9]*\\/?[0-9]*\\/?[0-9]*");
		if (str == null || "".equals(str.trim())) {
			return 0;
		} else if (PhonePattern.matcher(str).matches()) {
			return 1;
		} else if (DatePattern.matcher(str).matches()){
			return 2;
		} else {
			return 3;
		}
	}
	
}
