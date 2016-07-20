package com.example.wifimap;
import android.os.*;
import android.app.*;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
public class wifigps_scan extends Service {
	public static final int dbversion = 1;
	public static final String dbname = "wifimap.db";
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
	public LocationListener gpslist=new LocationListener(){
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			MainActivity.txt1.setText
		    ("GPS location: Ok"+"\r\n"
		    +"Latitude: "+location.getLatitude()+"\r\n"
		    +"Longitude: "+location.getLongitude()+"\r\n" 
		    +"Speed: "+location.getSpeed()+" KM"+"\r\n"
		    +"Time:"+location.getTime());
			WifiManager wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);
				sqldatabase sqldata=new sqldatabase(wifigps_scan.this);
		        SQLiteDatabase sql=sqldata.getWritableDatabase();
		        ContentValues cv = new ContentValues();
		        MainActivity.txt2.setText("");
		        MainActivity.txt2.append("Scan Found Wifi:"+"\r\n");
		        for(int i=0;i<wifiManager.getScanResults().size();i++){
		           MainActivity.txt2.append("SSID:"+wifiManager.getScanResults().get(i).SSID+
						    " BSSID:"+wifiManager.getScanResults().get(i).BSSID.toUpperCase()+
						    " RSSID:"+wifiManager.getScanResults().get(i).level+
						    " "+wifiManager.getScanResults().get(i).capabilities+"\r\n");
				   cv.put("SSID", wifiManager.getScanResults().get(i).SSID);
		           cv.put("BSSID", wifiManager.getScanResults().get(i).BSSID.toUpperCase());
		           cv.put("RSSID", wifiManager.getScanResults().get(i).level);
		           cv.put("Latitude", location.getLatitude());
		           cv.put("Longitude", location.getLongitude());
		           cv.put("Frequency", wifiManager.getScanResults().get(i).frequency);
		           cv.put("capabilities", wifiManager.getScanResults().get(i).capabilities);
		           sql.insert("wardriving", null, cv);
		        } 
		      sql.close();
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub	
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}};
	   @Override
	   public int onStartCommand(Intent intent, int flags, int startId) {
		  LocationManager gpsmana=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		  gpsmana.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpslist);
		  return super.onStartCommand(intent, flags, startId);
	   }
	   @Override
	   public void onDestroy() {
		  super.onDestroy();
		  LocationManager gpsmana=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		  gpsmana.removeUpdates(gpslist);
	   }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
