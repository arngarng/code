package com.cokastore.res;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class ConsumerAutoCompleteTextView extends AutoCompleteTextView{

	public ConsumerAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		HashMap<String,Object> map = (HashMap<String,Object>) selectedItem;
		return map.get("name").toString();
	}
}
