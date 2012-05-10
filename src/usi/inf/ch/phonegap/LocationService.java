package usi.inf.ch.phonegap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.NotYetConnectedException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.java_websocket.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;


public class LocationService extends Service {

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	public static PreferenceDataSource datasource;


	private static HashMap<String, String[]> displays = new HashMap<String, String[]>();

	//stores the active application sockets
	private static HashMap<String, WebSocketClient> activeSockets = new HashMap<String, WebSocketClient>();



	public HashMap<String, WebSocketClient> getSockets() {

		return activeSockets;
	}


	public HashMap<String, String[]> getDisplays() {
		return displays;
	}



	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {

		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.
			Toast.makeText(getApplicationContext(), "running", Toast.LENGTH_SHORT).show();
			readXMLFile("http://172.16.224.104:9000/assets/displays/list.xml");

			// Acquire a reference to the system Location Manager
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// Define a listener that responds to location updates
			LocationListener locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {

					// Called when a new location is found by the network location provider.
					String displayID = checkNearDisplays(location);
					if(displayID != null) {
						Log.v("displayID", displayID);
						Toast.makeText(getApplicationContext(), "displayID = "+displayID, Toast.LENGTH_SHORT).show();
						triggerPreferences(datasource);
					}
				}



				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {


				}

				public void onProviderEnabled(String provider) {


				}

				public void onProviderDisabled(String provider) {


				}


			};

			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, locationListener);

		}
	}





	@Override
	public void onCreate() {
		// Start up the thread running the service.  Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.  We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler 
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		datasource = new PreferenceDataSource(this);
		datasource.open();

		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);


		// If we get killed, after returning from here, restart
		return START_STICKY;
	}



	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}



	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show(); 
	}




	public String getTagValue(String sTag, Element eElement) {

		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}




	public void readXMLFile(String path) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(path).openStream());
			Element root = doc.getDocumentElement();

			//get the list of displays
			NodeList pd_list = root.getElementsByTagName("display");
			if(pd_list != null && pd_list.getLength() > 0) {
				for(int i = 0 ; i < pd_list.getLength();i++) {

					//get the employee element
					Element cur_display = (Element)pd_list.item(i);
					Log.v("cur_display", cur_display.toString());
					String id = getTagValue("id", cur_display);
					String lat = getTagValue("latitude", cur_display);
					String lng = getTagValue("longitude", cur_display);
					Log.v("lat", lat);
					Log.v("lng", lng);

					String[] pd_info = new String[]{lat, lng};

					Log.v("ciao", "id = "+id);

					HashMap<String, String[]> pub_displays = getDisplays();
					pub_displays.put(id, pd_info);

				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





	public String checkNearDisplays(Location user_location) {

		Log.v("check", "check near displays");

		HashMap<String, String[]> pub_displays = getDisplays();

		Set<String> keys = pub_displays.keySet();

		for(String id: keys) {

			//display position
			String[] cur_info = pub_displays.get(id); 
			double lat = new Double(cur_info[0]);
			double lng = new Double(cur_info[1]);

			double user_lat = user_location.getLatitude();
			double user_lng = user_location.getLongitude();


			Log.v("user_lat", ""+user_lat);
			Log.v("user_lng", ""+user_lng);


			Location pd_location = new Location(LocationManager.NETWORK_PROVIDER);
			pd_location.setLatitude(lat);
			pd_location.setLongitude(lng);
			float distance = user_location.distanceTo(pd_location);

			Toast.makeText(this, "dst = "+distance, Toast.LENGTH_SHORT).show(); 


			//if the display is in a range of 50 meters
			if(distance < 1000000050) {

				return id;
			}

		}

		return null;

	}





	public void triggerPreferences(PreferenceDataSource datasource) {

		Log.v("triggerPreference", "sending preferences");
		List<Preference> prefs = datasource.getAllPreferences();

		Log.v("loop prefs", "for each pref (size = "+prefs.size()+")");
		for (int i = 0; i < prefs.size(); i++) {

			Preference curPref = prefs.get(i);
			//check if the app is active
			Log.v("appName", prefs.get(i).getAppName());
			Log.v("isActive value", ""+prefs.get(i).isPrefActive());

			if(prefs.get(i).isPrefActive() == 1) {

				String appName = curPref.getAppName();
				String prefValue = curPref.getPrefValue();
				Set<String> apps = activeSockets.keySet();
				Log.v("checking", "is active or not");
				//if the socket is not already stored create a new one
				if(!apps.contains(appName)) {

					Log.v("create", "createnew socket");
					createAppSocket(appName, prefValue);

				} else {
					Log.v("send", "just send preference");
					WebSocketClient appSocket = activeSockets.get(appName);
					sendPreference(appSocket, prefValue);

				}
			}

		}

	}




	public void sendPreference(WebSocketClient socket, String prefValue) {

		Log.v("sending", "send preference");

		JSONObject msg = new JSONObject(); 
		try {
			msg.put("kind", "mobileRequest");
			msg.put("preference", prefValue);
			msg.put("displayID", "1");
			msg.put("username", "mattia");
			socket.send(msg.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (NotYetConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public void createAppSocket(String appName, final String prefValue) {

		try {

			WebSocketClient app_socket = new WebSocketClient(new URI("ws://172.16.224.104:9000/"+appName+"/socket")) {

				@Override
				public void onClose(int arg0, String arg1, boolean arg2) {
					// TODO Auto-generated method stub
					Log.v("close", "close");
				}

				@Override
				public void onError(Exception arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMessage(String arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onOpen(ServerHandshake arg0) {
					// TODO Auto-generated method stub
					Log.v("connect", "connect");
					sendPreference(this, prefValue);
				}

			};

			app_socket.connect();
			activeSockets.put(appName, app_socket);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NotYetConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}




}