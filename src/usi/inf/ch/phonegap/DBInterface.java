package usi.inf.ch.phonegap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class DBInterface {
	
	Context mContext;

	
    /** Instantiate the interface and set the context */
    DBInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    public void showToast(String toast) {
    	
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
	
    
    /**
     * If pref doesn't exist it creates a new one.
     * @param username
     * @param appName
     */
    public void createPreferenceEntry(String username, String appName) {
    	
        if(LocationService.datasource.getPreference(appName) == null) {
			Toast.makeText(mContext, "creating entry", Toast.LENGTH_SHORT).show();
        	LocationService.datasource.createPreference(appName, 0, "");
        	
        } else {
        	
        	Toast.makeText(mContext, "entry already exists", Toast.LENGTH_SHORT).show();
        }
        
    	List<Preference> prefs = LocationService.datasource.getAllPreferences();
    	Toast.makeText(mContext, "length =  "+prefs.size(), Toast.LENGTH_SHORT).show();

    }
    
    
    
    /**
     * Activate pref entry
     * @param appName
     */
    public void activateUserPreferenceEntry(String appName) {
    	System.out.println("/*** ACTIVATE APP SOCKET "+appName+" ***/");
    	LocationService.datasource.activatePreference(appName);
    }
    
    
    /**
     * Deactivate app and socket
     * @param appName
     */
    public void deactivateUserPreferenceEntry(String appName) {

    	System.out.println("/*** DEACTIVATE APP SOCKET ***/");
    	LocationService.datasource.deactivatePreference(appName);
    	
    }
    
    
    
    /**
     * Update pref entry.
     * @param appName
     * @param values
     */
    public void updatePreferenceEntry(String appName, String[] values) {
    	
    	String prefValue = createPrefValue(values);
    	Toast.makeText(mContext, prefValue, Toast.LENGTH_SHORT).show();

    	Toast.makeText(mContext, "updating entry "+appName, Toast.LENGTH_SHORT).show();
    	LocationService.datasource.updatePreference(appName, prefValue);
    	
    }
    
    /**
     * Format given values to a string
     * @param values
     * @return
     */
    public String createPrefValue(String[] values) {
    	
    	String prefValue = "";
    	for(int i = 0; i < values.length; i++) {
    		if(i == values.length-1) {
    			prefValue += values[i];
    		} 
    		else {
    			prefValue += values[i] + ", ";
    		}
    	}
    	return prefValue;
    }
    
    
   /**
    * Create application entry.
    * @param name
    * @param namespace
    * @param view
    * @param socketAddress
    * @param description
    * @param icon
    */
   public void addApp(String name, String namespace, String view, String socketAddress, String description, String icon) {
	   
	   Toast.makeText(mContext, "adding app", Toast.LENGTH_SHORT).show();
   	   LocationService.datasource.createApp(name, namespace, view, socketAddress, description, icon);
	   
   }
   
   
  public void removeApp(String namespace) {
	   
	   Log.v("DB Interface", "removing "+namespace);
   	   LocationService.datasource.deleteApp(namespace);
	   
   }
   
   
   public String getInstalledApps() {
	  
	   Toast.makeText(mContext, "loading apps", Toast.LENGTH_SHORT).show();
	   List<App> apps = LocationService.datasource.getAllApps();
	   JSONArray json_apps = new JSONArray();
	   for(int j = 0; j < apps.size(); j++) {
			   App cur_app = apps.get(j);
			   JSONObject app_obj = new JSONObject();
			   try {
				   app_obj.put("id", cur_app.getId());
				   app_obj.put("name", cur_app.getName());
				   app_obj.put("namespace", cur_app.getNamespace());
				   app_obj.put("view", cur_app.getView());
				   app_obj.put("socket", cur_app.getSocketAddress());
				   app_obj.put("description", cur_app.getDescription());
				   app_obj.put("icon", cur_app.getIcon());
				   json_apps.put(app_obj);
			   } catch (JSONException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
			   }
			   
	   }
	   return json_apps.toString();
	   
   }
   
   
   
   public boolean isAppActive(String appName) {
	   
	   Toast.makeText(mContext, "checking activeness", Toast.LENGTH_SHORT).show();
   	   boolean isActive = LocationService.datasource.checkPrefStatus(appName);
   	   return isActive;
	   
   }
   
   
   public boolean isAppInstalled(String appName) {
	   
	   Toast.makeText(mContext, "checking install", Toast.LENGTH_SHORT).show();
   	   boolean isInstalled = LocationService.datasource.checkAppStatus(appName);
   	   return isInstalled;
	   
   }
   
   
   public String getCurrentLocation() {
	   
	   Location lastLocation = LocationService.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	  
	   String lat = Double.toString(lastLocation.getLatitude());
	   String lng = Double.toString(lastLocation.getLongitude());
	   String accuracy = Float.toString(lastLocation.getAccuracy());
	   
	   JSONObject locObj = new JSONObject();
	   try {
		   locObj.put("lat", lat);
		   locObj.put("lng", lng);
		   locObj.put("accuracy", accuracy);
	   } catch (JSONException e) {
		   // TODO Auto-generated catch block
			e.printStackTrace();
	   }
	   return locObj.toString();
   }
   

}
