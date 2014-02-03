package com.example.gismeteo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.example.gismeteo.interfaces.XMLTaskListener;
import com.example.gismeteo.constants.Constants;

public class RegionList extends Activity implements AdapterView.OnItemClickListener, XMLTaskListener {
	// private final static String REG_NAME = "region_name", REGION = "region", EXIT = "EXIT";
    private ListView regionListView;
    private EditText inputSearch;
    private ArrayList<String> regionList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ProgressBar progress;
    private XMLTask regionFind;
    private boolean active = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
 		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.regions);
        active = true;
        progress = (ProgressBar) findViewById(R.id.progressBar);
		regionListView = (ListView)findViewById(R.id.region_list);
		regionListView.setOnItemClickListener(this);
        regionFind = new XMLTask(this, regionList, this);
        regionFind.execute();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.main, menu);
			return false;
	}
	@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(Constants.REGION, regionList.get(position).toString());
        setResult(RESULT_OK, intent);
		overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        finish();
    }
	@Override
	public void onBackPressed() {
        active = false;
		if(getCallingActivity().getClassName().equals(SplashScreen.class)){
			Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(Constants.EXIT, true);
			startActivity(intent);
		} else { finish();}
	}

    @Override
    public void onXMLTaskComplete(ArrayList<String> regionList) {
        progress.setVisibility(View.INVISIBLE);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,regionList);
        regionListView.setAdapter(adapter);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                RegionList.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
    }

    class XMLTask extends AsyncTask<Void, Void, ArrayList<String>> {
        private Context context;
        private ArrayList<String> taskRegionList;
        private XMLTaskListener callback;

        public XMLTask(Context context, ArrayList<String> regionList, XMLTaskListener callback) {
            this.context = context;
            this.taskRegionList = regionList;
            this.callback = callback;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            XmlPullParser xpp = context.getResources().getXml(R.xml.gismeteo_city);
            String tagName = new String();
            try {
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if(xpp.getEventType() == XmlPullParser.START_TAG) {
                        tagName = xpp.getName();
                    }
                    if(xpp.getEventType() == XmlPullParser.TEXT) {
                        if (tagName.equals(Constants.REG_NAME)){
                            taskRegionList.add(xpp.getText());
                        }
                    }
                    xpp.next();
                }
                return taskRegionList;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            if(result == null) {
                alert(context.getString(R.string.error), context);
            } else {
                callback.onXMLTaskComplete(result);
            }
        }

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
}
