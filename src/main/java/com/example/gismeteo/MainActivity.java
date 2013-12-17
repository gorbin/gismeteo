package com.example.gismeteo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
//Master
public class MainActivity extends Activity implements ExpandableListView.OnGroupExpandListener {

    private ExpandableListView listView;
    private WeatherListAdapter adapter;
    private LoadTask lt;
    private ArrayList<Weather> forecast = new ArrayList<Weather>();
    private Button refresh;
	private String region = new String();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		if(intent != null) {
			region = intent.getStringExtra("region");
		}
        refresh = (Button) findViewById(R.id.refresh);
        listView = (ExpandableListView)findViewById(R.id.exListView);
		listView.setOnGroupExpandListener(this);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId)
            {
                listView.expandGroup(itemPosition);
                return true;
            }
        });

		refresh.setText(""+region);
//        if(loc!=null){
//            refresh.setText("" + loc.getLatitude() + "/" + loc.getLongitude());
//        }
//        else
//        {
            // refresh.setText("looser");
//        }
	}
    protected void gpsAlertBox(String mymessage) {
        final Context context = this;
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(this);	
        // ad.setTitle(title);
        ad.setMessage(mymessage);
        ad.setPositiveButton(this.getString(R.string.GPS_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        ad.setNegativeButton(this.getString(R.string.listreg_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
				startActivity(new Intent(((Dialog) dialog).getContext(),RegionList.class));
				return;
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                startActivity(new Intent(((Dialog) dialog).getContext(),RegionList.class));
                return;
            }
        });
        ad.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }
    public void onRefresh(View view) throws IOException, XmlPullParserException {
        refresh.setEnabled(false);
        refresh.setVisibility(View.GONE);
        lt = new LoadTask(this, region);
        lt.execute();
		// XmlParse wat = new XmlParse(this);
		// refresh.setText(wat.getGisCode(this,"54"));
    }
    public void alert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(this.getString(R.string.close),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
    }
    public void listItems(ArrayList<Weather> forecast){

		adapter = new WeatherListAdapter(getApplicationContext(), forecast);
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

    class LoadTask extends AsyncTask<Void, String, ArrayList<Weather>> {
        private Context thisContext;
		private String region;
        private ProgressDialog progressDialog;
        private XmlParse gismeteo;
		private GetLocation gl;
        public LoadTask(Context context, String region) {
            thisContext = context;
			this.region = region;
			progressDialog = ProgressDialog.show(MainActivity.this, thisContext.getString(R.string.pd_title),thisContext.getString(R.string.pd_message), true);
            gl = new GetLocation(thisContext);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Weather> doInBackground(Void... params) {
            try {
                gl.checkRegion();
				if(region == null) {
					region = gl.getRegion();
					if (region == null) {
						return null;
					}
				}
				publishProgress(thisContext.getString(R.string.pd_forecast));
                gismeteo = new XmlParse(thisContext, region);
                return gismeteo.getForecast();
            } catch (IOException e) {
                progressDialog.dismiss();
                alert(thisContext.getString(R.string.error));
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                progressDialog.dismiss();
                alert(thisContext.getString(R.string.error));
                e.printStackTrace();
            }
            return null;
        }
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			progressDialog.setMessage(values[0]);
		}
        @Override
        protected void onPostExecute(ArrayList<Weather> result) {
            super.onPostExecute(result);
            forecast = result;
			if(region == null) {
				gpsAlertBox(thisContext.getString(R.string.GPS_error));
			} else if(forecast == null) {
				alert(thisContext.getString(R.string.error));
			}
				else
				{
					listItems(forecast);
				}
			progressDialog.dismiss();
        }
    }
}