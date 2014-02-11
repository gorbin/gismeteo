package com.example.gismeteo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.example.gismeteo.task.ForecastForRegion;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.gismeteo.interfaces.ForecastTaskListener;
import com.example.gismeteo.adapter.WeatherListAdapter;
import com.example.gismeteo.utils.Weather;
import com.example.gismeteo.receiver.WeatherNotification;

import com.example.gismeteo.constants.Constants;
//Test
public class MainActivity extends Activity implements ExpandableListView.OnGroupExpandListener, ForecastTaskListener {

    private ExpandableListView listView;
    private WeatherListAdapter adapter;
	private ForecastForRegion task;
    private ArrayList<Weather> forecast = new ArrayList<Weather>();
	private String region = new String();
    private int height;
	private MenuItem serviceBtn,serviceBtn2;
	private PendingIntent pendingIntent;
    private AlarmManager am;
    private boolean active;
	private TimePicker tp ;
    private CheckBox activeBox;
	private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = true;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		region = intent.getStringExtra(Constants.REGION);
		if (forecast != null){
			listItems(forecast);
		} else if(region.length() != 0){
			showForecast();
		} else {
			alert(this.getString(R.string.error),this);
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        serviceBtn2 = menu.findItem(R.id.service_mbtn2);
        serviceBtn2.setTitle("Settings");
		serviceBtn = menu.findItem(R.id.service_mbtn);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if(isServiceRunning()){
            serviceBtn.setTitle(String.format(this.getString(R.string.service_button),this.getString(R.string.off)));
        } else {
            serviceBtn.setTitle(String.format(this.getString(R.string.service_button),this.getString(R.string.on)));
        }
        return super.onPrepareOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.listregion:
			startActivityForResult(new Intent(this,RegionList.class),1);
			return true;
		case R.id.service_mbtn:
			if(isServiceRunning()){
                am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(this, WeatherNotification.class);
                intent.putExtra(Constants.REGION, region);
                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
				am.cancel(pendingIntent);
                pendingIntent.cancel();
			} else {
				restartNotify();
			}
			return true;
        case R.id.service_mbtn2:
            // if (android.os.Build.VERSION.SDK_INT < 11) {
                // startActivity(new Intent(this, Prefs.class));
            // } else {
                // getFragmentManager().beginTransaction()
                        // .replace(android.R.id.content, new PrefsFrag())
                        // .addToBackStack(null)
                        // .commit();
            // }
			openTime(context);
            return true;
		default:
            return super.onOptionsItemSelected(item);
		}
    }
	private boolean isServiceRunning() {
		boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                new Intent(getApplicationContext(), WeatherNotification.class),
                PendingIntent.FLAG_NO_CREATE) != null);
		return alarmUp; 
	}
	private void restartNotify() {
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, WeatherNotification.class);
		intent.putExtra(Constants.REGION, region);
        intent.putExtra(Constants.NOTIF, true);
        // intent.putExtra(SECOND_NOTIF, true);
		pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
		am.cancel(pendingIntent);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 5);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 20000, pendingIntent);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        region = data.getStringExtra(Constants.REGION);
        showForecast();
    }
	private void showForecast(){
		task = new ForecastForRegion(this, region, true, this);
		task.execute();
	}
	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null){
			listItems(forecast);
		} else {
			alert(this.getString(R.string.error),this);
		}
	}
	public void listItems(ArrayList<Weather> forecast){
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
	public void alert(String message, Context context){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage(message);
        ad.setCancelable(true);
        ad.setPositiveButton(context.getString(R.string.close),	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				finish();
			}
		}).create().show();
		ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
                return;
            }
        });
        if(active) {
            ad.show();
        }
    }
	private void openTime(Context context) {
        View timeLayout = getLayoutInflater().inflate(R.layout.time_dialog, null);
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage("Wat");
        ad.setView(timeLayout);
        ad.setCancelable(true);
        tp = (TimePicker)timeLayout.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        tp.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        activeBox = (CheckBox) timeLayout.findViewById(R.id.active);
        ad.setPositiveButton("Set",	new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Integer hour = tp.getCurrentHour();
                Integer minute = tp.getCurrentMinute();
                boolean activeIt = activeBox.isChecked();
                // label.setText("Active:"+activeIt+"; Time"+hour+":"+minute);
                // dialog.cancel();
            }
        }).create();
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
            }
        });
        ad.show();

    }

}