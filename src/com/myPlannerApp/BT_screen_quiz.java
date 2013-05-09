
package com.myPlannerApp;

import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class BT_screen_quiz extends BT_activity_base{
	
	public boolean didCreate = false;
	private boolean didLoadData = false;
	private DownloadScreenDataWorker downloadScreenDataWorker;
	private String JSONData = "";
	private ArrayList<BT_item> childItems = null;
	public ArrayList<BT_item> questionPool;

	//bonus images...
	public ArrayList<Bitmap> bonusImages;
	
	//media players for sound effects...
	public MediaPlayer soundPlayerCorrect;
	public MediaPlayer soundPlayerIncorrect;
	public MediaPlayer soundPlayerFinished;
	public MediaPlayer soundPlayerReward;

	//properties from JSON
	private String dataURL = "";
	private String saveAsFileName = "";

	private int quizStartCountdownSeconds = 3;
	private int quizCountdownSeconds = 0;
	private int quizNumberOfQuestions = 0;
	private int quizQuestionDelay = 0;
	private String quizRandomizeQuestions = "";
	private String quizShowCorrectAnswers = "";
	private String quizFinishScreenItemId = "";
	private int quizPointsPerAnswer = 0;
	private int quizRewardIfPointsOver = 0;
	private String quizRewardScreenItemId = "";
	private String quizSoundEffectFileNameCorrect = "";
	private String quizSoundEffectFileNameIncorrect = "";
	private String quizSoundEffectFileNameFinished = "";
	private String quizSoundEffectFileNameReward = "";
	private String quizShowTimer = "";
	private String sendResultsToURL = "";
	private int quizImageCornerRadius = 0;
	private String quizFontColorQuestions = "";
	private String quizFontColorAnswers = "";
	private String quizButtonColorAnswers = "";
	private String quizButtonColorCorrect = "";
	private String quizButtonColorIncorrect = "";
	private int quizQuestionFontSizeSmallDevice = 0;
	private int quizButtonFontSizeSmallDevice = 0;
	private int quizQuestionFontSizeLargeDevice = 0;
	private int quizButtonFontSizeLargeDevice = 0;
	
	//quiz vars...
	public int numberRight;
	public int numberWrong;
	public int numberStreak;
	public int currentQuestion;
	public int totalSeconds;
	public int totalScore;
	public Long startTime;
	public int allowNext;
	public int quizIsComplete;
	
	//UI Components...
	public ImageView imgQuestion;
	public ImageView imgRight;
	public ImageView imgWrong;
	public ImageView imgBonus;
	public TextView txtQuestion;
	public TextView txtScore;
	public TextView txtTimer;
	public Button btnAnswer_1;
	public Button btnAnswer_2;
	public Button btnAnswer_3;
	public Button btnAnswer_4;
	public RelativeLayout btnPanel;

	//////////////////////////////////////////////////////////////////////////
	//activity life-cycle events.
	
	//onCreate
	@Override
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        this.activityName = "BT_screen_quiz";
		BT_debugger.showIt(activityName + ":onCreate");	
		
		
		//prevent this screen from rotating, FORCE portrait...
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//get reference to baseView...
		LinearLayout baseView = (LinearLayout)findViewById(R.id.baseView);
		
		//setup background colors...
		BT_viewUtilities.updateBackgroundColorsForScreen(this, this.screenData);
		
		//setup navigation bar...
		LinearLayout navBar = BT_viewUtilities.getNavBarForScreen(this, this.screenData);
		if(navBar != null){
			baseView.addView(navBar);
		}
		
		//init properties for JSON data...
		childItems = new ArrayList<BT_item>();
		dataURL = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "dataURL", "");
		saveAsFileName = this.screenData.getItemId() + "_screenData.txt";
		sendResultsToURL = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "sendResultsToURL", "");

		quizRandomizeQuestions = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizRandomizeQuestions", "1");
		quizShowCorrectAnswers = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizShowCorrectAnswers", "1");
		quizFinishScreenItemId = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizFinishScreenItemId", "");
		quizRewardScreenItemId = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizRewardScreenItemId", "");
		quizSoundEffectFileNameCorrect = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizSoundEffectFileNameCorrect", "");
		quizSoundEffectFileNameIncorrect = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizSoundEffectFileNameIncorrect", "");
		quizSoundEffectFileNameFinished = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizSoundEffectFileNameFinished", "");
		quizSoundEffectFileNameReward = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizSoundEffectFileNameReward", "");
		quizShowTimer = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizShowTimer", "1");
		
		quizFontColorQuestions = BT_strings.getStyleValueForScreen(this.screenData, "quizFontColorQuestions", "#FFFFFF");
		quizFontColorAnswers = BT_strings.getStyleValueForScreen(this.screenData, "quizFontColorAnswers", "#000000");
		quizButtonColorAnswers = BT_strings.getStyleValueForScreen(this.screenData, "quizButtonColorAnswers", "#C7C7C7");
		quizButtonColorCorrect = BT_strings.getStyleValueForScreen(this.screenData, "quizButtonColorCorrect", "#336600");
		quizButtonColorIncorrect = BT_strings.getStyleValueForScreen(this.screenData, "quizButtonColorIncorrect", "#990000");

		quizNumberOfQuestions = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizNumberOfQuestions", "0"));
		quizQuestionDelay = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizQuestionDelay", "2"));
		quizPointsPerAnswer = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizPointsPerAnswer", "150"));
		quizRewardIfPointsOver = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizRewardIfPointsOver", "0"));
		quizImageCornerRadius = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizImageCornerRadius", "10"));
		quizQuestionFontSizeSmallDevice = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizQuestionFontSizeSmallDevice", "25"));
		quizButtonFontSizeSmallDevice = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizButtonFontSizeSmallDevice", "18"));
		quizQuestionFontSizeLargeDevice = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizQuestionFontSizeLargeDevice", "30"));
		quizButtonFontSizeLargeDevice = Integer.parseInt(BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "quizButtonFontSizeLargeDevice", "20"));
		
	   	//tell android we want device to control volume for possible sound effects...
    	this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    	
    	//init sound effect players to null..."startCountdown" will load them with media...
    	soundPlayerCorrect = null;
    	soundPlayerIncorrect = null;
    	soundPlayerFinished = null;
    	soundPlayerReward = null;

		numberRight = 0;
		numberWrong = 0;
		numberStreak = 0;
		currentQuestion = 0;
		totalSeconds = 0;
		totalScore = 0;
		allowNext = 0;
		quizIsComplete = 0;

		//init child items array...
		childItems = new ArrayList<BT_item>();
		dataURL = BT_strings.getJsonPropertyValue(this.screenData.getJsonObject(), "dataURL", "");
		saveAsFileName = this.screenData.getItemId() + "_screenData.txt";
		if(dataURL.length() < 1) BT_fileManager.deleteFile(saveAsFileName);
		
		//inflate this screens layout file...
		LayoutInflater vi = (LayoutInflater)thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View thisScreensView = vi.inflate(R.layout.screen_quiz, null);
		
		//init image components...
    	imgQuestion = (ImageView) thisScreensView.findViewById(R.id.imgQuestion);
    	imgRight = (ImageView) thisScreensView.findViewById(R.id.imgRight);
    	imgRight.setVisibility(INVISIBLE);
    	imgWrong = (ImageView) thisScreensView.findViewById(R.id.imgWrong);
    	imgWrong.setVisibility(INVISIBLE);
    	imgBonus = (ImageView) thisScreensView.findViewById(R.id.imgBonus);
    	imgBonus.setVisibility(INVISIBLE);
		
       	//init text view components...	
    	txtQuestion = (TextView) thisScreensView.findViewById(R.id.txtQuestion);
    	txtScore = (TextView) thisScreensView.findViewById(R.id.txtScore);
    	txtTimer = (TextView) thisScreensView.findViewById(R.id.txtTimer);
    	
    	//set question text font color...
    	txtQuestion.setTextColor(BT_color.getColorFromHexString(quizFontColorQuestions));
    	
    	
    	//init button components...
    	btnAnswer_1 = (Button) thisScreensView.findViewById(R.id.btnAnswer_1);
        btnAnswer_2 = (Button) thisScreensView.findViewById(R.id.btnAnswer_2);
        btnAnswer_3 = (Button) thisScreensView.findViewById(R.id.btnAnswer_3);
        btnAnswer_4 = (Button) thisScreensView.findViewById(R.id.btnAnswer_4);
        btnPanel = (RelativeLayout) thisScreensView.findViewById(R.id.layoutButtons);
        btnPanel.setVisibility(INVISIBLE);
   	
        //set button text colors...
		btnAnswer_1.setTextColor(BT_color.getColorFromHexString(quizFontColorAnswers));
		btnAnswer_2.setTextColor(BT_color.getColorFromHexString(quizFontColorAnswers));
		btnAnswer_3.setTextColor(BT_color.getColorFromHexString(quizFontColorAnswers));
		btnAnswer_4.setTextColor(BT_color.getColorFromHexString(quizFontColorAnswers));
        
		//set button background colors...
		btnAnswer_1.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorAnswers));
		btnAnswer_2.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorAnswers));
		btnAnswer_3.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorAnswers));
		btnAnswer_4.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorAnswers));
	   	
		//set font size for buttons and question, this depends on device type...
		if(myPlannerApp_appDelegate.rootApp.getRootDevice().getIsLargeDevice()){
			txtQuestion.setTextSize(quizQuestionFontSizeLargeDevice);
			btnAnswer_1.setTextSize(quizButtonFontSizeLargeDevice);
			btnAnswer_2.setTextSize(quizButtonFontSizeLargeDevice);
			btnAnswer_3.setTextSize(quizButtonFontSizeLargeDevice);
			btnAnswer_4.setTextSize(quizButtonFontSizeLargeDevice);
		}else{
			txtQuestion.setTextSize(quizQuestionFontSizeSmallDevice);
			btnAnswer_1.setTextSize(quizButtonFontSizeSmallDevice);
			btnAnswer_2.setTextSize(quizButtonFontSizeSmallDevice);
			btnAnswer_3.setTextSize(quizButtonFontSizeSmallDevice);
			btnAnswer_4.setTextSize(quizButtonFontSizeSmallDevice);
		}
		
	    //setup click handlers
        btnAnswer_1.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
            	answerClick(v);
            }
        });  
        btnAnswer_2.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
            	answerClick(v);
            }
        });         
        btnAnswer_3.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
            	answerClick(v);
            }
        }); 
        btnAnswer_4.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
            	answerClick(v);
            }
        }); 
        
        //load up bonus images for better performance
        bonusImages = new ArrayList<Bitmap>(); 

		Bitmap tmpImg;
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.bt_blank);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.bt_blank);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.bt_blank);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.quiz_3x);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.quiz_4x);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.quiz_5x);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.quiz_6x);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.quiz_7x);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.quiz_8x);				
			bonusImages.add(tmpImg);
		tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.quiz_9x);				
			bonusImages.add(tmpImg);
			tmpImg = BitmapFactory.decodeResource(getResources(),R.drawable.quiz_10x);				
		bonusImages.add(tmpImg);
		
		//add the view to the base view...
		baseView.addView(thisScreensView);
		
		//invalidate view to it repaints...
		baseView.invalidate();
		
		//flag as created..
        didCreate = true;
        
 		
	}//onCreate

	//onStart
	@Override 
	protected void onStart() {
		//BT_debugger.showIt(activityName + ":onStart");	
		super.onStart();
		
		//ignore this if we already created the screen...
		if(!didLoadData){
			
			if(saveAsFileName.length() > 1){
				
				//check cache...
				String parseData = "";
				if(BT_fileManager.doesCachedFileExist(saveAsFileName)){
					BT_debugger.showIt(activityName + ":onStart using cached screen data");	
					parseData = BT_fileManager.readTextFileFromCache(saveAsFileName);
					parseScreenData(parseData);
				}else{
					//get data from URL if we have one...
					if(this.dataURL.length() > 1){
						BT_debugger.showIt(activityName + ":onStart downloading screen data from URL");	
						refreshScreenData();
					}else{
						//parse with "empty" data...
						BT_debugger.showIt(activityName + ":onStart using data from app's configuration file");	
						parseScreenData("");
					}
				}
					
			}//saveAsFileName
		}//did load data
		
		
	}
	
    //onResume
    @Override
    public void onResume() {
       super.onResume();
       	//BT_debugger.showIt(activityName + ":onResume");       
       
    }
    
    //onPause
    @Override
    public void onPause() {
        super.onPause();
        //BT_debugger.showIt(activityName + ":onPause");
        
        //kill possible sound effect players...
        killQuizSounds();
        
    }
    
	
	//onStop
	@Override 
	protected void onStop(){
		super.onStop();
        //BT_debugger.showIt(activityName + ":onStop");	
		if(downloadScreenDataWorker != null){
			boolean retry = true;
			downloadScreenDataWorker.setThreadRunning(false);
			while(retry){
				try{
					downloadScreenDataWorker.join();
					retry = false;
				}catch (Exception je){
				}
			}
		}
		
		//kill timer for countdown timer...
		if(updateCountdownHandler != null){
			updateCountdownHandler.removeCallbacks(mUpdateCountdownTask);
		}
		
		//kill timer for running quiz timer...
		if(updateTimeHandler != null){
			updateTimeHandler.removeCallbacks(mUpdateTimeTask);
		}
		if(delayHandler != null){
			delayHandler.removeCallbacks(mDelayTask);
		}
		
		//kill possible sound effect players...
        killQuizSounds();

		
		
	}	
	
	
	//onDestroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        //BT_debugger.showIt(activityName + ":onDestroy");	
    }
    
	//end activity life-cycle events
	//////////////////////////////////////////////////////////////////////////
  
    
    //parse screenData...
    public void parseScreenData(String theJSONString){
        BT_debugger.showIt(activityName + ":parseScreenData");	
        //BT_debugger.showIt(activityName + ":parseScreenData " + theJSONString);
		//parse JSON string
    	
		//hide controls
		txtTimer.setVisibility(INVISIBLE);
		txtScore.setVisibility(INVISIBLE);
		txtQuestion.setVisibility(INVISIBLE);		
		btnPanel.setVisibility(INVISIBLE);		
        
		// init question pool...
		questionPool = new ArrayList<BT_item>(); 
		childItems = new ArrayList<BT_item>(); 

		try{

            //if theJSONString is empty, look for child items in this screen's config data..
    		JSONArray items = null;
    		if(theJSONString.length() < 1){
    			if(this.screenData.getJsonObject().has("childItems")){
        			items =  this.screenData.getJsonObject().getJSONArray("childItems");
    			}
    		}else{
        		JSONObject raw = new JSONObject(theJSONString);
        		if(raw.has("childItems")){
        			items =  raw.getJSONArray("childItems");
        		}
    		}
  
    		//loop items..
    		if(items != null){
                for (int i = 0; i < items.length(); i++){
                	
                	JSONObject tmpJson = items.getJSONObject(i);
                	BT_item tmpItem = new BT_item();
                	if(tmpJson.has("itemId")) tmpItem.setItemId(tmpJson.getString("itemId"));
                	if(tmpJson.has("itemType")) tmpItem.setItemType(tmpJson.getString("itemType"));
                	tmpItem.setJsonObject(tmpJson);
                	childItems.add(tmpItem);
            		questionPool.add(tmpItem);
                	
                }//for
                
                
                //figure out quizNumberOfQuestions...
                if(quizNumberOfQuestions < 1){
                	quizNumberOfQuestions = childItems.size();
                }else{
                	
                	if(childItems.size() <= quizNumberOfQuestions){
                		quizNumberOfQuestions = childItems.size();
                	}
                	
                }
                
                
                //flag data loaded...
                didLoadData = true;

                //hide progress...
                hideProgress();
                
                //start countdown to quiz start..
                startCountdown();
                
                //bail...
                return;
    			
    		}else{
    			BT_debugger.showIt(activityName + ":parseScreenData NO CHILD ITEMS?");
    			
    		}
    	}catch(Exception e){
			BT_debugger.showIt(activityName + ":parseScreenData EXCEPTION " + e.toString());
    	}
        
        //hide progress...
        hideProgress();
        
    }
    
    //refresh screenData
    public void refreshScreenData(){
        BT_debugger.showIt(activityName + ":refreshScreenData");	
        showProgress(null, null);
        
		//hide controls
		txtTimer.setVisibility(INVISIBLE);
		txtScore.setVisibility(INVISIBLE);
		txtQuestion.setVisibility(INVISIBLE);		
		btnPanel.setVisibility(INVISIBLE);		
        
        
        if(dataURL.length() > 1){
	      	downloadScreenDataWorker = new DownloadScreenDataWorker();
        	downloadScreenDataWorker.setDownloadURL(dataURL);
        	downloadScreenDataWorker.setSaveAsFileName(saveAsFileName);
        	downloadScreenDataWorker.setThreadRunning(true);
        	downloadScreenDataWorker.start();
        }else{
            BT_debugger.showIt(activityName + ":refreshScreenData NO DATA URL for this screen? Not downloading.");	
        }
        
    }     
    
    //sends results to URL on quiz end (not fired if sendResultsToURL is empty)...
    public void sendQuizResultsToURL(){
        BT_debugger.showIt(activityName + ":sendQuizResultsToURL");	
        
        if(sendResultsToURL.length() > 1){
        	String useURL = sendResultsToURL;
        	useURL += "&totalPoints=" + totalScore;
        	useURL += "&totalSeconds=" + totalSeconds;
        	useURL += "&numberQuestions=" + totalScore;
        	useURL += "&numberCorrect=" + numberRight;
        	useURL += "&numberIncorrect=" + numberWrong;
        	
        	//merge remaining vars...
        	useURL = BT_strings.mergeBTVariablesInString(dataURL);
			BT_debugger.showIt(activityName + ": sending results to: " + useURL);
			
			//fetch the URL (ignore results)...
			BT_downloader objDownloader = new BT_downloader(useURL);
			objDownloader.setSaveAsFileName(""); 
			objDownloader.downloadTextData();
        	
        }
        
        
    }
    
    //setup quiz sound effect players...
    public void setupQuizSounds(){
        BT_debugger.showIt(activityName + ":setupQuizSounds");	

		//four possible sound effect file names provided in JSON. If a file name is setup, the file must
		//exist in the /assets/BT_Audio folder
		//	quizSoundEffectFileNameCorrect
		//	quizSoundEffectFileNameIncorrect
		//	quizSoundEffectFileNameFinished
		//	quizSoundEffectFileNameReward
		
        
		//quizSoundEffectFileNameCorrect...
    	if(quizSoundEffectFileNameCorrect.length() > 1){
			if(BT_fileManager.doesProjectAssetExist("BT_Audio", quizSoundEffectFileNameCorrect)){
				try{
				    AssetFileDescriptor afd = getAssets().openFd("BT_Audio/" + quizSoundEffectFileNameCorrect);
					soundPlayerCorrect = new MediaPlayer();
					soundPlayerCorrect.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					soundPlayerCorrect.prepareAsync();
				}catch(Exception e){
					BT_debugger.showIt(activityName + ":EXCEPTION setting up soundPlayerCorrect. Message: " + e.toString());	
				}
			}
    	}
    	
		//quizSoundEffectFileNameIncorrect...
    	if(quizSoundEffectFileNameIncorrect.length() > 1){
			if(BT_fileManager.doesProjectAssetExist("BT_Audio", quizSoundEffectFileNameIncorrect)){
				try{
				    AssetFileDescriptor afd = getAssets().openFd("BT_Audio/" + quizSoundEffectFileNameIncorrect);
					soundPlayerIncorrect = new MediaPlayer();
					soundPlayerIncorrect.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					soundPlayerIncorrect.prepareAsync();
				}catch(Exception e){
					BT_debugger.showIt(activityName + ":EXCEPTION setting up soundPlayerIncorrect. Message: " + e.toString());	
				}
			}
    	}
    	
		//quizSoundEffectFileNameFinished...
    	if(quizSoundEffectFileNameFinished.length() > 1){
			if(BT_fileManager.doesProjectAssetExist("BT_Audio", quizSoundEffectFileNameFinished)){
				try{
				    AssetFileDescriptor afd = getAssets().openFd("BT_Audio/" + quizSoundEffectFileNameFinished);
					soundPlayerFinished = new MediaPlayer();
					soundPlayerFinished.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					soundPlayerFinished.prepareAsync();
				}catch(Exception e){
					BT_debugger.showIt(activityName + ":EXCEPTION setting up quizSoundEffectFileNameFinished. Message: " + e.toString());	
				}
			}
    	}
    	
		//quizSoundEffectFileNameReward...
    	if(quizSoundEffectFileNameReward.length() > 1){
			if(BT_fileManager.doesProjectAssetExist("BT_Audio", quizSoundEffectFileNameReward)){
				try{
				    AssetFileDescriptor afd = getAssets().openFd("BT_Audio/" + quizSoundEffectFileNameReward);
					soundPlayerReward = new MediaPlayer();
					soundPlayerReward.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					soundPlayerReward.prepareAsync();
				}catch(Exception e){
					BT_debugger.showIt(activityName + ":EXCEPTION setting up quizSoundEffectFileNameReward. Message: " + e.toString());	
				}
			}
    	}
    	
    }
    
    //kill possible sound effects..
    public void killQuizSounds(){
        BT_debugger.showIt(activityName + ":killQuizSounds");	
        
        //soundPlayerCorrect...
        if(soundPlayerCorrect != null){
        	if(soundPlayerCorrect.isPlaying()) soundPlayerCorrect.stop();
        	soundPlayerCorrect.release();
        	soundPlayerCorrect = null;
        }
        
        //soundPlayerIncorrect...
        if(soundPlayerIncorrect != null){
        	if(soundPlayerIncorrect.isPlaying()) soundPlayerIncorrect.stop();
        	soundPlayerIncorrect.release();
        	soundPlayerIncorrect = null;
        }
    	
        //soundPlayerFinished...
        if(soundPlayerFinished != null){
        	if(soundPlayerFinished.isPlaying()) soundPlayerFinished.stop();
        	soundPlayerFinished.release();
        	soundPlayerFinished = null;
        }

        //soundPlayerReward...
        if(soundPlayerReward != null){
        	if(soundPlayerReward.isPlaying()) soundPlayerReward.stop();
        	soundPlayerReward.release();
        	soundPlayerReward = null;
        }
    
    }

    
	//answer button clicks
	public void answerClick(View v){
        BT_debugger.showIt(activityName + ":answerClick");	
		Button clickedButton = (Button) v;
	   
	   //buttons depend on whether quiz is over or not
	   if(quizIsComplete < 1){
		   String selectedAnswer = clickedButton.getText().toString();
	        BT_debugger.showIt(activityName + ":answerClick The selected answer was: \"" + selectedAnswer + "\"");	

	        //prevents super fast button taps..
		   if(allowNext > 0){
			   if(currentQuestion > -1 && childItems.size() > 0){
				  
				  //flag..
				  allowNext = 0;
				   
				  BT_item tmpQuestion = childItems.get(currentQuestion);
				  
				   String correctAnswer = BT_strings.getJsonPropertyValue(tmpQuestion.getJsonObject(), "correctAnswerText", "");

				   //do we show correct answers???
				   if(quizShowCorrectAnswers.equalsIgnoreCase("1")){
				   
					   //change button backgrounds, red for wrong, green for right
					   if(correctAnswer.equals(btnAnswer_1.getText().toString())){
						   btnAnswer_1.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorCorrect));
					   }else{
						   btnAnswer_1.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorIncorrect));
					   }
					   if(correctAnswer.equals(btnAnswer_2.getText().toString())){
						   btnAnswer_2.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorCorrect));
					   }else{
						   btnAnswer_2.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorIncorrect));
					   }
					   if(correctAnswer.equals(btnAnswer_3.getText().toString())){
						   btnAnswer_3.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorCorrect));
					   }else{
						   btnAnswer_3.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorIncorrect));
					   }
					   if(correctAnswer.equals(btnAnswer_4.getText().toString())){
						   btnAnswer_4.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorCorrect));
					   }else{
						   btnAnswer_4.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorIncorrect));
					   }
				   
				   }//show correct answers...
				   
				   //show image, calculate score..
				   if(selectedAnswer.toString().equals(correctAnswer.toString())){
					   
					   //counts..
					   numberRight = (numberRight + 1);
					   numberStreak = (numberStreak + 1);
					   if(numberStreak >= 10) numberStreak = 10;
					   
					   //set score
					   if(numberStreak > 2){
						   totalScore = (totalScore + (quizPointsPerAnswer * numberStreak));
					   }else{
						   totalScore = (totalScore + quizPointsPerAnswer);
					   }
		
					   //increment score
					   txtScore.setText("" + totalScore);
					   
					   //set bonus image
					   imgBonus.setImageBitmap(bonusImages.get(numberStreak));
					   			
					   //play possible correct sound...
					   if(soundPlayerCorrect != null){
						   soundPlayerCorrect.start();
					   }
					   
					   //fade images in...
					   fadeImageIn(imgRight);
					   fadeImageIn(imgBonus);
					   
					   //fade images out...
					   fadeImageOut(imgRight);
					   fadeImageOut(imgBonus);
					   
				   }else{
					   
					   //play possible incorrect sound...
					   if(soundPlayerIncorrect != null){
						   soundPlayerIncorrect.start();
					   }

					   numberWrong = (numberWrong + 1);
					   numberStreak = 0;
					   fadeImageIn(imgWrong);
					   fadeImageOut(imgWrong);
				   }
			   
				   	//increment question
				   	currentQuestion = (currentQuestion + 1);
					
				   	//timer handle's showNextQuestion
				   	if(quizQuestionDelay > 10) quizQuestionDelay = 2;
			        delayHandler.removeCallbacks(mDelayTask);
			        delayHandler.postDelayed(mDelayTask, (quizQuestionDelay * 1000));
				   	
			   }else{
				   showAlert("Quiz Not Started", "Try using the refresh button on the devices menu?");
			   }
		   }//allowNext	   
		   
	   }else{
		   
		   //////////////////////////////////////////////////////
		   //button clicks are different if quiz is complete...
		   //button_1 becomes "try again"
		   //button_2 becomes "continue" or "show reward"
		   //button_3 becomes "quit"
		   
		   //button_1
		   if(clickedButton.getId() == R.id.btnAnswer_1){
			   startCountdown();
		   }
		   
		   //button_2
		   if(clickedButton.getId() == R.id.btnAnswer_2){
			   
			   	//holds the next screen to show...
	   			BT_item nextScreenObject = null;
			   
	   			//reward screen and user earned enough points...
	   			if(quizRewardScreenItemId.length() > 0 && (totalScore >= quizRewardIfPointsOver)){
					
				   	//instantiate the reward screen..
					BT_debugger.showIt(activityName + ":answerClick creating reward screen with itemId: " + quizRewardScreenItemId);
					nextScreenObject = myPlannerApp_appDelegate.rootApp.getScreenDataByItemId(quizRewardScreenItemId);
					
				}else{
					
					//if we have a quizFinishScreenItemId...
				   	if(quizFinishScreenItemId.length() > 1){
				   		
				   		//instantiate the finish screen..
				   		BT_debugger.showIt(activityName + ":answerClick creating finished screen with itemId: " + quizFinishScreenItemId);
				   		nextScreenObject = myPlannerApp_appDelegate.rootApp.getScreenDataByItemId(quizFinishScreenItemId);
				   		
				   	}
					
					
				}//reward screen and user earned enough points...
	   			
	   			//show next screen if we created on, else quit...
	   			if(nextScreenObject != null){
	   	   			
	   				//load the next screen...
	   				BT_act_controller.loadScreenObject(this, this.screenData, null, nextScreenObject);

	   			}else{
					onBackPressed();
	   			}
	   			
	   			
		   }

		   //button_3
		   if(clickedButton.getId() == R.id.btnAnswer_3){
			   onBackPressed();
		   }
		   
	   }
		  
	}
	
	//start countdown...
	public void startCountdown(){
		BT_debugger.showIt(activityName + ":startCountdown");	

		//kill possible sounds...
		killQuizSounds();					

		//reset possible sounds...
		setupQuizSounds();
		
		//set image to default...
		String tmpImageName = "quiz_bgsmall.png";
		if(myPlannerApp_appDelegate.rootApp.getRootDevice().getIsLargeDevice()){
			tmpImageName = "quiz_bglarge.png";
		}
		Drawable d = BT_fileManager.getDrawableByName(tmpImageName);
		if(quizImageCornerRadius > 0){
			Bitmap b = ((BitmapDrawable)d).getBitmap();
			b = BT_viewUtilities.getRoundedImage(b, quizImageCornerRadius);
			d = new BitmapDrawable(b);
		}
		imgQuestion.setImageDrawable(d);
		imgQuestion.setVisibility(VISIBLE);
		imgQuestion.setAlpha(255);
		
		//hide controls
		txtTimer.setVisibility(INVISIBLE);
		txtScore.setVisibility(VISIBLE);
		txtQuestion.setVisibility(INVISIBLE);		
		btnPanel.setVisibility(INVISIBLE);		
		
		//set score to countdown number...
		txtScore.setText("" + quizStartCountdownSeconds);
		
		//start countdown timer...
	    quizCountdownSeconds = 0;
	    updateCountdownHandler.removeCallbacks(mUpdateCountdownTask);
	    updateCountdownHandler.postDelayed(mUpdateCountdownTask, 100);
		
	}
	
	/////////////////////////////////////////////////////////////////////
	//handles timer updates for quiz "countdown starting"
	Handler updateCountdownHandler = new Handler(){
		@Override public void handleMessage(Message msg){
	        
			//quizCountdownSeconds is counter for quizStartCountdownSeconds
			if(quizCountdownSeconds > quizStartCountdownSeconds){
			
				//reset quizCountdown seconds for next time...
				quizCountdownSeconds = 0;
				
				//start quiz...
				startQuiz();
				
			}else{
				
				//update score text to show countdown...
				if(quizCountdownSeconds >= quizStartCountdownSeconds){
					txtScore.setText("Go!");
				}else{
					txtScore.setText("" + (quizStartCountdownSeconds - quizCountdownSeconds));
				}
				
				//add one to  quizCountdownSeconds...
				quizCountdownSeconds = (quizCountdownSeconds + 1);
				
				//reset timer to trigger this again in 1 second (1000 milliseconds)...
				updateCountdownHandler.removeCallbacks(mUpdateCountdownTask);
				updateCountdownHandler.postDelayed(mUpdateCountdownTask, 1000);	

				
			}
			
		}
	};		
	
	private Runnable mUpdateCountdownTask = new Runnable() {
		public void run() {
			if((quizCountdownSeconds - 1) <= quizStartCountdownSeconds){
				updateCountdownHandler.sendMessage(updateCountdownHandler.obtainMessage());
			}
		}
	};	
	//end timer stuff
	/////////////////////////////////////////////////////////////////////
	
	
	//starts quiz..
	public void startQuiz(){
		BT_debugger.showIt(activityName + ":startQuiz");	

		//stop countdown timer...
	    updateCountdownHandler.removeCallbacks(mUpdateCountdownTask);
	    
		//reset values...
		numberRight = 0;
		numberWrong = 0;
		numberStreak = 0;
		totalSeconds = 0;
		totalScore = 0;
		currentQuestion = 0;
		allowNext = 1;
		quizIsComplete = 0;
		
		//reset text
		txtQuestion.setText("");
		txtScore.setText("");
		txtTimer.setText("");
		txtTimer.setVisibility(VISIBLE);
		txtScore.setVisibility(VISIBLE);
		txtQuestion.setVisibility(VISIBLE);

		btnAnswer_1.setText("");
		btnAnswer_2.setText("");
		btnAnswer_3.setText("");
		btnAnswer_4.setText("");
		
		
		//randomize questions from pool then grab "x" number for quiz
		if(questionPool.size() > 0 && (quizRandomizeQuestions.equals("1") || quizRandomizeQuestions.toUpperCase().equals("YES")  )){
			Collections.shuffle(questionPool);
	  		childItems = new ArrayList<BT_item>(); 
	        for (int i = 0; i < questionPool.size(); i++){
	        	if(i < quizNumberOfQuestions){
	        		BT_item thisQuestion = questionPool.get(i);
	        		childItems.add(thisQuestion);
	        	}else{
	        		break;
	        	}
	        }//end for each
		}else{
			//showAlert("No Questions?", "This quiz does not have any questions associated with it?");
		}
		
		//start timer...
        startTime = System.currentTimeMillis();
        updateTimeHandler.removeCallbacks(mUpdateTimeTask);
        updateTimeHandler.postDelayed(mUpdateTimeTask, 100);
        
        //show next question
        showNextQuestion();
	}
	
		

	//shows next question
	public void showNextQuestion(){
		BT_debugger.showIt(activityName + ":showNextQuestion Question Number: " + (currentQuestion + 1));	
		
		//hide current image...
		String tmpBlankImg = "bt_blank.png";
		Drawable dblank = BT_fileManager.getDrawableByName(tmpBlankImg);
		imgQuestion.setImageDrawable(dblank);
		showQuestionImage();	
		
		//set button backgrounds...
		btnAnswer_1.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorAnswers));
		btnAnswer_2.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorAnswers));
		btnAnswer_3.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorAnswers));
		btnAnswer_4.setBackgroundColor(BT_color.getColorFromHexString(quizButtonColorAnswers));
		
		//show the buttons...
		btnAnswer_1.setVisibility(VISIBLE);
		btnAnswer_2.setVisibility(VISIBLE);
		btnAnswer_3.setVisibility(VISIBLE);
		btnAnswer_4.setVisibility(VISIBLE);

		//quiz may be over
		if(currentQuestion >= childItems.size()){
			
	        imgQuestion.setVisibility(INVISIBLE);
			endQuiz();
		
		}else{
			
			
			//slide in buttons..
			slideButtons();
	        			
			//slide in question
			slideQuestion();
			
			//get next question object (BT_item)...
			BT_item nextQuestion = childItems.get(currentQuestion);
			
			//set question text
			String questionText = BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "questionText", "");
			txtQuestion.setText(questionText);
			BT_debugger.showIt(activityName + ":showNextQuestion. \"" + questionText + "\"");	

			//load image?
			String tmpImageFileName = BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "imageNameSmallDevice", "");
			String tmpImageURL = BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "imageURLSmallDevice", "");
			
			//images are different if this is a large device...
			if(myPlannerApp_appDelegate.rootApp.getRootDevice().getIsLargeDevice()){
				tmpImageFileName = BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "imageNameLargeDevice", "");
				tmpImageURL = BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "imageURLLargeDevice", "");
			}
			
			
			//do we have an image name or URL in the JSON?...
			if(tmpImageFileName.length() > 1 || tmpImageURL.length() > 1){
				
				//image in project...
				if(tmpImageFileName.length() > 1){
					BT_debugger.showIt(activityName + ":showNextQuestion. Setting image from /drawables directory: \"" + tmpImageFileName + "\"");	

					Drawable d = BT_fileManager.getDrawableByName(tmpImageFileName);
					if(quizImageCornerRadius > 0){
						Bitmap b = ((BitmapDrawable)d).getBitmap();
						b = BT_viewUtilities.getRoundedImage(b, quizImageCornerRadius);
						d = new BitmapDrawable(b);
					}
					imgQuestion.setImageDrawable(d);
					imgQuestion.setAlpha(0);
					imgQuestion.setVisibility(INVISIBLE);
					showQuestionImage();
					
				}else{
				
					//image from URL
					if(tmpImageURL.length() > 1){
					
						String useImageName = BT_strings.getSaveAsFileNameFromURL(tmpImageURL);
		        		if(BT_fileManager.doesCachedFileExist(useImageName)){
							BT_debugger.showIt(activityName + ":showNextQuestion. Using cached image previously downloaded from: \"" + tmpImageURL + "\"");	
								
							Drawable d = BT_fileManager.getDrawableFromCache(useImageName);
							if(quizImageCornerRadius > 0){
								Bitmap b = ((BitmapDrawable)d).getBitmap();
								b = BT_viewUtilities.getRoundedImage(b, quizImageCornerRadius);
								d = new BitmapDrawable(b);
							}
							imgQuestion.setImageDrawable(d);
							imgQuestion.setAlpha(0);
							imgQuestion.setVisibility(INVISIBLE);
							showQuestionImage();

		        		}else{
		        			
							BT_debugger.showIt(activityName + ":showNextQuestion. Downloading image from: \"" + tmpImageURL + "\"");	
	        			 	BT_downloader objDownloader = new BT_downloader(tmpImageURL);
	        			 	objDownloader.setSaveAsFileName(useImageName);
	        			 	Drawable d = objDownloader.downloadDrawable();
							if(quizImageCornerRadius > 0){
								Bitmap b = ((BitmapDrawable)d).getBitmap();
								b = BT_viewUtilities.getRoundedImage(b, quizImageCornerRadius);
								d = new BitmapDrawable(b);
							}
							imgQuestion.setImageDrawable(d);
							imgQuestion.setAlpha(0);
							imgQuestion.setVisibility(INVISIBLE);
							showQuestionImage();
							
							
		        			
		        		}
		        		
					
					
					}
				
				
				}
			
			}else{
				BT_debugger.showIt(activityName + ":showNextQuestion. Question number " + (currentQuestion + 1) + " does not use an image");	
			}
			
			//randomize array of answers on buttons
			ArrayList<String> answers = new ArrayList<String>();
			answers.add(BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "incorrectText1", ""));
			answers.add(BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "incorrectText2", ""));
			answers.add(BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "incorrectText3", ""));
			answers.add(BT_strings.getJsonPropertyValue(nextQuestion.getJsonObject(), "correctAnswerText", ""));
			Collections.shuffle(answers);
			
			//show on buttons
			btnAnswer_1.setText(answers.get(0).toString());
			btnAnswer_2.setText(answers.get(1).toString());
			btnAnswer_3.setText(answers.get(2).toString());
			btnAnswer_4.setText(answers.get(3).toString());
			
		}
		
		//flag allow next
		allowNext = 1;
		
		
	}
	
	//end quiz
	public void endQuiz(){
		BT_debugger.showIt(activityName + ":endQuiz");	
		
		//set image to default...
		String tmpImageName = "quiz_bgsmall.png";
		if(myPlannerApp_appDelegate.rootApp.getRootDevice().getIsLargeDevice()){
			tmpImageName = "quiz_bglarge.png";
		}
		Drawable d = BT_fileManager.getDrawableByName(tmpImageName);
		if(quizImageCornerRadius > 0){
			Bitmap b = ((BitmapDrawable)d).getBitmap();
			b = BT_viewUtilities.getRoundedImage(b, quizImageCornerRadius);
			d = new BitmapDrawable(b);
		}
		imgQuestion.setImageDrawable(d);
		imgQuestion.setVisibility(VISIBLE);
		imgQuestion.setAlpha(255);

		//flag
		quizIsComplete = 1;
		
		//clear values
		currentQuestion = 0;
		txtQuestion.setText("");
		txtScore.setText("Quiz Complete");
		
		//turn off timer
		updateTimeHandler.removeCallbacks(mUpdateTimeTask);
	    String tmpTime = "";
		long millis = System.currentTimeMillis() - startTime;
	    int seconds = (int) (millis / 1000);
	    int minutes = seconds / 60;
	    seconds = seconds % 60;
	    if(seconds < 10) {
	    	tmpTime =  minutes + ":0" + seconds;
	    }else{
	    	tmpTime = minutes + ":" + seconds;            
	    }
	    totalSeconds = seconds;
	    
	    //send results to URL if provided...
	    if(sendResultsToURL.length() > 1){
	    	sendQuizResultsToURL();
	    }
	    
	    //show finished options, this method also plays possible sound effects...
	    showFinishedOptions();
	    
	    //show alert
		String tmpResults = "\nAnswers: " +  numberRight + " / " + childItems.size();
		tmpResults += "\nPoints: " + totalScore;
		tmpResults += "\nElapsed Time: " + tmpTime;
		tmpResults += "\n";
		showAlert("Quiz Complete", tmpResults);

		
	}
	
	//shows finished options...
	public void showFinishedOptions(){
		BT_debugger.showIt(activityName + ":showFinishedOptions");	
		
		//NOTE: we may be arriving here on reward screen or finished screen "back" press...
		
		//set image to default...
		String tmpImageName = "quiz_bgsmall.png";
		if(myPlannerApp_appDelegate.rootApp.getRootDevice().getIsLargeDevice()){
			tmpImageName = "quiz_bglarge.png";
		}
		Drawable d = BT_fileManager.getDrawableByName(tmpImageName);
		if(quizImageCornerRadius > 0){
			Bitmap b = ((BitmapDrawable)d).getBitmap();
			b = BT_viewUtilities.getRoundedImage(b, quizImageCornerRadius);
			d = new BitmapDrawable(b);
		}
		imgQuestion.setImageDrawable(d);
		imgQuestion.setVisibility(VISIBLE);
		imgQuestion.setAlpha(255);
	    
		//hide controls
		txtTimer.setVisibility(INVISIBLE);
		txtScore.setVisibility(INVISIBLE);
		txtQuestion.setVisibility(INVISIBLE);		
		btnPanel.setVisibility(INVISIBLE);
		
		//button 1 becomes try again...
		btnAnswer_1.setText("Try Again");
		
		//button 2 becomes show reward or continue...
		//if we have a quizRewardScreenItemId AND a totalPoints > 
		if(quizRewardScreenItemId.length() > 0 && (totalScore >= quizRewardIfPointsOver)){
			btnAnswer_2.setText("Show Reward");
			
			//play possible reward sound...
			if(soundPlayerReward != null){
			   soundPlayerReward.start();
			}			
			
		}else{
			btnAnswer_2.setText("Continue");
			   
			//play possible finished sound...
			if(soundPlayerFinished != null){
				soundPlayerFinished.start();
			}
			
		}
		
		//button 3 becomes quit...
		btnAnswer_3.setText("Quit");
		
		//button 4 is unused...
		btnAnswer_4.setVisibility(INVISIBLE);
		
		//slide in buttons..
		slideButtons();
		
	}
	
	
	/////////////////////////////////////////////////////////////////////
	//handles timer updates for quiz "running time"
	Handler updateTimeHandler = new Handler(){
		@Override public void handleMessage(Message msg){
	        
			//final long start = startTime;
		    long millis = System.currentTimeMillis() - startTime;
		    int seconds = (int) (millis / 1000);
		    int minutes = seconds / 60;
		    seconds = seconds % 60;
		    if(quizShowTimer == "1" || quizShowTimer.toUpperCase() == "YES"){
		    	if(seconds < 10) {
		    		txtTimer.setText("" + minutes + ":0" + seconds);
		    	}else{
		    		txtTimer.setText("" + minutes + ":" + seconds);            
		    	}
		    }else{
		    	txtTimer.setText("");
		    }
		    
		    //re-trigger timer
	        updateTimeHandler.removeCallbacks(mUpdateTimeTask);
	        updateTimeHandler.postDelayed(mUpdateTimeTask, 100);	
		}
	};		
	
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
		    updateTimeHandler.sendMessage(updateTimeHandler.obtainMessage());
		}
	};	
	//end timer stuff
	/////////////////////////////////////////////////////////////////////
	
	
	/////////////////////////////////////////////////////////////////////
	//handles question delay updates after each answer
	Handler delayHandler = new Handler(){
		@Override public void handleMessage(Message msg){
			delayHandler.removeCallbacks(mUpdateTimeTask);
		}
	};		
	
	private Runnable mDelayTask = new Runnable() {
		public void run() {
		    updateTimeHandler.sendMessage(delayHandler.obtainMessage());
		    showNextQuestion();
		}
	};	
	//end timer stuff
	/////////////////////////////////////////////////////////////////////
    
    
	//fade image in
	public void fadeImageIn(ImageView theImageView){
		BT_debugger.showIt(activityName + ":fadeImageIn");	

		try{
			theImageView.setVisibility(INVISIBLE);
			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(1200);
			theImageView.startAnimation(animation); 
			theImageView.setVisibility(VISIBLE);
		}catch(Exception ex){
			
		}
	}	
	
	//fade image out
	public void fadeImageOut(ImageView theImageView){
		BT_debugger.showIt(activityName + ":fadeImageOut");	

		try{
			theImageView.setVisibility(VISIBLE);
			Animation animation = new AlphaAnimation(1.0f, 0.0f);
			animation.setDuration(1200);
			theImageView.startAnimation(animation); 
			theImageView.setVisibility(INVISIBLE);
		}catch(Exception ex){
			
		}
	}	
	
	//transitions question image..
	public void showQuestionImage(){
		BT_debugger.showIt(activityName + ":showQuestionImage");	

		try{
			imgQuestion.setAlpha(255);
			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(1200);
			imgQuestion.startAnimation(animation); 
			imgQuestion.setVisibility(VISIBLE);
		}catch(Exception ex){
			
		}
	}	
	//slide buttons
	public void slideButtons(){
		BT_debugger.showIt(activityName + ":slideButtons");	

		try{
			btnPanel.setVisibility(VISIBLE);
			Animation animation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
			animation.setDuration(500);
			btnPanel.startAnimation(animation); 
		}catch(Exception ex){
			
		}
	}		
	
	//slide question
	public void slideQuestion(){
		BT_debugger.showIt(activityName + ":slideQuestion");	

		try{
			txtQuestion.setVisibility(VISIBLE);
			Animation animation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
					Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
			animation.setDuration(500);
			txtQuestion.startAnimation(animation); 
		}catch(Exception ex){
			
		}
	}		
    
    
    
    ///////////////////////////////////////////////////////////////////
	//DownloadScreenDataThread and Handler
	Handler downloadScreenDataHandler = new Handler(){
		@Override public void handleMessage(Message msg){
			if(JSONData.length() < 1){
				hideProgress();
				showAlert(getString(R.string.errorTitle), getString(R.string.errorDownloadingData));
			}else{
				parseScreenData(JSONData);
			}
		}
	};	   
    
	public class DownloadScreenDataWorker extends Thread{
		 boolean threadRunning = false;
		 String downloadURL = "";
		 String saveAsFileName = "";
		 void setThreadRunning(boolean bolRunning){
			 threadRunning = bolRunning;
		 }	
		 void setDownloadURL(String theURL){
			 downloadURL = theURL;
		 }
		 void setSaveAsFileName(String theFileName){
			 saveAsFileName = theFileName;
		 }
		 @Override 
    	 public void run(){
			
			 //downloader will fetch and save data..Set this screen data as "current" to be sure the screenId
			 //in the URL gets merged properly. Several screens could be loading at the same time...
			 myPlannerApp_appDelegate.rootApp.setCurrentScreenData(screenData);
			 String useURL = BT_strings.mergeBTVariablesInString(dataURL);
			 BT_debugger.showIt(activityName + ": downloading screen data from " + useURL);
			 BT_downloader objDownloader = new BT_downloader(useURL);
			 objDownloader.setSaveAsFileName(saveAsFileName);
			 JSONData = objDownloader.downloadTextData();
			
			 //send message to handler..
			 this.setThreadRunning(false);
			 downloadScreenDataHandler.sendMessage(downloadScreenDataHandler.obtainMessage());
   	 	
		 }
	}	
	//END DownloadScreenDataThread and Handler
	///////////////////////////////////////////////////////////////////
	

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
		
		//refresh screen data...
		if(dataURL.length() > 1){
			final Button btnRefreshScreenData = new Button(this);
			btnRefreshScreenData.setText(getString(R.string.refreshScreenData));
			btnRefreshScreenData.setOnClickListener(new OnClickListener(){
            	public void onClick(View v){
                	dialog.cancel();
            		refreshScreenData();
            	}
			});
			options.add(btnRefreshScreenData);
		}
		
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
		
		//restart quiz...
		final Button btnRestart = new Button(this);
		btnRestart.setText("Restart Quiz");
		btnRestart.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                dialog.cancel();
                startCountdown();
            }
        });
		options.add(btnRestart);
		
		//quit quiz...
		final Button btnQuit = new Button(this);
		btnQuit.setText("Quit");
		btnQuit.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                dialog.cancel();
                onBackPressed();
            }
        });
		options.add(btnQuit);
		
		//add each option to layout, set layoutParams as we go...
		for(int x = 0; x < options.size(); x++){
            Button btn = (Button)options.get(x);
            btn.setTextSize(18);
            btn.setLayoutParams(btnLayoutParams);
            btn.setPadding(5, 5, 5, 5);
			optionsView.addView(btn);
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


 


