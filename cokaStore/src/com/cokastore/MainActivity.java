package com.cokastore;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.cokastore.fragment.BookingFragment;
import com.cokastore.fragment.ConsumerFragment;
import com.cokastore.fragment.SystemFragment;
import com.cokastore.fragment.UserFragment;
import com.cokastore.res.TabListener;

public class MainActivity extends ActionBarActivity {
	
	public boolean isfinished = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tab = actionBar.newTab()
				.setText(R.string.action_user)
				.setIcon(R.drawable.ic_action_person)
				.setTabListener(new TabListener<UserFragment>(this, "user", UserFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText(R.string.action_consumer)
				.setIcon(R.drawable.ic_action_event)
				.setTabListener(new TabListener<ConsumerFragment>(this, "consumer", ConsumerFragment.class));
		actionBar.addTab(tab);
		
//		tab = actionBar.newTab()
//				.setText(R.string.action_booking)
//				.setIcon(R.drawable.ic_action_edit)
//				.setTabListener(new TabListener<BookingFragment>(this, "booking", BookingFragment.class));
//		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText(R.string.action_system)
				.setIcon(R.drawable.ic_action_settings)
				.setTabListener(new TabListener<SystemFragment>(this, "systemFragment", SystemFragment.class));
		actionBar.addTab(tab);		

	}
	
	@Override
	public void onBackPressed() {
		if (isfinished) {
			finish();
		} else {
			isfinished = true;
			Toast.makeText(this, "再按一次返回鍵退出", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isfinished = false;										
				}
				
			},2000);
		}
	}

}
