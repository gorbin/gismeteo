package com.example.gismeteo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }
    public void onRefresh(View view) {
        refresh.setEnabled(false);
        refresh.setVisibility(View.GONE);
        lt = new LoadTask(this);
        lt.execute();
    }
    public void alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getString(R.string.error));
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
                alert();
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                progressDialog.dismiss();
                alert();
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
				alert();
			}
			else
			{
				listItems(forecast);
			
			}
			progressDialog.dismiss();
        }
    }
}