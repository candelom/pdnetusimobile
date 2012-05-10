package usi.inf.ch.phonegap;

import java.util.List;

import android.content.Context;
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
	
    
    
    public void createPreferenceEntry(String username, String appName) {
    	
        Toast.makeText(mContext, "craeting test entry", Toast.LENGTH_SHORT).show();
    	LocationService.datasource.createPreference(appName, 0, "");
        
    	List<Preference> prefs = LocationService.datasource.getAllPreferences();
    	Toast.makeText(mContext, "length =  "+prefs.size(), Toast.LENGTH_SHORT).show();

    	
    }
    
    
    
    public void activateUserPreference(String appName) {
    	
    	Toast.makeText(mContext, "activating "+appName, Toast.LENGTH_SHORT).show();
    	LocationService.datasource.activatePreference(appName);
    	
    }
    
    
    public void updatePreferenceEntry(String appName, String prefValue) {
    	
    		
    	Toast.makeText(mContext, "updating entry "+appName, Toast.LENGTH_SHORT).show();
    	LocationService.datasource.updatePreference(appName, prefValue);
    	
    	
    }
    
    

}
