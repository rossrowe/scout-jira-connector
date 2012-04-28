package net.customware.plugins.connector.saucelabs.api;

public class SauceLabsTypeDescription {
	
	private String name;
	private String id;
	private String type;
	
	public SauceLabsTypeDescription(String type, String name, String id) {
		super();
		this.type = type;
		this.name = name;
		this.id = id;
	}
	
	public SauceLabsTypeDescription() {
		super();
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
