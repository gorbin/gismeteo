import android.app.Activity;
 
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
 
public class RegionList extends Activity {
	private final static String REG_NAME = "region_name";
    private ListView regionListView;
    private ArrayList<String> regionList = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
 
		super.onCreate(savedInstanceState);

		setContentView(R.layout.regions);
		regionListView = (ListView)findViewById(R.id.region_list);
		
		XmlPullParser xpp = context.getResources().getXml(R.xml.gismeteo_city);
		String tagName = new String();
		while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
			if(xpp.getEventType() == XmlPullParser.START_TAG) {
				tagName = xpp.getName();
			}
			if(xpp.getEventType() == XmlPullParser.TEXT) {
				if (tagName.equals(REG_NAME)){
					regionList.add(xpp.getText());
				}	
			}
			xpp.next();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,regionList);
		regionListView.setAdapter(adapter);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.main, menu);
			return false;
	}
	@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putString("region", regionList.get(position));
        startActivity(intent);
    }
}