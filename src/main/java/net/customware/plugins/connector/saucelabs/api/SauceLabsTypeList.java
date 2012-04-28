package net.customware.plugins.connector.saucelabs.api;

public class SauceLabsTypeList {
	
	private String name;
	private String id;
	private String description;
	
	public SauceLabsTypeList(String id, String name, String description) {
		super();
		this.description = description;
		this.name = name;
		this.id = id;
	}
	
	public SauceLabsTypeList() {
		super();
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
