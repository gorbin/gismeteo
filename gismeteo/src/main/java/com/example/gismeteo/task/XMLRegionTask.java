package com.example.gismeteo.task;

import com.example.gismeteo.R;
import com.example.gismeteo.interfaces.XMLTaskListener;
import com.example.gismeteo.constants.Constants;

class XMLRegionTask extends AsyncTask<Void, Void, ArrayList<Region>> {
        private Context context;
        private ArrayList<Region> taskRegionList;
        private XMLRegionTaskListener callback;

        public XMLRegionTask(Context context, ArrayList<Region> regionList, XMLTaskListener callback) {
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
			Region regionItem = new Region();
            String tagName = new String();
            try {
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if(xpp.getEventType() == XmlPullParser.START_TAG) {
                        tagName = xpp.getName();
                    }
                    if(xpp.getEventType() == XmlPullParser.TEXT) {
                        if (tagName.equals(Constants.REG_NAME)){
                            // taskRegionList.add(xpp.getText());
							regionItem.setName(xpp.getText());
                        }
                        if (tagName.equals(Constants.REG_NUM)){
							regionItem.setNum(xpp.getText());
                        }
						if (tagName.equals(Constants.REG_CODE)){
							regionItem.setGisCode(xpp.getText());
                        }
                    }
					if(xpp.getEventType() == XmlPullParser.END_TAG){
						if (xpp.getName().equals(Constants.ITEM)) {
							taskRegionList.add(i,regionItem);
							regionItem = new Region();
							i++;
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
        protected void onPostExecute(ArrayList<Region> result) {
            super.onPostExecute(result);
            if(result == null) {
                alert(context.getString(R.string.error), context);
            } else {
                callback.onXMLTaskComplete(result);
            }
        }

    }