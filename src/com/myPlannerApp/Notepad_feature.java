
package com.myPlannerApp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;


public class Notepad_feature extends BT_activity_base{

	private boolean didCreate = false;
	private String saveAsFileName;
	private ViewSwitcher vwSwitcher;
	private TextView txtNotes;
	private EditText txtEdit;
	private Button btnEdit;

	//////////////////////////////////////////////////////////////////////////
	//activity life-cycle events.

	//onCreate
	@Override
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        this.activityName = "Notepad_feature";
		BT_debugger.showIt(activityName + ":onCreate");	
		
		//reference to base layout..
		LinearLayout baseView = (LinearLayout)findViewById(R.id.baseView);
		
		//setup background colors...
		BT_viewUtilities.updateBackgroundColorsForScreen(this, this.screenData);
		
		//setup background images..
		if(backgroundImageWorkerThread == null){
			backgroundImageWorkerThread = new BackgroundImageWorkerThread();
			backgroundImageWorkerThread.start();
		}			
		
		//setup navigation bar...
		LinearLayout navBar = BT_viewUtilities.getNavBarForScreen(this, this.screenData);
		if(navBar != null){
			baseView.addView(navBar);
		}
		
		//inflate this screens layout file...
		LayoutInflater vi = (LayoutInflater)thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View thisScreensView = vi.inflate(R.layout.screen_notepad_feature, null);
		
		//add the view to the base view...
		baseView.addView(thisScreensView);
	    
		//saveAsFileName is unique for this screen so several note screens can be used in one app...
		saveAsFileName = "persist_" + this.screenData.getItemId() + ".txt";
		
		//reference to vwSwitcher..
		vwSwitcher = (ViewSwitcher) thisScreensView.findViewById(R.id.vwSwitcher);

		//reference to txtNotes and txtEdit. View Switcher toggles between them...
		txtNotes = (TextView) vwSwitcher.findViewById(R.id.txtNotes);
		txtEdit = (EditText) vwSwitcher.findViewById(R.id.txtEdit);
		
		//////////////////////////////////////////////////////////
		//change right side navigation image (control panel does not allow right-side nav option)...
		btnEdit = (Button) navBar.findViewById(R.id.rightButton);
		btnEdit.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		btnEdit.setText("Edit");
		btnEdit.setVisibility(View.VISIBLE);
		ImageView rightImage = (ImageView) navBar.findViewById(R.id.rightImage);
    	rightImage.setVisibility(View.GONE);
		//////////////////////////////////////////////////////////
    	
