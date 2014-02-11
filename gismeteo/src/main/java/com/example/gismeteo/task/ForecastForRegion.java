package com.example.gismeteo.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import com.example.gismeteo.R;
import com.example.gismeteo.utils.Weather;
import com.example.gismeteo.xml.XmlParse;
import com.example.gismeteo.interfaces.ForecastTaskListener;

public class ForecastForRegion extends AsyncTask<Void, String, ArrayList<Weather>> {
		private String giscode;
        private ProgressDialog progressDialog;
        private XmlParse gismeteo;
		private boolean progressDialogSet;
		private ForecastTaskListener callback;
        private Context context;

        
		public ForecastForRegion(Context context, String giscode, boolean progressDialogSet, ForecastTaskListener callback) {
			this.giscode = giscode;
            this.context=context;
            this.progressDialogSet = progressDialogSet;
			if(progressDialogSet){
				progressDialog = ProgressDialog.show(context, context.getString(R.string.pd_title), context.getString(R.string.pd_forecast), true);
			}
			this.callback = callback;
        }
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
		
        @Override
        protected ArrayList<Weather> doInBackground(Void... params) {
            try {
                gismeteo = new XmlParse(context, giscode);
                return gismeteo.getForecast();
            } catch (IOException e) {
                e.printStackTrace();
				return null;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
				return null;
            }
            catch (Exception e) {
                e.printStackTrace();
				return null;
            }
        }
		
        @Override
        protected void onPostExecute(ArrayList<Weather> result) {
            super.onPostExecute(result);
			if(progressDialogSet){
				progressDialog.dismiss();
			}
			callback.onTaskComplete(result);
        }
    }