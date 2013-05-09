
package com.myPlannerApp;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;


public class myPlannerApp_appDelegate extends Application{


	
	public static boolean showDebugInfo = true;
	

	public static String configurationFileName = "BT_config.txt";
	
	//init the allowed input characters string. ONLY these characters will be allowed in input fields.
	public static String allowedInputCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.@!$";

	//file names for apps cached configuration file
	public static String cachedConfigDataFileName = "cachedAppConfig.txt";
	public static String cachedConfigModifiedFileName = "appModified.txt";
	
	//references to the application object for use in other classes
	public static String delegateName = "myPlannerApp_appDelegate";
	public static Context theContext;
	public static Application theApplication;
	
	//media player for screen background sounds
	public MediaPlayer audioPlayer;	
	
	//array of sound effect names and matching sound effect MediaPlayers
    public static ArrayList<String> soundEffectNames = new ArrayList<String>();
	public static ArrayList<MediaPlayer> soundEffectPlayers = new ArrayList<MediaPlayer>();
	
	//root objects
	public static BT_application rootApp;
	
	//flag for locationManager in BT_activity_base
	public static boolean foundUpdatedLocation;
	
	//when the application is created...
    public void onCreate(){
        super.onCreate();
        
        //debug
		BT_debugger.showIt("myPlannerApp_appDelegate: onCreate");

       //save a reference to the applications context
		theContext = this;
		theApplication = this;
		
		//flag locationManager
		foundUpdatedLocation = false;
		
        //init an audio player to play screen background sounds...
        initAudioPlayer();
	
        //load sound effects so they are prepared to play when needed...
        SoundEffectLoader soundEffectsLoader = new SoundEffectLoader();
        soundEffectsLoader.execute("", "");
        
 		//create the root app object.
		rootApp = new BT_application();
       
		/*
		 	*********************************************************************************
		 	*********************************************************************************
			BT_activity_root loads here. It's the starting activity (see AndroidManifest.xml)
		 	*********************************************************************************
		 	*********************************************************************************
		 */

		
    }
	
	//onLowMemory...
    public void onLowMemory(){
		BT_debugger.showIt("myPlannerApp_appDelegate: onLowMemory");
        super.onLowMemory();
    }
    
	//onTerminate...
    public void onTerminate(){
		BT_debugger.showIt("myPlannerApp_appDelegate: onTerminate");
        super.onTerminate();
    }
    
	

    //return the application itself
    public static Application getApplication(){
    	return theApplication;
    }
    
    //return the applications context
    public static Context getContext(){
        return theContext;
    }	

	//init audio player
	public void initAudioPlayer(){
		BT_debugger.showIt(delegateName + ":loadAudioPlayer");			
		audioPlayer = null;
	}    
	
	//load audio with screen data..
	public void loadAudioForScreen(BT_item theScreenData){
		BT_debugger.showIt(delegateName + ":loadAudioForScreen with nickname: " + theScreenData.getItemNickname());	
		
		//do we have a file name for the audio?
		String audioFileName = BT_strings.getJsonPropertyValue(theScreenData.getJsonObject(), "audioFileName", "");
		String audioFileURL = BT_strings.getJsonPropertyValue(theScreenData.getJsonObject(), "audioFileURL", "");

		//if we have a local file...
		if(audioFileName.length() > 1){
			
			//use the cached version of audio if available..
			if(BT_fileManager.doesCachedFileExist(audioFileName)){
				
				try{

					//reset if it's already playing...
					if(audioPlayer != null) audioPlayer = null;
					audioPlayer = MediaPlayer.create(this, null);
					audioPlayer.setVolume(0.5f, 0.5f);
					audioPlayer.setDataSource(audioFileName);	
					audioPlayer.start();
					
				}catch(Exception e){
					BT_debugger.showIt(delegateName + ":loadAudioForScreen Exception loading an audio file from the cache: " + audioFileName + " ERROR: " + e.toString());
				}	
				
			}else{
				
				//if file exists in the project..
				String audioFilePath = "file://android_asset/BT_Audio/" + audioFileName;
				if(BT_fileManager.doesProjectAssetExist("BT_Audio", audioFileName)){
					try{
						
						//reset if it's already playing...
						if(audioPlayer != null) audioPlayer = null;
						audioPlayer = MediaPlayer.create(this, Uri.parse(audioFilePath));
						audioPlayer.setVolume(0.8f, 0.8f);
						audioPlayer.start();
						
					}catch(Exception e){
						BT_debugger.showIt(delegateName + ":loadAudioForScreen Exception loading an audio file included in the Android project: "  + audioFileName + " ERROR: " + e.toString());
					}
				}
				
			}
			
		}else{
			
			//play audio URL
			if(audioFileURL.length() > 1){
				
				try{
	
					//reset if it's already playing...
					if(audioPlayer != null) audioPlayer = null;
					audioPlayer = new MediaPlayer();
					audioPlayer.setVolume(0.5f, 0.5f);
					audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					audioPlayer.setDataSource(audioFileURL);
					audioPlayer.prepare(); 
					audioPlayer.start();
					
				}catch(Exception e){
					BT_debugger.showIt(delegateName + ":loadAudioForScreen Exception loading an audio file from a URL: "  + audioFileURL + " ERROR: " + e.toString());
				}
			
			}
			
		}
		
		//if we had no audioFileName and no audioFileURL...
		if(audioFileName.length() < 1 && audioFileURL.length() < 1){
			BT_debugger.showIt(delegateName + ":loadAudioForScreen No audio name or URL for this screen.");
		}
		

	}	
	
	//Sound Effect Loader class so we can run an Async task...
	private class SoundEffectLoader extends AsyncTask<String, Void, String>{
		protected String doInBackground(String... theParams) {
			BT_debugger.showIt(delegateName + ":SoundEffectLoader:doInBackground" + " initSoundEffects");	
			BT_debugger.showIt(delegateName + ":SoundEffectLoader:doInBackground" + " initSoundEffects DISABLED");	
			String response = "";
			
			return response;
		}

	}	
	
	
	//play sound effect
	public static void playSoundEffect(String theFileName){
		BT_debugger.showIt(delegateName + ":playSoundEffect: " + theFileName);
		BT_debugger.showIt(delegateName + ":playSoundEffect: DISABLED");
		
	
	}
	
	
	
	
}



















