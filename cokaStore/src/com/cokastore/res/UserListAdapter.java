package com.cokastore.res;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cokastore.R;
import com.cokastore.res.AsyncImageFileLoader.ImageCallback;


public class UserListAdapter extends SimpleAdapter {
	
	private ListView mListView = null;
	private LayoutInflater mInflater;
	private List<Map<String,Object>> data;
    private Bitmap loadingIcon;        
    private AsyncImageFileLoader asyncImageFileLoader;
    private Filter mFilter;
	private ArrayList<Map<String,Object>> originalData;
	
	
	public UserListAdapter(Context context,ListView listView,List<Map<String,Object>>data) {
		super(context
				,data
				,R.layout.user_list
				,new String[]{"pic","name","id"}
				,new int[]{R.id.list_userPic,R.id.list_userName,R.id.list_userId});
		loadingIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dark_action_person);
		mListView = listView;
		mInflater = LayoutInflater.from(context);
		this.data = data; 
		asyncImageFileLoader = new AsyncImageFileLoader(); 
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
		ViewHolder mHolder;

		if(convertView == null)
        {
        	convertView = mInflater.inflate(R.layout.user_list, null);
        	
        	mHolder = new ViewHolder();
        	mHolder.icon = (ImageView) convertView.findViewById(R.id.list_userPic);
        	mHolder.text = (TextView) convertView.findViewById(R.id.list_userName);
        	mHolder.phone = (TextView) convertView.findViewById(R.id.list_userPhone);
        	mHolder.date = (TextView) convertView.findViewById(R.id.list_userDate);
        	mHolder.id = (TextView) convertView.findViewById(R.id.list_userId);
        	convertView.setTag(mHolder);
        } else {
        	mHolder = (ViewHolder) convertView.getTag();
        }
        
    	mHolder.text.setText(data.get(position).get("name").toString());
    	mHolder.id.setText(data.get(position).get("id").toString());
    	mHolder.date.setText((data.get(position).get("date") == null ? "" : data.get(position).get("date").toString()));
    	mHolder.phone.setText(data.get(position).get("phone").toString());
    	
    	ImageView imageView = mHolder.icon;
    	imageView.setTag(data.get(position).get("pic").toString());
    	
    	Bitmap cachedBitmap = asyncImageFileLoader.loadBitmap(data.get(position).get("pic").toString(), 200, 200, new ImageCallback() {  
            @Override
            public void imageCallback(Bitmap imageBitmap, String imageFile) {
                // 利用檔案名稱找尋當前mHolder.icon
                ImageView imageViewByTag = (ImageView) mListView.findViewWithTag(imageFile);  
                if (imageViewByTag != null) {  
                    if(imageBitmap != null)
                        imageViewByTag.setImageBitmap(imageBitmap);  
                } 
            }  
        });
    	
    	if(cachedBitmap != null)
            imageView.setImageBitmap(cachedBitmap);
        else
            imageView.setImageBitmap(loadingIcon); //顯示預設的圖片
         
        
        return convertView;
    }
	
	private class ViewHolder {
		TextView text;
		TextView id;
		TextView phone;
		TextView date;
		ImageView icon;
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
				//判斷輸入的字串為姓名或電話
				switch (pattern(prefixString)) {
					case 1:
						filter_string = "phone";
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
