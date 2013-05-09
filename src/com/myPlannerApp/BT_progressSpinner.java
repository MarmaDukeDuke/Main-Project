
package com.myPlannerApp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

public class BT_progressSpinner extends Dialog {

	public BT_progressSpinner(Context context) {
	    super(context, R.style.BT_spinner);
	}	
	
	public static BT_progressSpinner show(Context context, CharSequence title, CharSequence message){
	    return show(context, title, message, false);
	}
	
	public static BT_progressSpinner show(Context context, CharSequence title, CharSequence message, boolean indeterminate){
	    return show(context, title, message, indeterminate, false, null);
	}
	
	public static BT_progressSpinner show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable){
	    return show(context, title, message, indeterminate, cancelable, null);
	}

	public static BT_progressSpinner show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener){
		BT_progressSpinner dialog = new BT_progressSpinner(context);
	    dialog.setTitle(title);
	    dialog.setCancelable(cancelable);
	    dialog.setOnCancelListener(cancelListener);
	    ProgressBar theBar = new ProgressBar(context);

	    Drawable d = BT_fileManager.getDrawableByName("bt_bg_progress.png");
    	theBar.setBackgroundDrawable(d);

	    theBar.setPadding(20, 20, 20, 20);
	    dialog.addContentView(theBar, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    dialog.show();
	
	    return dialog;
	}

}


