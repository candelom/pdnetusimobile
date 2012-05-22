package pdnet.usi.ch.tacita;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.java_websocket.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
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
	public static LocationManager locationManager;
	
	
	private static ApplicationDBAdapter appDBAdapter;
	private static UserPreferenceDBAdapter userPrefDBAdapter;
	private static LocationListener locationListener;
	private static boolean isNext = false;
	
	
//	private static String PDNET_HOST = "http://pdnet.inf.unisi.ch:9000";
	private static String PDNET_HOST = "http://172.16.224.104:9000";
	
	
//	private static String PDNET_SOCKET_HOST = "ws://pdnet.inf.unisi.ch:9000";
	private static String PDNET_SOCKET_HOST = "ws://172.16.224.104:9000";

	
	private static String DISPLAYS_URL = "/assets/displays/list.xml";
	private static String APPS_URL = "/assets/applications/list.xml";
	
	
	
	//stores the displays
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
			Log.v("LocationService", "reading "+PDNET_HOST+DISPLAYS_URL);
			readXMLFile(PDNET_HOST+DISPLAYS_URL);
			
			// Acquire a reference to the system Location Manager
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// Define a listener that responds to location updates
			locationListener = new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					if(location != null) {
						
						String user_lat = Double.toString(location.getLatitude());
						String user_lng = Double.toString(location.getLongitude());
						String accuracy = Double.toString(location.getAccuracy());
						
						printCoordinates(user_lat, user_lng, accuracy);
						// Called when a new location is found by the network location provider.
						String displayID = checkNearDisplays(location);
						if(displayID != null) {
							printProximityMessage(displayID);
							triggerPreferences(displayID);
						} 
						else {
							System.out.println("/*** NO DISPLAYS IN PROXIMITY ***/");
						}
					}
				}


				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {

					System.out.println("/*** ON STATUS CHANGED ***/");
					
				}

				@Override
				public void onProviderEnabled(String provider) {

					System.out.println("/*** ON PROVIDER ENABLED ***/");

				}

				@Override
				public void onProviderDisabled(String provider) {

					System.out.println("/*** PROVIDER DISABLED ***/");
				}
			};

			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		}
	}

	
	




	@Override
	public void onCreate() {
		// Start up the thread running the service.  Note that we create a
		// separate thread becausef the service normally runs in the process's
		// main thread, which we don't want to block.  We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		Log.v("LocationService", "create service");
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler 
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		appDBAdapter = ApplicationDBAdapter.getInstance(this);
		Log.v("LocationService", "here"+appDBAdapter);
		appDBAdapter.open();
		appDBAdapter.deleteAllApps();
		
		userPrefDBAdapter = UserPreferenceDBAdapter.getInstance(this);
		userPrefDBAdapter.open();
		userPrefDBAdapter.deleteAll();
		
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_NOT_STICKY;
	}

	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	

	@Override
	public void onDestroy() {
		Log.v("LocationService", "stop service");
		super.onDestroy();
		stopReceivingUpdates();
		appDBAdapter.close();
		userPrefDBAdapter.close();
	}

	
	
	
	public void stopReceivingUpdates() {
		Log.v("LocationService", "STOP receiving updates");
		locationManager.removeUpdates(locationListener);
	}
	
	
		
	public String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = nlList.item(0);
		return nValue.getNodeValue();
	}


	/**
	 * Reads XML file containing display information
	 * @param path
	 */
	public void readXMLFile(String path) {

		Log.v("LocationService", "reading file");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Log.v("LocationService", "before");
			Document doc = db.parse(new URL(path).openStream());
			Log.v("LocationService", "after");
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

	
	

	/**
	 * Checks if the position of the user is in proximity of a display.
	 * If so it return the displayID.
	 * @param user_location
	 * @return
	 */
	public String checkNearDisplays(Location user_location) {

		printProximityCheckMessage();
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

			printDistance(distance);
			
			//if the display is in a range of 50 meters
			if(distance < 1000000050) {
				return id;
			}
		}
		return null;
	}
	

	public void printDistance(float distance) {
		System.out.println("/*** DISTANCE  =  " + distance + "***/");
	}

	public void printProximityCheckMessage() {
		System.out.println("/*** CHECKING NEARBY DISPLAYS ***/");
	}


	
	/**
	 * Triggers all active preferences and checks whether 
	 * the corresponding app has already an active socket. If not it call createAppSocket method.
	 * Else it calls sendPreferences method
	 * @param displayID
	 */
	public void triggerPreferences(String displayID) {

		
		printTriggerPreferenceMessage(displayID);
		
		List<UserPreference> prefs = userPrefDBAdapter.getActivePreferences();
		if(prefs.size() == 0) {
			
			System.out.println("/*** NO APPS ACTIVE ***/");
			
		} else {
			
			for (int i = 0; i < prefs.size(); i++) {
	
				UserPreference curPref = prefs.get(i);
				
				//check if the app is active
				if(prefs.get(i).isPrefActive() == 1) {
	
					String appName = curPref.getAppName();
					String prefValue = curPref.getPrefValue();
					Set<String> apps = activeSockets.keySet();
					//if the socket is not already stored create a new one
					if(!apps.contains(appName)) {
						createAppSocket(appName, prefValue, displayID);
					} else {
						WebSocketClient appSocket = activeSockets.get(appName);
						printSendMessage(appName, appSocket.getConnection().getRemoteSocketAddress().getAddress().getHostAddress(), prefValue);
						sendPreference(appSocket, prefValue, displayID, appName);
	
					}
				}
	
			}
		}

	}




	public void printTriggerPreferenceMessage(String displayID) {

		System.out.println("/*** TRIGGER PREFERENCES ***/\n\n");
			
	}


	public void sendPreference(WebSocketClient socket, String prefValue, String displayID, String appName) {

		ArrayList<String> f_values = new ArrayList<String>();
		String[] values = prefValue.split(", ");
		for(int j =0; j < values.length; j++) {
			
			f_values.add(values[j]);
		}
		
		
		try {
				JSONObject msg = new JSONObject(); 
				msg.put("kind", "mobileRequest");
				if(appName.equals("weather")) {
					msg.put("preference", values[0]);
				} 
				else {
					msg.put("preference", new JSONArray(f_values));
				}
				msg.put("displayID", displayID);
				msg.put("username", "mattia");
				socket.send(msg.toString());

		}  catch (JSONException e) {
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


	

	/**
	 * Create WebSocketClient for the given application
	 * @param appName
	 * @param prefValue
	 */
	public void createAppSocket(final String appName, final String prefValue, final String displayID) {

		System.out.println("/*** CREATE SOCKET FOR "+appName+" ***/\n\n");

		
		try {
//			final String socketAddress = "ws://172.16.224.104:9000/"+appName+"/socket";

			final String socketAddress = PDNET_SOCKET_HOST+"/"+appName+"/socket";
			WebSocketClient app_socket = new WebSocketClient(new URI(socketAddress)) {

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
					printSendMessage(appName, socketAddress, prefValue);
					sendPreference(this, prefValue, displayID, appName);
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

	
	
	
	public static void deactivateSocket(String appName) {
		
		System.out.println("/*** DEACTIVATE SOCKET "+appName+" ***/\n\n");
		if(activeSockets.keySet().contains(appName)) {
			activeSockets.remove(appName);
			System.out.println(activeSockets);
		}
		
	}
	
	
	
	public void printPreferences(ArrayList<String> values) {
		
		for(int i = 0 ; i < values.size(); i++) {
			Log.v("pref_value", values.get(i));
		}
		
	}
	
	/**
	 * Prints information of preference message
	 * @param appName
	 * @param appSocket
	 * @param prefValue
	 */
	public void printSendMessage(String appName, String appSocket, String prefValue) {
		
		System.out.println("/*** SENDING PREFERENCE ***/\n"+
							"Name : "+appName+"\n"+
							"Socket : "+appSocket+"\n"+
							"Data : "+prefValue+"\n\n");
		
	}

	
	
	/**
	 * Prints message when user is in proximity of a display.
	 */
	public void printProximityMessage(String displayID) {
		
		System.out.println("/*** NEXT TO DISPLAY ***/\n"+
				"DisplayID : "+displayID+"\n\n");
		
	}
	
	
	public void printCoordinates(String lat, String lng, String accuracy) {
		
		System.out.println("/*** USER COORDINATES ***/\n"+
							"Lat : "+lat+"\n"+
							"Lng : "+lng+"\n"+
							"Accuracy : "+accuracy+"\n"+
							"\n\n");
		
	}

	
	public static UserPreferenceDBAdapter getUserPreferenceDBAdapter() {

		return userPrefDBAdapter;
	}

	
	public static ApplicationDBAdapter getApplicationDBAdapter() {
		
		return appDBAdapter;
	}


}