
package com.myPlannerApp;
import org.json.JSONObject;

import android.graphics.Bitmap;

//import com.google.android.maps.GeoPoint;

public class BT_item {
	
	//properties
	private int itemIndex;
	private String itemId;
	private String itemNickname;
	private String itemType;
	private Bitmap image;
	private String imageName;
	private String imageURL;
	private String sortName;
	private boolean isHomeScreen;
	private JSONObject jsonObject;
	
	//only used for when this is a map-location item...
	//GeoPoint point;
	private boolean isDeviceLocation = false;
	private String annotationTitle = "";
	private String annotationSubTitle = "";
	private double latitude = 0;
	private double longitude = 0;

	//default constructor
	public BT_item(){
		this.itemIndex = -1;
		this.itemId = "";
		this.itemNickname = "";
		this.itemType = "";
		this.imageName = "";
		this.imageURL = "";
		this.sortName = "";
		this.image = null;
		this.isHomeScreen = false;
		this.jsonObject = null;
	}
	
	//constructor with values
	public BT_item(String itemId, String itemNickname, String itemType, String jsonVars){
		this.itemIndex = -1;
		this.itemId = itemId;
		this.itemNickname = itemNickname;
		this.itemType = itemType;
		this.imageName = "";
		this.imageURL = "";
		this.sortName = "";
		this.image = null;
		this.isHomeScreen = false;
		this.jsonObject = null;
	}

	//getters / setters
	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemNickname() {
		return itemNickname;
	}

	public void setItemNickname(String itemNickname) {
		this.itemNickname = itemNickname;
	}
	
	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}	
	

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public boolean isHomeScreen() {
		return isHomeScreen;
	}

	public void setIsHomeScreen(boolean isHomeScreen) {
		this.isHomeScreen = isHomeScreen;
	}
	
	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}
	
	
	//only used for map location BT_items..
	public boolean getIsDeviceLocation() {
		return isDeviceLocation;
	}
	public void setIsDeviceLocation(boolean isDeviceLocation) {
		this.isDeviceLocation = isDeviceLocation;
	}	
	
	public String getAnnotationTitle() {
		return annotationTitle;
	}
	public void setAnnotationTitle(String annotationTitle) {
		this.annotationTitle = annotationTitle;
	}
	
	public String getAnnotationSubTitle() {
		return annotationSubTitle;
	}
	public void setAnnotationSubTitle(String annotationSubTitle) {
		this.annotationSubTitle = annotationSubTitle;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}		
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}	
	
}



























