
package com.myPlannerApp;

public class BT_user {
	
	private String objectName = "BT_user";
	private boolean isLoggedIn;
	private String userDisplayName = "";
	private String userId = "";
	private String userEmail = "";
	private String userLogInId = "";
	private String userLogInPassword = "";
	
	//constructor
	public BT_user(){
		BT_debugger.showIt(objectName + ": Creating root-user object.");
		
		//init vars...
		isLoggedIn = false;
		userDisplayName = BT_strings.getPrefString("userDisplayName");
		userId = BT_strings.getPrefString("userDisplayName");
		userEmail = BT_strings.getPrefString("userDisplayName");
		userLogInId = BT_strings.getPrefString("userDisplayName");
		userLogInPassword = BT_strings.getPrefString("userLogInPassword");
		
		//if we have an id in user preferences we are logged in...
		if(BT_strings.getPrefString("userId").length() > 1 || BT_strings.getPrefString("userGuid").length() > 1){
			isLoggedIn = true;
		}else{
			isLoggedIn = false;
		}
		
		
	}

	//getters // setters
	public boolean getIsLoggedIn() {
		return isLoggedIn;
	}
	
	public void setIsLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getUserEmail() {
		return userEmail;
	}


	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}


	public String getUserLogInId() {
		return userLogInId;
	}


	public void setUserLogInId(String userLogInId) {
		this.userLogInId = userLogInId;
	}


	public String getUserLogInPassword() {
		return userLogInPassword;
	}

	public void setUserLogInPassword(String userLogInPassword) {
		this.userLogInPassword = userLogInPassword;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}


	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}
	
	
	
	
}







