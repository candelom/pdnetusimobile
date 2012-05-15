package usi.inf.ch.phonegap;

public class App {


	
	private long id;
	private String name;
	private String namespace;
	private String view;
	private String socketAddress;
	private String description;
	private String icon;
	

	/**
	 * Returns the applicaiton ID.
	 * @return
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Sets the application ID.
	 * @param id
	 */
	public void setId(long id) {
		
		this.id = id;
	}

	
	/**
	 * Returns the value of attribute name.
	 * @return
	 */
	public String getName() {
		
		return name;
	}
	
	
	/**
	 * Sets the value of name attribute.
	 * @param name
	 */
	public void setName(String name) {
		
		this.name = name;
	}
	
	
	/**
	 * Returns namespace attribute value.
	 * @return
	 */
	public String getNamespace() {
		
		return namespace;
	}

	
	/**
	 * Sets namespace attribute.
	 * @param namespace
	 */
	public void setNamespace(String namespace) {
		
		this.namespace = namespace;
	}
	
	
	/**
	 * Returns application view path.
	 * @return
	 */
	public String getView() {
		
		return view;
	}
	
	
	/**
	 * Sets application view path.
	 * @param view
	 */
	public void setView(String view) {
		
		this.view = view;
	}
	
	
	/**
	 * Returns application view path.
	 * @return
	 */
	public String getSocketAddress() {
		
		return socketAddress;
	}
	
	
	/**
	 * Sets application view path.
	 * @param view
	 */
	public void setSocketAddress(String socketAddress) {
		
		this.socketAddress = socketAddress;
	}
	
	/**
	 * Returns application view path.
	 * @return
	 */
	public String getDescription() {
		
		return description;
	}
	
	
	/**
	 * Sets application view path.
	 * @param view
	 */
	public void setDescription(String description) {
		
		this.description = description;
	}
	
	/**
	 * Returns application view path.
	 * @return
	 */
	public String getIcon() {
		
		return icon;
	}
	
	
	/**
	 * Sets application view path.
	 * @param view
	 */
	public void setIcon(String icon) {
		
		this.icon = icon;
	}
	
	
	
}
