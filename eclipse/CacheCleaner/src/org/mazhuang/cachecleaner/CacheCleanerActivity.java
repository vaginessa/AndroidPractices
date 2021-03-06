package org.mazhuang.cachecleaner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CacheCleanerActivity extends FragmentActivity implements ICacheScanCallBack, ICacheCleanCallBack {
	
	private Button mScanButton;
	private Button mCleanButton;
	private TextView mCleanInfoTextView;
	private CacheScanTask mCacheScanTask;
	private CacheCleanTask mCacheCleanTask;
	
	private static final int MSG_HANDLER_SCAN_START = 0x01;
	private static final int MSG_HANDLER_SCAN_FINISH = 0x02;
	private static final int MSG_HANDLER_SCAN_PROGRESS = 0x03;
	
	private static final int MSG_HANDLER_CLEAN_START = 0x11;
	private static final int MSG_HANDLER_CLEAN_FINISH = 0x12;
	private static final int MSG_HANDLER_CLEAN_PROGRESS = 0x13;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_HANDLER_SCAN_START:
					mCleanInfoTextView.setText(R.string.scan_start);
					break;
					
				case MSG_HANDLER_SCAN_FINISH:
					mCleanInfoTextView.setText(R.string.scan_finish);
					FragmentManager fm = getSupportFragmentManager();
					CacheListFragment fragment = (CacheListFragment)fm.findFragmentById(R.id.cacheListFragmentContainer);
					fragment.updateUI();
					break;
					
				case MSG_HANDLER_SCAN_PROGRESS:
					mCleanInfoTextView.setText(msg.obj.toString());
					break;
					
				case MSG_HANDLER_CLEAN_START:
					mCleanInfoTextView.setText(R.string.clean_start);
					break;
					
				case MSG_HANDLER_CLEAN_FINISH:
					mCleanInfoTextView.setText(R.string.clean_finish);
					FragmentManager fm1 = getSupportFragmentManager();
					CacheListFragment fragment1 = (CacheListFragment)fm1.findFragmentById(R.id.cacheListFragmentContainer);
					fragment1.updateUI();
					break;
					
				case MSG_HANDLER_CLEAN_PROGRESS:
					mCleanInfoTextView.setText(msg.obj.toString());
					break;
					
				default:
						break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_cleaner);
		mScanButton = (Button)findViewById(R.id.scan_button);
		mScanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCacheScanTask = new CacheScanTask(getApplicationContext(), CacheCleanerActivity.this);
				mCacheScanTask.execute();
			}
		});
		
		mCleanButton = (Button)findViewById(R.id.clean_button);
		mCleanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCacheCleanTask = new CacheCleanTask(getApplicationContext(), CacheCleanerActivity.this);
				mCacheCleanTask.execute();
			}
		});
		
		mCleanInfoTextView = (TextView)findViewById(R.id.clean_info_text);
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.cacheListFragmentContainer);
		
		if (fragment == null) {
			fragment = new CacheListFragment();
			fm.beginTransaction()
				.add(R.id.cacheListFragmentContainer, fragment)
				.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cache_cleaner, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onScanStart() {
		mHandler.sendEmptyMessage(MSG_HANDLER_SCAN_START);
	}
	
	@Override
	public void onScanFinish() {
		mHandler.sendEmptyMessage(MSG_HANDLER_SCAN_FINISH);
	}
	
	@Override
	public void onScanProgress(int pos) {
		Message msg = mHandler.obtainMessage(MSG_HANDLER_SCAN_PROGRESS);
		msg.obj = pos;
		msg.sendToTarget();
	}
	
	@Override
	public void onCleanStart() {
		mHandler.sendEmptyMessage(MSG_HANDLER_CLEAN_START);
	}
	
	@Override
	public void onCleanFinish() {
		mHandler.sendEmptyMessage(MSG_HANDLER_CLEAN_FINISH);
	}
	
	@Override
	public void onCleanProgress(int pos) {
		Message msg = mHandler.obtainMessage(MSG_HANDLER_CLEAN_PROGRESS);
		msg.obj = pos;
		msg.sendToTarget();
	}
}