		//set clickListener for edit button...
		btnEdit.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
				startEdit();
            }
        });		
		
		//load existing notes...
		loadExistingNotes();
    	
    	
    	//flag as created..
        didCreate = true;
        
 		
	}//onCreate

	//onStart
	@Override 
	protected void onStart() {
		//BT_debugger.showIt(activityName + ":onStart");	
		super.onStart();
	}
	
    //onResume
    @Override
    public void onResume() {
       super.onResume();
       	//BT_debugger.showIt(activityName + ":onResume");
		
       //verify onCreate already ran...
       if(didCreate){

       }
       
    }
    
    //onPause
    @Override
    public void onPause() {
        super.onPause();
        //BT_debugger.showIt(activityName + ":onPause");	
    }
    
    //onStop
	@Override 
	protected void onStop(){
		super.onStop();
        //BT_debugger.showIt(activityName + ":onStop");	
	}	
	
	//onDestroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        //BT_debugger.showIt(activityName + ":onDestroy");	
    }
    
	//end activity life-cycle events
	//////////////////////////////////////////////////////////////////////////
 
    
    //loads existing notes in edit text view..
    public void loadExistingNotes(){
        BT_debugger.showIt(activityName + "loadExistingNotes");	
    
        /* NOTE
         * 	In most cases it's best to use BT_fileManager.readTextFileFromCache() to get the 
         * contents of a previously saved file. However, that method DOES NOT include a carriage
         * return at the end of each line when reading plain text files. This plugin needs to 
         * show carriage returns so we did not use the BT_fileManager class to read the cached data.
       */
            
        
		if(BT_fileManager.doesCachedFileExist(saveAsFileName)){
			String savedNotes = "";
    		try{
    	    	FileInputStream fin = myPlannerApp_appDelegate.getApplication().openFileInput(saveAsFileName);
    			if(fin != null){
    				BufferedReader r = new BufferedReader(new InputStreamReader(fin));
    				StringBuilder total = new StringBuilder();
    				String line;
    				while ((line = r.readLine()) != null) {
    					total.append(line + "\n");
    				}
    				savedNotes = total.toString();
    			}    	    	
    		}catch (Exception je){
    			savedNotes = "";
    			BT_debugger.showIt(activityName + "loadExistingNotes: ERROR 2. An exception occurred trying to read " + saveAsFileName + " from the cache.");
        	}
			
    		//set text..
			this.txtNotes.setText(savedNotes);
			this.txtEdit.setText(savedNotes);
		}    
    }
    
    //start editing notes...
    public void startEdit(){
        BT_debugger.showIt(activityName + "startEdit");	
        
        //switch from txtView to txtEdit...
        vwSwitcher.showNext();

        //show keyboard...
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtEdit, InputMethodManager.SHOW_IMPLICIT);
        
        //set cursor to end of text...
        txtEdit.setSelection(txtEdit.getText().length());
        txtEdit.requestFocus();
        
 		//change clickListener for edit button...
		btnEdit.setText("Done");
		btnEdit.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
				endEdit();
            }
        });			
        
    }    
    
    //end editing notes..
    public void endEdit(){
        BT_debugger.showIt(activityName + "endEdit");
        
        //save notes...
        String saveText = this.txtEdit.getText().toString();
        BT_fileManager.saveTextFileToCache(saveText, saveAsFileName);
		
        //re-load txtNotes (the non-editable view)
        txtNotes.setText(saveText);
        
        //hide keyboard...
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtEdit.getWindowToken(), 0);
        
        //switch from txtEdit to txtView...
        vwSwitcher.showPrevious();
       
 		//change clickListener for edit button...
        btnEdit.setText("Edit");
        btnEdit.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
				startEdit();
            }
        });	
		
    }     
    
	
	/////////////////////////////////////////////////////
	//options menu (hardware menu-button)
	@Override 
	public boolean onPrepareOptionsMenu(Menu menu) { 
		super.onPrepareOptionsMenu(menu); 
		
		 //set up dialog
        final Dialog dialog = new Dialog(this);
        
		//linear layout holds all the options...
		LinearLayout optionsView = new LinearLayout(this);
		optionsView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		optionsView.setOrientation(LinearLayout.VERTICAL);
		optionsView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		optionsView.setPadding(20, 0, 20, 20); //left, top, right, bottom
		
		//options have individual layout params
		LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(400, LayoutParams.WRAP_CONTENT);
		btnLayoutParams.setMargins(10, 10, 10, 10);
		btnLayoutParams.leftMargin = 10;
		btnLayoutParams.rightMargin = 10;
		btnLayoutParams.topMargin = 0;
		btnLayoutParams.bottomMargin = 10;
		
		//holds all the options 
		ArrayList<Button> options = new ArrayList<Button>();

		//cancel...
		final Button btnCancel = new Button(this);
		btnCancel.setText(getString(R.string.okClose));
		
		btnCancel.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                dialog.cancel();
            }
        });
		options.add(btnCancel);

		//refreshAppData (if we are on home screen)
		if(this.screenData.isHomeScreen() && myPlannerApp_appDelegate.rootApp.getDataURL().length() > 1){
			
			final Button btnRefreshAppData = new Button(this);
			btnRefreshAppData.setText(getString(R.string.refreshAppData));
			btnRefreshAppData.setOnClickListener(new OnClickListener(){
	            public void onClick(View v){
	                dialog.cancel();
	                refreshAppData();
	            }
	        });
			options.add(btnRefreshAppData);			
		}		
		
		//add each option to layout, set layoutParams as we go...
		for(int x = 0; x < options.size(); x++){
			options.get(x).setLayoutParams(btnLayoutParams);
			options.get(x).setPadding(5, 5, 5, 5);
			optionsView.addView(options.get(x));
		}
		
	
		//set content view..        
        dialog.setContentView(optionsView);
        if(options.size() > 1){
        	dialog.setTitle(getString(R.string.menuOptions));
        }else{
        	dialog.setTitle(getString(R.string.menuNoOptions));
        }
        
        //show
        dialog.show();
		return true;
		
	} 
	//end options menu
	/////////////////////////////////////////////////////	
	
	
	
}


 


