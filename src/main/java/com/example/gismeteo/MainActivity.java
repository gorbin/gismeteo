package com.example.gismeteo;

import android.app.AlertDialog;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        GetLocation gl = new GetLocation(this);
        Location loc = gl.getCurrentLocation();
        if(loc!=null){
//            refresh.setText("" + loc.getLatitude() + "/" + loc.getLongitude());
            refresh.setText(""+gl.getAddress(loc.getLatitude(), loc.getLongitude()));
        }
        else
        {
            refresh.setText("looser");
        }
	}
    protected void alertbox(String title, String mymessage) {
        final Context context = this;
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(this);
        ad.setTitle(title);
        ad.setMessage(mymessage);
        ad.setPositiveButton("Включить GPS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        });
        ad.setNegativeButton("Не сейчас", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Маршрут не будет построен, для постройки маршрута включите GPS", Toast.LENGTH_LONG)
                        .show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Маршрут не будет построен, для постройки маршрута включите GPS", Toast.LENGTH_LONG)
                        .show();
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
        // refresh.setEnabled(false);
        // refresh.setVisibility(View.GONE);
        // lt = new LoadTask(this);
        // lt.execute();
		XmlParse wat = new XmlParse(this);
		refresh.setText(wat.getGisCode(this,"54"));
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
        Log.e("group", "--->" + groupPosition);

	}

    class LoadTask extends AsyncTask<Void, Void, ArrayList<Weather>> {
        private Context thisContext;
        private ProgressDialog progressDialog;
        private XmlParse gismeteo;

        public LoadTask(Context context) {
            thisContext = context;
			progressDialog = ProgressDialog.show(MainActivity.this, thisContext.getString(R.string.pd_title),thisContext.getString(R.string.pd_message), true);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Weather> doInBackground(Void... params) {
            try {
                gismeteo = new XmlParse(thisContext);
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
        protected void onPostExecute(ArrayList<Weather> result) {
            super.onPostExecute(result);
            forecast = result;
			if(forecast == null)
			{
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