 package com.example.gismeteo;
 
 class LoadForecastTask extends AsyncTask<Void, String, ArrayList<Weather>> {
        private Context thisContext;
		private String region;
        private XmlParse gismeteo;
		private GetLocation gl;
        
		public LoadTask(Context context, String region) {
            thisContext = context;
			this.region = region;
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
                if(region.length() == 0){
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
			noty.setText(values[0]);
		}
		
        @Override
        protected void onPostExecute(ArrayList<Weather> result) {
            super.onPostExecute(result);
            forecast = result;
			if(region == null) {
				gpsAlertBox(thisContext.getString(R.string.GPS_error));
			} else if(forecast == null) {
				alert(thisContext.getString(R.string.error));
			} else {
				Intent intent = new Intent(thisContext,MainActivity.class);
				intent.putExtra("forecast",forecast);
				startActivity(intent);
                finish();
			}

        }
    }