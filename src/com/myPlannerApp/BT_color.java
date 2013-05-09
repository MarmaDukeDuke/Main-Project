
package com.myPlannerApp;
import android.graphics.Color;


public class BT_color {

	//returns color as integer from hex string
	public static int getColorFromHexString(String hexString){
		if(hexString.equalsIgnoreCase("clear")){
			return Color.TRANSPARENT;
		}
		if(hexString.equalsIgnoreCase("stripes")){
			return Color.TRANSPARENT;
		}
		return Color.parseColor(hexString);
	}

	
}
