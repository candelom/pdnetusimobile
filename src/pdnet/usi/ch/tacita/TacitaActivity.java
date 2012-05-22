package pdnet.usi.ch.tacita;

import org.apache.cordova.DroidGap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;



/**
 * The Class App.
 */
public class TacitaActivity extends DroidGap {

	private static WebView view;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
        
        // attach websocket factory
        appView.getSettings().setJavaScriptEnabled(true);
        appView.setWebChromeClient(new WebChromeClient());
        appView.addJavascriptInterface(new WebSocketFactory(appView), "WebSocketFactory");
        appView.addJavascriptInterface(new JSInterface(this), "JSInterface");
        
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
	}

	
	
	@Override
	public void onDestroy() {
		Log.v("Activity", "closing");
		super.onDestroy();
	}
	
	
}