package com.example.gismeteo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import com.example.gismeteo.dialogs.SimpleDialogs;
import com.example.gismeteo.interfaces.XMLRegionTaskListener;
import com.example.gismeteo.constants.Constants;
import com.example.gismeteo.utils.Region;
import com.example.gismeteo.task.XMLRegionTask;
import com.example.gismeteo.adapter.RegionListAdapter;


public class RegionList extends Activity implements AdapterView.OnItemClickListener, XMLRegionTaskListener {
    private ListView regionListView;
    private EditText inputSearch;
    private ArrayList<Region> regionList = new ArrayList<Region>();
    private Context context;
    private RegionListAdapter adapter;
    private ProgressBar progress;
    private XMLRegionTask regionFind;
    private boolean active = false;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
 		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.regions);
        context = this;
        active = true;
        progress = (ProgressBar) findViewById(R.id.progressBar);
		regionListView = (ListView)findViewById(R.id.region_list);
		regionListView.setOnItemClickListener(this);
        regionFind = new XMLRegionTask(context, regionList, this);
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
        intent.putExtra(Constants.REGION, regionList.get(position).getGisCode());
        setResult(RESULT_OK, intent);
		overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
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
    public void onXMLRegionTaskComplete(ArrayList<Region> regionList) {
        if(regionList != null) {
            progress.setVisibility(View.INVISIBLE);
            adapter = new RegionListAdapter(this, regionList);
            regionListView.setAdapter(adapter);
            inputSearch = (EditText) findViewById(R.id.inputSearch);
            inputSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    adapter.filter(cs.toString());
                    // adapterContactList.filter(cs.toString());
                }
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }
                @Override
                public void afterTextChanged(Editable arg0) {

                }
            });
        } else { SimpleDialogs.alert(context.getString(R.string.error), context, active);}
    }

//    private void alert(String message, Context context){
//        AlertDialog.Builder ad = new AlertDialog.Builder(context);
//        ad.setMessage(message);
//        ad.setCancelable(true);
//        ad.setPositiveButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//                finish();
//            }
//        });
//        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(DialogInterface dialog) {
//                finish();
//                return;
//            }
//        });
//        ad.create();
//        if(active) {
//            ad.show();
//        }
//    }
}
