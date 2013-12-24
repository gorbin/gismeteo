package com.example.gismeteo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
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
	private String region = new String();
    private int height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
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
                listView.expandGroup(itemPosition);
                return true;
            }
        });
		Intent intent = getIntent();
        forecast = (ArrayList<Weather>) intent.getSerializableExtra("forecast");
		if (forecast != null){
			listItems(forecast);
		} else{
			alert(this.getString(R.string.error));
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
		menu.add(this.getString(R.string.listreg_button));
        return super.onCreateOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
      startActivityForResult(new Intent(this,RegionList.class),1);
      return super.onOptionsItemSelected(item);
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        region = data.getStringExtra("region");
        showForecast();
    }

	private void showForecast(){
	    lt = new LoadTask(this, region);
        lt.execute();
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

    class LoadTask extends AsyncTask<Void, String, ArrayList<Weather>> {
        private Context thisContext;
		private String region;
        private ProgressDialog progressDialog;
        private XmlParse gismeteo;
        
		public LoadTask(Context context, String region) {
            thisContext = context;
			this.region = region;
			progressDialog = ProgressDialog.show(MainActivity.this, thisContext.getString(R.string.pd_title),thisContext.getString(R.string.pd_forecast), true);
        }
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
		
        @Override
        protected ArrayList<Weather> doInBackground(Void... params) {
            try {
                gismeteo = new XmlParse(thisContext, region);
            return gismeteo.getForecast();
            } catch (IOException e) {
                progressDialog.dismiss();
                e.printStackTrace();
				return null;
            } catch (XmlPullParserException e) {
                progressDialog.dismiss();
                e.printStackTrace();
				return null;
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
				return null;
            }
        }
		
        @Override
        protected void onPostExecute(ArrayList<Weather> result) {
            super.onPostExecute(result);
            forecast = result;
			progressDialog.dismiss();
			if(forecast == null) {
				alert(thisContext.getString(R.string.error));
			} else {
				listItems(forecast);
			}
        }
    }
}