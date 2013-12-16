package com.speedven.demo;

import java.io.DataOutputStream;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.speedven.pickview.widget.NumericWheelAdapter;
import com.speedven.pickview.widget.OnWheelScrollListener;
import com.speedven.pickview.widget.WheelView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity implements OnClickListener {
	private LayoutInflater inflater = null;
	private WheelView year;
	private WheelView month;
	private WheelView day;
	private WheelView hour;
	private WheelView mins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.time:
			showPopwindow(getTimePick());
			break;
		case R.id.data:
			showPopwindow(getDataPick());
			break;
		}
	}

	private void showPopwindow(View view) {
		PopupWindow menuWindow = new PopupWindow(view,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		menuWindow.setFocusable(true);
		menuWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
		menuWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * 时间
	 * 
	 * @return
	 */
	private View getTimePick() {
		View view = inflater.inflate(R.layout.timepick, null);
		hour = (WheelView) view.findViewById(R.id.hour);
		hour.setAdapter(new NumericWheelAdapter(0, 23));
		hour.setLabel("时");
		hour.setCyclic(true);
		mins = (WheelView) view.findViewById(R.id.mins);
		mins.setAdapter(new NumericWheelAdapter(0, 59));
		mins.setLabel("分");
		mins.setCyclic(true);
		hour.setCurrentItem(8);
		mins.setCurrentItem(30);
		TextView bt = (TextView) view.findViewById(R.id.set);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int date = calendar.get(Calendar.DAY_OF_MONTH);
				int h = hour.getCurrentItem();
				int m = mins.getCurrentItem();
				int s = calendar.get(Calendar.SECOND);
				 StringBuffer sb = new StringBuffer();
				 sb.append(year).append(month).append(date);
				 sb.append(".");
				 sb.append(h > 10 ? h : "0" + h).append(m > 10 ? m : "0" + m)
				 .append(s > 10 ? s : "0" + s);
				 setDate(sb.toString());
				// Toast.makeText(getApplicationContext(), sb.toString(),
				// Toast.LENGTH_LONG).show();

				 
			}
		});
		return view;
	}

	public void setDate(String time) {
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(
					process.getOutputStream());
			// os.writeBytes("date -s 20121219.024012; \n");
			os.writeBytes("date -s" + time + ";\n");
		} catch (Exception e) {
			Log.d(MainActivity.this.getClass().getSimpleName(),
					"error==" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 日期
	 * 
	 * @return
	 */
	private View getDataPick() {
		final Calendar c = Calendar.getInstance();
		final int curYear = c.get(Calendar.YEAR);
		final int curMonth = c.get(Calendar.MONTH) + 1;// 月份的下标是从0开始的
		final int curDate = c.get(Calendar.DATE);
		View view = inflater.inflate(R.layout.datapick, null);
		// 年
		year = (WheelView) view.findViewById(R.id.year);
		year.setAdapter(new NumericWheelAdapter(2000, 2099));
		year.setLabel("年");
		year.setCyclic(true);
		year.addScrollingListener(scrollListener);
		// 月
		month = (WheelView) view.findViewById(R.id.month);
		month.setAdapter(new NumericWheelAdapter(1, 12));
		month.setLabel("月");
		month.setCyclic(true);
		month.addScrollingListener(scrollListener);
		// 日
		day = (WheelView) view.findViewById(R.id.day);
		initDay(curYear, curMonth);
		day.setLabel("日");
		day.setCyclic(true);

		// 设置默认值(下面的2000，1即为minValue,设置的不是确切值，而是游标)
		year.setCurrentItem(curYear - 2000);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);
		TextView bt = (TextView) view.findViewById(R.id.set);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int h = c.get(Calendar.HOUR_OF_DAY);
				int m = c.get(Calendar.MINUTE);
				int s = c.get(Calendar.SECOND);
				StringBuffer sb = new StringBuffer();
				sb.append(String.valueOf(year.getCurrentItem() + 2000))
						.append(month.getCurrentItem() + 1 > 9 ? (month
								.getCurrentItem() + 1) + "" : "0"
								+ month.getCurrentItem() + 1)
						.append(String.valueOf(day.getCurrentItem() + 1));
				sb.append(".");
				sb.append(h).append(m).append(s);
				setDate(sb.toString());
				// Toast.makeText(getApplicationContext(), sb.toString(),
				// Toast.LENGTH_LONG).show();
			}
		});
		return view;
	}

	/**
	 * 用于监听滑动事件，更改月份天数
	 */
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			// TODO Auto-generated method stub
			int n_year = year.getCurrentItem() + 2000;// 年
			int n_month = month.getCurrentItem() + 1;// 月
			initDay(n_year, n_month);
		}
	};

	/**
	 * 根据年月算出这个月多少天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {// 计算是否是闰年
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}

	/**
	 * 初始化天数
	 */
	private void initDay(int arg1, int arg2) {
		day.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
	}
}
