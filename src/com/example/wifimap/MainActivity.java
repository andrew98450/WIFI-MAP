package com.example.wifimap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
public class MainActivity extends Activity {
	public static final int dbversion = 1;
	public static final String dbname = "wifimap.db";
    public static TextView txt1,txt2,txt3;	
    public static RadioButton rd1,rd2,rd3;
	public class tb1 implements CompoundButton.OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			LocationManager gpsmana=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
			if(isChecked){
			  WifiManager wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);
			  if(!wifiManager.isWifiEnabled()){
				  Toast.makeText(MainActivity.this,"Enabled Wifi....", Toast.LENGTH_LONG).show();
				  wifiManager.setWifiEnabled(true);
			  }
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
				                +"capabilities"+" text,"+");";
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
				  Intent start =new Intent(MainActivity.this,wifigps_scan.class);
					 startService(start);
			  }else{
				  Toast.makeText(MainActivity.this,"Please Open GPS", Toast.LENGTH_LONG).show();
				  Intent setting =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				  startActivity(setting);
			  }
			}else{
				Intent stop =new Intent(MainActivity.this,wifigps_scan.class);
				stopService(stop);
			} 
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txt1=(TextView)findViewById(R.id.textView1);
		txt2=(TextView)findViewById(R.id.textView2);
		txt3=(TextView)findViewById(R.id.textView3);
		rd1=(RadioButton)findViewById(R.id.radio0);
		rd2=(RadioButton)findViewById(R.id.radio1);
		rd3=(RadioButton)findViewById(R.id.radio2);
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
			 Intent kml = new Intent(MainActivity.this, kml.class);
			 startActivity(kml); 
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
					 TextView txt3=(TextView)findViewById(R.id.textView3);
					 txt1.setText("Location: No Data");
					 txt2.setText("Wireless: No Data");
					 txt3.setText("");
					 sqldatabase sqldata=new sqldatabase(MainActivity.this);
				     SQLiteDatabase sql=sqldata.getWritableDatabase();
					 sql.execSQL("DROP TABLE IF EXISTS wardriving");
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

