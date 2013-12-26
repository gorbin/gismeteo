package com.example.gismeteo;

class ForecastForRegion extends AsyncTask<Void, String, ArrayList<Weather>> {
        private Context thisContext;
		private String region;
        private ProgressDialog progressDialog;
        private XmlParse gismeteo;
		private boolean progressDialogSet;
		private ForecastTaskListener callback;
        
		public ForecastForRegion(Context context, String region, boolean progressDialogSet, ForecastTaskListener callback) {
            thisContext = context;
			this.region = region;
			if(progressDialog){
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
			if(result == null) {
				AlertIt.alert(thisContext.getString(R.string.error), thisContext);
			} 
			callback.onTaskComplete(result);
        }
    }