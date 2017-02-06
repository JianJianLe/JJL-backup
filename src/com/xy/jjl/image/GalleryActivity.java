package com.xy.jjl.image;
import java.util.ArrayList;
import java.util.List;

import com.xy.jjl.utils.Bimp;
import com.xy.jjl.utils.PublicWay;
import com.xy.jjl.utils.Res;
import com.xy.jjl.zoom.PhotoView;
import com.xy.jjl.zoom.ViewPagerFixed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

  
public class GalleryActivity extends Activity {
	private Intent intent;
    // ���ذ�ť
    private Button back_bt;
	// ���Ͱ�ť
	private Button send_bt;
	//ɾ����ť
	private Button del_bt;
	//������ʾԤ��ͼƬλ�õ�textview
	private TextView positionTextView;
	//��ȡǰһ��activity��������position
	private int position;
	//��ǰ��λ��
	private int location = 0;
	
	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	
	private Context mContext;

	RelativeLayout photo_relativeLayout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(Res.getLayoutID("plugin_camera_gallery"));// ������������
		
		PublicWay.activityList.add(this);
		mContext = this;
		back_bt = (Button) findViewById(Res.getWidgetID("gallery_back"));
		send_bt = (Button) findViewById(Res.getWidgetID("send_button"));
		del_bt = (Button)findViewById(Res.getWidgetID("gallery_del"));
		back_bt.setOnClickListener(new BackListener());
		send_bt.setOnClickListener(new GallerySendListener());
		del_bt.setOnClickListener(new DelListener());
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		position = Integer.parseInt(intent.getStringExtra("position"));
		isShowOkBt();
		// Ϊ���Ͱ�ť��������
		pager = (ViewPagerFixed) findViewById(Res.getWidgetID("gallery01"));
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			initListViews( Bimp.tempSelectBitmap.get(i).getBitmap() );
		}
		
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin((int)getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}
	
	// ���ذ�ť���ӵļ�����
	private class BackListener implements OnClickListener {

		public void onClick(View v) {
			intent.setClass(GalleryActivity.this, ImageFile.class);
			startActivity(intent);
		}
	}
	
	// ɾ����ť���ӵļ�����
	private class DelListener implements OnClickListener {

		public void onClick(View v) {
			if (listViews.size() == 1) {
				Bimp.tempSelectBitmap.clear();
				Bimp.max = 0;
				send_bt.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
				Intent intent = new Intent("data.broadcast.action");  
                sendBroadcast(intent);  
				finish();
			} else {
				Bimp.tempSelectBitmap.remove(location);
				Bimp.max--;
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				send_bt.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
				adapter.notifyDataSetChanged();
			}
		}
	}

	// ��ɰ�ť�ļ���
	private class GallerySendListener implements OnClickListener {
		public void onClick(View v) {
			finish();
			intent.setClass(mContext,ImageShowActivity.class);
			startActivity(intent);
		}

	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			send_bt.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			send_bt.setPressed(true);
			send_bt.setClickable(true);
			send_bt.setTextColor(Color.WHITE);
		} else {
			send_bt.setPressed(false);
			send_bt.setClickable(false);
			send_bt.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	/**
	 * �������ذ�ť
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(position==1){
				this.finish();
				intent.setClass(GalleryActivity.this, AlbumActivity.class);
				startActivity(intent);
			}else if(position==2){
				this.finish();
				intent.setClass(GalleryActivity.this, ShowAllPhoto.class);
				startActivity(intent);
			}
		}
		return true;
	}
	
	
	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}