package usi.inf.ch.phonegap;

import org.apache.cordova.DroidGap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        appView.setWebChromeClient(new WebChromeClient());
        appView.addJavascriptInterface(new WebSocketFactory(appView), "WebSocketFactory");
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        
        
        appView.addJavascriptInterface(new DBInterface(this), "DB");
        
	}
	
	
}