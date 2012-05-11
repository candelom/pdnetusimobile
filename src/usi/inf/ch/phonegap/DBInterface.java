package usi.inf.ch.phonegap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
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
    
    
    
    public void activateUserPreferenceEntry(String appName) {
    	
    	Toast.makeText(mContext, "activating "+appName, Toast.LENGTH_SHORT).show();
    	LocationService.datasource.activatePreference(appName);
    	
    }
    
    
    
    public void updatePreferenceEntry(String appName, String[] values) {
    	
    	String prefValue = createPrefValue(values);
    	Toast.makeText(mContext, prefValue, Toast.LENGTH_SHORT).show();

    	Toast.makeText(mContext, "updating entry "+appName, Toast.LENGTH_SHORT).show();
    	LocationService.datasource.updatePreference(appName, prefValue);
    
    }
    
    
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
    
    
    
    public void updateXML(String path, String appName, String appNameSpace, String app_view, String app_socket) {
    	
    	
    	AssetManager assetManager = mContext.getAssets();
    	InputStream is;
    	OutputStream os;
    	try {
    		
    		os = assetManager.openFd("www/apps/my_list.xml").createOutputStream();
    		is = assetManager.open("www/apps/my_list.xml");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(is);
			Node root = doc.getFirstChild();
		
			
			Element appNode = doc.createElement("app");
			Element name = doc.createElement("name");
			name.appendChild(doc.createTextNode(appName));
			appNode.appendChild(name);
			root.appendChild(appNode);
			
			
//			
//			
//			
//			Element namespace = doc.createElement("namespace");
//			namespace.appendChild(doc.createTextNode(appNameSpace));
//			appNode.appendChild(name);
//			
//			
//			Element view = doc.createElement("view");
//			view.appendChild(doc.createTextNode(app_view));
//			appNode.appendChild(view);
//			
//			
//			
//			Element socket = doc.createElement("websocket_address");
//			socket.appendChild(doc.createTextNode(app_socket));
//			app.appendChild(socket);
			
			//write content to file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
			
		
		
    	} catch (IOException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
		catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
		
		
    	
    }

}
