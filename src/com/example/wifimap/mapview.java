package com.example.wifimap;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
public class mapview extends Activity {
	public static final int dbversion = 1;
	public static final String dbname = "wifimap.db";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Button bt1=(Button)findViewById(R.id.button1);
		final GoogleMap mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		View.OnClickListener load=new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			 try{
				sqldatabase sqldata=new sqldatabase(mapview.this);
			    SQLiteDatabase sql=sqldata.getReadableDatabase();
			    Cursor cu=sql.query("wardriving", null, null, null, null, null, null);
				for(int i=0;i<cu.getCount();i++){
			    	cu.moveToPosition(i);
			    	String SSID=cu.getString(cu.getColumnIndex("SSID"));
			    	String BSSID=cu.getString(cu.getColumnIndex("BSSID"));
			    	int RSSID=cu.getInt(cu.getColumnIndex("RSSID"));
			    	Double lat=cu.getDouble(cu.getColumnIndex("Latitude"));
			    	Double log=cu.getDouble(cu.getColumnIndex("Longitude"));
			    	int frequency=cu.getInt(cu.getColumnIndex("Frequency"));
			    	String cap=cu.getString(cu.getColumnIndex("capabilities"));
			    	MarkerOptions markerOpt = new MarkerOptions();
			    	markerOpt.position(new LatLng(lat, log));
			    	markerOpt.title("SSID: "+SSID);
			    	markerOpt.snippet("BSSID: "+BSSID+" "+"RSSID: "+RSSID+" "+"Frequency: "+frequency+" "+"capabilities: "+cap);
			    	markerOpt.draggable(false);
			    	markerOpt.visible(true);
			    	markerOpt.anchor(0.5f, 0.5f);
			    	markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
			    	mMap.addMarker(markerOpt);
				}
			}catch(Exception e){
				Toast.makeText(mapview.this,"No GPS Data.....", Toast.LENGTH_LONG).show();
			}
		   }
		};
		bt1.setOnClickListener(load);
	}
	public class sqldatabase extends SQLiteOpenHelper{
	    public sqldatabase(Context context) {
			super(context, dbname, null, dbversion);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
	}
}
