package com.example.wifimap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.*;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.widget.*;
import android.net.wifi.*;
import android.location.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.io.*;
public class MainActivity extends Activity {
	public Double Long; //宣告經度(Longitude)
	public Double Lat;  //宣告緯度(Latitude)
	public Handler h;
	public static final int dbversion = 1;
	public static final String dbname = "wifimap.db";
	Runnable wifiwardriving=new Runnable(){
		public void run() {
			 WifiManager wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);
			 TextView txt2=(TextView)findViewById(R.id.textView2);
		     txt2.setText("");
			 txt2.append("Scan Found Wifi:"+"\r\n");
			 for(int i=0;i<wifiManager.getScanResults().size();i++){
			 txt2.append("SSID:"+wifiManager.getScanResults().get(i).SSID+
					    " BSSID:"+wifiManager.getScanResults().get(i).BSSID.toUpperCase()+
					    " RSSID:"+wifiManager.getScanResults().get(i).level+
					    " Capabilities:"+wifiManager.getScanResults().get(i).capabilities+"\r\n");
			  }
			 sqldatabase sqldata=new sqldatabase(MainActivity.this);
			 SQLiteDatabase sql=sqldata.getWritableDatabase();
			 ContentValues cv = new ContentValues();
			 for(int i=0;i<wifiManager.getScanResults().size();i++){
				 cv.put("SSID", wifiManager.getScanResults().get(i).SSID);
		         cv.put("BSSID", wifiManager.getScanResults().get(i).BSSID.toUpperCase());
		         cv.put("RSSID", wifiManager.getScanResults().get(i).level);
		         cv.put("Latitude", MainActivity.this.Lat);
		         cv.put("Longitude", MainActivity.this.Long);
		         cv.put("Frequency", wifiManager.getScanResults().get(i).frequency);
		         cv.put("capabilities", wifiManager.getScanResults().get(i).capabilities);
		         sql.insert("wardriving", null, cv);
			} 
			sql.close();
	      }
		};
		LocationListener gpslist = new LocationListener(){
				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					TextView txt1=(TextView)findViewById(R.id.textView1);
				    MainActivity.this.Long = location.getLongitude();
				    MainActivity.this.Lat = location.getLatitude();
				    txt1.setText
				    ("GPS location: Ok"+"\r\n"
				    +"Latitude: "+MainActivity.this.Lat.toString()+"\r\n"
				    +"Longitude: "+MainActivity.this.Long.toString()+"\r\n" 
				    +"Speed: "+location.getSpeed()+" KM"+"\r\n"
				    +"Time:"+location.getTime());
				    h=new Handler();
				    h.postAtTime(wifiwardriving, 10000);
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
	private class tb1 implements CompoundButton.OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			LocationManager gpsmana=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
			if(isChecked){
			  if(gpsmana.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				  sqldatabase sqldata=new sqldatabase(MainActivity.this);
				  SQLiteDatabase sql=sqldata.getWritableDatabase();
				  try{
				  String TABLE = "CREATE TABLE wardriving"+" (" 
				            +"_id"+" integer primary key autoincrement,"
				                +"SSID"+" text,"
				                +"BSSID"+" text,"
				                +"RSSID"+" integer,"
				                +"Latitude"+" real,"
				                +"Longitude"+" real,"
				                +"Frequency"+" integer,"
				                +"capabilities"+" text"+");";
				  sql.execSQL(TABLE);
				  }catch(Exception e){
					  sql.execSQL("DROP TABLE IF EXISTS wardriving");
					  String TABLE = "CREATE TABLE wardriving"+" (" 
					            +"_id"+" integer primary key autoincrement,"
					                +"SSID"+" text,"
					                +"BSSID"+" text,"
					                +"RSSID"+" integer,"
					                +"Latitude"+" real,"
					                +"Longitude"+" real,"
					                +"Frequency"+" integer,"
					                +"capabilities"+" text"+");";
					  sql.execSQL(TABLE);
				  }
			      gpsmana.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, gpslist); //執行GPS定位(Start GPS Location)
			  }else{
				  Toast.makeText(MainActivity.this,"Please Open GPS", Toast.LENGTH_LONG).show();
				  Intent setting =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				  startActivity(setting);	//開啟設定頁面(Open Setting)
			  }
			}else{
			     gpsmana.removeUpdates(gpslist); //取消GPS定位(Cancel GPS Location)
			}
		}
	}
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ToggleButton tobt=(ToggleButton)findViewById(R.id.toggleButton1);
		tobt.setOnCheckedChangeListener(new tb1());
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
		case R.id.KML:
		   AlertDialog.Builder build = new AlertDialog.Builder(this);
		   build.setTitle(R.string.KMLExport)
		   .setMessage("Save to KML File?")
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try{
			     sqldatabase sqldata=new sqldatabase(MainActivity.this);
			     SQLiteDatabase sql=sqldata.getReadableDatabase();
			     Cursor cu=sql.query("wardriving", null, null, null, null, null, null);
			     File sdcard=Environment.getExternalStorageDirectory();
			     java.util.Date date=new java.util.Date();
			     String FILE_PATH="wifimap-"+date.getTime()+".kml";
			     File kml=new File(sdcard.getAbsoluteFile()+"/"+FILE_PATH);
		    	 FileWriter wsd = null;
				 try {
						wsd = new FileWriter(sdcard.getAbsoluteFile()+"/"+FILE_PATH);
						wsd.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\r\n"+
							          "<kml xmlns=\"http://earth.google.com/kml/2.2\">"+"\r\n"+
							          "<Document>"+"\r\n"+
							          "<name>WifiMAP</name>"+"\r\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			     for(int i=0;i<cu.getCount();i++){
			    	 cu.moveToPosition(i);
			    	String SSID=cu.getString(cu.getColumnIndex("SSID"));
			    	String BSSID=cu.getString(cu.getColumnIndex("BSSID"));
			    	int RSSID=cu.getInt(cu.getColumnIndex("RSSID"));
			    	Double lat=cu.getDouble(cu.getColumnIndex("Latitude"));
			    	Double log=cu.getDouble(cu.getColumnIndex("Longitude"));
			    	int frequency=cu.getInt(cu.getColumnIndex("Frequency"));
			    	String capabilities=cu.getString(cu.getColumnIndex("capabilities"));
						try {
						  wsd.write(
							 "<Style id="+"\""+SSID.toString()+"\""+">"+"\r\n"+
							 "<IconStyle>"+"\r\n"+
							 "<color>7f00FF00</color>"+"\r\n"+
							 "<scale>1</scale>"+"\r\n"+
							 "<Icon>"+"\r\n"+
							 "<href>http://maps.google.com/mapfiles/kml/shapes/target.png</href>"+"\r\n"+
							 "</Icon>"+"\r\n"+
							 "</IconStyle>"+"\r\n"+
							 "</Style>"+"\r\n"+
							 "<Style id="+"\""+SSID.toString()+"\""+">"+"\r\n"+
							 "<IconStyle>"+"\r\n"+
							 "<color>7f00FF00</color>"+"\r\n"+
							 "<scale>1</scale>"+"\r\n"+
							 "<Icon>"+"\r\n"+
							 "<href>http://maps.google.com/mapfiles/kml/shapes/target.png</href>"+"\r\n"+
							 "</Icon>"+"\r\n"+
							 "</IconStyle>"+"\r\n"+
							 "</Style>"+"\r\n"+
							 "<StyleMap id="+"\""+SSID.toString()+"\""+">"+"\r\n"+
							 "<Pair>"+"\r\n"+
							 "<key>normal</key>"+"\r\n"+
							 "<styleUrl>"+"#"+SSID.toString()+"</styleUrl>"+"\r\n"+
							 "</Pair>"+"\r\n"+
							 "<Pair>"+"\r\n"+
						     "<key>highlight</key>"+"\r\n"+
							 "<styleUrl>"+"#"+SSID.toString()+"</styleUrl>"+"\r\n"+
							 "</Pair>"+"\r\n"+
							 "</StyleMap>"+"\r\n"+
							 "<Placemark>"+"\r\n"+
							 "<name>"+SSID.toString()+"</name>"+"\r\n"+
							 "<styleUrl>#"+SSID.toString()+"</styleUrl>    <description>"+"<![CDATA[BSSID:"+BSSID.toString()+"<br>RSSID:"+RSSID+"<br>Frequency:"+frequency+"<br>Encryption: "+capabilities.toString()+"]]></description>"+"\r\n"+
							 "<Point>"+"\r\n"+
							 "<LookAt><longitude>"+log.toString()+"</longitude><latitude>"+lat.toString()+"</latitude><altitude>1</altitude><range>1</range><tilt>1</tilt><heading>1</heading></LookAt>      <coordinates>"+log.toString()+","+lat.toString()+",0"+"</coordinates>"+"\r\n"+
							 "</Point>"+"\r\n"+
							 "</Placemark>"+"\r\n"
							 );
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			     }
				try {
					wsd.append("</Document>"+"\r\n"+"</kml>");
					wsd.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(MainActivity.this,"Export KML File Successfully", Toast.LENGTH_LONG).show();
				}catch(Exception e){Toast.makeText(MainActivity.this,"Export KML File Fail", Toast.LENGTH_LONG).show();}
				}
		   })
		   .setNegativeButton("No", null).show();
			break;
		case R.id.Clear:
			AlertDialog.Builder build1 = new AlertDialog.Builder(this);
			   build1.setTitle(R.string.Clear)
			   .setMessage("Clear DataBase And Log?")
			   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					 TextView txt1=(TextView)findViewById(R.id.textView1);
					 TextView txt2=(TextView)findViewById(R.id.textView2);
					 sqldatabase sqldata=new sqldatabase(MainActivity.this);
				     SQLiteDatabase sql=sqldata.getWritableDatabase();
					 sql.execSQL("DROP TABLE IF EXISTS wardriving");
					 txt1.setText("GPS location:");
					 txt2.setText("Scan Found Wifi:");
					 Toast.makeText(MainActivity.this,"Clear Successfully", Toast.LENGTH_LONG).show();
				 }
			    })
			   .setNegativeButton("No", null).show();
			break;
		case R.id.About:
			 Intent about = new Intent(MainActivity.this, about.class);
			 startActivity(about);
			break;
		case R.id.Exit:
			AlertDialog.Builder build2 = new AlertDialog.Builder(this);
			   build2.setTitle(R.string.Exit)
			   .setMessage("Exit?")
			   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					System.exit(0);
				 }
			    })
			   .setNegativeButton("No", null).show();
			break;
		}
		return super.onOptionsItemSelected(item);
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

