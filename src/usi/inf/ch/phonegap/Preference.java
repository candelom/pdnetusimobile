package usi.inf.ch.phonegap;

public class Preference {

	
	private long id;
	private String username;
	private String appName;
	private int isActive;
	private String prefValue;
	
	
	
	
	public long getId() {
		
		return id;
	}
	
	
	public void setId(long id) {
		
		this.id = id;
		
	}

	
	
	public String getUsername() {
		
		
		return username;
	}
	
	
	public void setUsername(String username) {
		
		this.username = username;
	}
	
	
	public String getAppName() {
		
		return appName;
	}

	
	public void setAppName(String appName) {
		
		this.appName = appName;
	}
	
	
	public int isPrefActive() {
		
		return isActive;
	}
	
	
	public void setIsActive(int isActive) {
		
		this.isActive = isActive;
		
	}
	
	
	public String getPrefValue() {
		
		return prefValue;
	}
	
	
	
	public void setValues(String prefValue) {
		
		this.prefValue = prefValue;
	}
	
	
	
}
