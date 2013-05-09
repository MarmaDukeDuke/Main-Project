
package com.myPlannerApp;

import android.util.Log;


public class BT_debugger {

	
	
		public static void showIt(String messageToPrint){
		if(myPlannerApp_appDelegate.showDebugInfo){
			String LOGTAG = "ZZ";
			Log.w(LOGTAG, messageToPrint);      
		}
	}	
	
	
}












