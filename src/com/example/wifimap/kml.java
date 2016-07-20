package com.example.wifimap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class kml extends Activity {
	public static final int dbversion = 1;
	public static final String dbname = "wifimap.db";
	java.util.Date date=new java.util.Date();
    String FILE_PATH="wifimap.kml";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kml_export);
		Button bt1=(Button)findViewById(R.id.button1);
		final EditText ed1=(EditText)findViewById(R.id.editText1);
		ed1.setText(Environment.getExternalStorageDirectory()+"/"+FILE_PATH);
	  View.OnClickListener kml=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder build = new AlertDialog.Builder(kml.this)
			 .setTitle(R.string.KMLExport)
			  .setMessage("Export KML File?")
			  .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
				 sqldatabase sqldata=new sqldatabase(kml.this);
			     SQLiteDatabase sql=sqldata.getReadableDatabase();
			     Cursor cu=sql.query("wardriving", null, null, null, null, null, null);
			     File kmlfile=new File(ed1.getText().toString());
		    	 FileWriter wsd = null;
				 try {
						wsd = new FileWriter(ed1.getText().toString());
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
				Toast.makeText(kml.this,"Export KML File "+ed1.getText().toString()+" Successfully", Toast.LENGTH_LONG).show();
				}catch(Exception e){Toast.makeText(kml.this,"Export KML File "+ed1.getText().toString()+" Fail", Toast.LENGTH_LONG).show();}
			   }
			 }).setNegativeButton("No", null);
			build.show();
		}
	   };
	   bt1.setOnClickListener(kml);
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
