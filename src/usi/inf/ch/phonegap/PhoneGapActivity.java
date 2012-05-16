package usi.inf.ch.phonegap;

import org.apache.cordova.DroidGap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;



/**
 * The Class App.
 */
public class PhoneGapActivity extends DroidGap {

	private static WebView view;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
        
        // attach websocket factory
        appView.getSettings().setJavaScriptEnabled(true);
        Log.v("Activity" ,"setJavascript");
        appView.setWebChromeClient(new WebChromeClient());
        appView.addJavascriptInterface(new WebSocketFactory(appView), "WebSocketFactory");
        Log.v("Activity" ,"WebSocketFactory");

        appView.addJavascriptInterface(new DBInterface(this), "DB");
        Log.v("Activity" ,"DbInterface");

        
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        Log.v("Activity" ,"Started Service");
        
        
        
        
	}
	
	@Override
	public void onDestroy() {
		Log.v("Activity", "closing");
		super.onDestroy();
	}
	
	
}