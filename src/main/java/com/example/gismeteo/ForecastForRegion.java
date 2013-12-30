package com.example.gismeteo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

class ForecastForRegion extends AsyncTask<Void, String, ArrayList<Weather>> {
		private String region;
        private ProgressDialog progressDialog;
        private XmlParse gismeteo;
		private boolean progressDialogSet;
		private ForecastTaskListener callback;
        private AlertIt ad = new AlertIt();
        
		public ForecastForRegion(String region, boolean progressDialogSet, ForecastTaskListener callback) {
			this.region = region;
            this.progressDialogSet = progressDialogSet;
			if(progressDialogSet){
				progressDialog = ProgressDialog.show(thisContext, thisContext.getString(R.string.pd_title),thisContext.getString(R.string.pd_forecast), true);
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
                gismeteo = new XmlParse(thisContext, region);
            return gismeteo.getForecast();
            } catch (IOException e) {
                e.printStackTrace();
				return null;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
				return null;
            } catch (Exception e) {
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