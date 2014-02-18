package com.example.gismeteo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.gismeteo.dialogs.SimpleDialogs;
import com.example.gismeteo.dialogs.TimeOfNotificationDialog;

import com.example.gismeteo.task.ForecastForRegion;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.gismeteo.adapter.WeatherListAdapter;
import com.example.gismeteo.utils.GetGiscode;
import com.example.gismeteo.utils.Weather;
import com.example.gismeteo.receiver.WeatherNotification;

import com.example.gismeteo.constants.Constants;
//Test
public class MainActivity extends Activity implements ExpandableListView.OnGroupExpandListener, ForecastForRegion.ForecastTaskListener, TimeOfNotificationDialog.TimeNotifSetListener, GetGiscode.GetGiscodeListener {

    private ExpandableListView listView;
    private WeatherListAdapter adapter;
	private ForecastForRegion task;
    private ArrayList<Weather> forecast = new ArrayList<Weather>();
    private String giscode = new String();
    private int height;
	private PendingIntent pendingIntent;
    private AlarmManager am;
    private boolean active;
	private Context context;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;
	private GetGiscode giscodeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
		context = this;
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.RL);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (android.os.Build.VERSION.SDK_INT >= 13){
            display.getSize(size);
            height = size.y;
        }
        else{
            height = display.getHeight();
        }
        rl.getLayoutParams().height = height;
        listView = (ExpandableListView)findViewById(R.id.exListView);
		listView.setOnGroupExpandListener(this);
        listView.setVerticalFadingEdgeEnabled(false);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId) {
                if (android.os.Build.VERSION.SDK_INT >= 14){
                    listView.expandGroup(itemPosition, true);
                } else {
                    listView.expandGroup(itemPosition);
                }
                return true;
            }
        });
		Intent intent = getIntent();
        forecast = intent.getParcelableArrayListExtra(Constants.FORECAST);
		giscode = intent.getStringExtra(Constants.REGION);
        sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ed = sPref.edit();
	}

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
		boolean error = false;
        String giscodePrefs = sPref.getString(Constants.REGION, null);
        Long giscodeTimes = sPref.getLong(Constants.LOC_TIME, 0);
        Toast.makeText(context, "PrefReg = " + giscodePrefs + " PrefTime = " + giscodeTimes, Toast.LENGTH_LONG).show();
        if (forecast != null){
            listItems(forecast);
        } else if(giscode != null && giscode.length() != 0){
            showForecast(giscode);
        } else {
			error = true;
            SimpleDialogs.alert(context.getString(R.string.error), context, active);
        }
		if ((!error) && (System.currentTimeMillis() - giscodeTimes > Constants.TIME_FOR_LOC)) {
			if(giscodeListener != null) { 
				giscodeListener.clear();
			}
			giscodeListener = GetGiscode.getInstance(context);
			giscodeListener.setGiscodeListener(this);
            giscodeListener.showRegion(false);
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.listregion:
				startActivityForResult(new Intent(context,RegionList.class),1);
			return true;
			case R.id.service_mbtn2:
				TimeOfNotificationDialog.openTime(context, active, isServiceRunning(), this);
            return true;
		default:
            return super.onOptionsItemSelected(item);
		}
    }

	private boolean isServiceRunning() {
		boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent(getApplicationContext(), WeatherNotification.class),
                PendingIntent.FLAG_NO_CREATE) != null);
		return alarmUp; 
	}

	private void restartNotify(long time) {
		Toast.makeText(context, "Time = " + time , Toast.LENGTH_LONG).show();
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, WeatherNotification.class);
		intent.putExtra(Constants.REGION, giscode);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
		am.cancel(pendingIntent);
		am.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}

        giscode = data.getStringExtra(Constants.REGION);
        if (giscode != null) {
            showForecast(giscode);
        } else {
            SimpleDialogs.alert(context.getString(R.string.error), context, active);
        }
    }
	public void onGetGiscode(String giscode) {
		if (giscode != null && giscode.length() != 0) {
			ed.putString(Constants.REGION, giscode);
            ed.commit();
			showForecast(giscode);
		}
	}
	private void showForecast(String giscode){
		task = new ForecastForRegion(context, giscode, true, this);
		task.execute();
	}

	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null){
			listItems(forecast);
		} else {
            SimpleDialogs.alert(context.getString(R.string.error), context, active);
		}
	}

	private void listItems(ArrayList<Weather> forecast){
		adapter = new WeatherListAdapter(getApplicationContext(), forecast, height);
        listView.setAdapter(adapter);
		listView.setChildDivider(getResources().getDrawable(android.R.color.transparent));
        listView.setDividerHeight(0);
		listView.expandGroup(0);
    }

	public void onGroupExpand(int groupPosition) {
		int lenght = adapter.getGroupCount();
		for (int i = 0; i < lenght; i++) {
			if (i != groupPosition) {
				listView.collapseGroup(i);
			}
		}
	}

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onTimeNotifSet(long time, boolean activate) {
        if (activate) {
			Calendar date = Calendar.getInstance();
			date.set(Calendar.HOUR_OF_DAY, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);
			long notifTime = date.getTimeInMillis() + time;
            restartNotify(notifTime);
        } else if(isServiceRunning()) {
            am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, WeatherNotification.class);
            intent.putExtra(Constants.REGION, giscode);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
            am.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

}