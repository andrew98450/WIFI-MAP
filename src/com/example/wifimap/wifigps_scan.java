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
	Handler h=new Handler();
	public static final int dbversion = 1;
	public static final String dbname = "wifimap.db";
	public double lat,lon;
	public float speed;
	public long time;
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
	public void data(){
	 WifiManager wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);
	 sqldatabase sqldata=new sqldatabase(wifigps_scan.this);
     SQLiteDatabase sql=sqldata.getWritableDatabase();
     ContentValues cv = new ContentValues();
     for(int i=0;i<wifiManager.getScanResults().size();i++){
	    cv.put("SSID", wifiManager.getScanResults().get(i).SSID);
		cv.put("BSSID", wifiManager.getScanResults().get(i).BSSID.toUpperCase());
	    cv.put("RSSID", wifiManager.getScanResults().get(i).level);
		cv.put("Latitude", lat);
	    cv.put("Longitude", lon);
	    cv.put("Frequency", wifiManager.getScanResults().get(i).frequency);
	    cv.put("capabilities", wifiManager.getScanResults().get(i).capabilities);
	    sql.insert("wardriving", null, cv);
     }
    sql.close();
   }
	Runnable wifi=new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			WifiManager wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);
			MainActivity.txt3.setText("");
		    for(int i=0;i<wifiManager.getScanResults().size();i++){	
		    MainActivity.txt2.setText("Wireless [Scanner]: OK"+" Found Wifi: "+(i+1)+" AP");
			MainActivity.txt3.append(wifiManager.getScanResults().get(i).SSID+
						"("+wifiManager.getScanResults().get(i).BSSID+
						")["+wifiManager.getScanResults().get(i).level+
						"]"+wifiManager.getScanResults().get(i).capabilities+"\r\n");
		    } 
			if(lat==0.0&&lon==0.0&&speed==0.0f&&time==0){
			if(MainActivity.rd1.isChecked()){
				MainActivity.txt1.setText
	 		    ("GPS location: Wait"+"\r\n"
	 		    +"Latitude: "+lat+" "
	 		    +"Longitude: "+lon+"\r\n" 
	 		    +"Speed: "+speed+" KM"+" "
	 		    +"Time:"+time);
			}
			if(MainActivity.rd2.isChecked()){
				MainActivity.txt1.setText
	 		    ("Network location: Wait"+"\r\n"
	 		    +"Latitude: "+lat+" "
	 		    +"Longitude: "+lon+"\r\n" 
	 		    +"Speed: "+speed+" KM"+" "
	 		    +"Time:"+time);
			}
			if(MainActivity.rd3.isChecked()){
				MainActivity.txt1.setText
	 		    ("GPS+Network location: Wait"+"\r\n"
	 		    +"Latitude: "+lat+" "
	 		    +"Longitude: "+lon+"\r\n" 
	 		    +"Speed: "+speed+" KM"+" "
	 		    +"Time:"+time);
			}
			}else{
			 if(MainActivity.rd1.isChecked()){
				MainActivity.txt1.setText
	 		    ("GPS location: Ok"+"\r\n"
	 		    +"Latitude: "+lat+" "
	 		    +"Longitude: "+lon+"\r\n" 
	 		    +"Speed: "+speed+" KM"+" "
	 		    +"Time:"+time);
	            data();
			}
			if(MainActivity.rd2.isChecked()){
				MainActivity.txt1.setText
		 		("Network location: Ok"+"\r\n"
		 		+"Latitude: "+lat+" "
		 		+"Longitude: "+lon+"\r\n" 
		 		+"Speed: "+speed+" KM"+" "
		 		+"Time:"+time);
		        data();
			}
			if(MainActivity.rd3.isChecked()){
				MainActivity.txt1.setText
		 	    ("GPS+Network location: Ok"+"\r\n"
		 		+"Latitude: "+lat+" "
		 	    +"Longitude: "+lon+"\r\n" 
		 		+"Speed: "+speed+" KM"+" "
		 		+"Time:"+time);
		        data();
			}
		  }
	      h.postDelayed(this, 2000);
		}
	};		
	public LocationListener gpslist=new LocationListener(){
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			lat = location.getLatitude();
			lon = location.getLongitude();
			speed = location.getSpeed();
			time = location.getTime();
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
		  if(MainActivity.rd1.isChecked()){
		  gpsmana.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,gpslist);
		  h.postDelayed(wifi, 2000);
		  }
		  if(MainActivity.rd2.isChecked()){
		  gpsmana.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,gpslist);
		  h.postDelayed(wifi, 2000);
		  }
		  if(MainActivity.rd3.isChecked()){
		  gpsmana.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,gpslist);
		  gpsmana.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,gpslist);
		  h.postDelayed(wifi, 2000);
		  }
		  return super.onStartCommand(intent, flags, startId);
	    }
	    @Override
	    public void onDestroy() {
		  super.onDestroy();
		  h.removeCallbacks(wifi);
		  LocationManager gpsmana=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		  gpsmana.removeUpdates(gpslist);
	    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
