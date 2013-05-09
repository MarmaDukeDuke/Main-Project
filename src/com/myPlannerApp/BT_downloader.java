
package com.myPlannerApp;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BT_downloader {
	
	public static String objectName = "BT_downloader";
	private int connectionTimeoutSeconds = 30;
	private boolean downloadInProgress;
	private String downloadURL;
	private String saveAsFileName;
	
	//constructor
	public BT_downloader(String downloadURL){
		this.downloadURL = downloadURL;
		this.saveAsFileName = "";
	}
	
	
	//download image from URL, return as drawable...
    public Drawable downloadDrawable(){  
		BT_debugger.showIt(objectName + ":downloadDrawable from URL: " + this.downloadURL);
    	setDownloadInProgress(true);
        InputStream in = null;  
    	Drawable d = null;
        int response = -1;
        try{
        	
	        URL url = new URL(this.downloadURL); 
	        URLConnection conn = url.openConnection();
	                 
	        if (!(conn instanceof HttpURLConnection))                     
	            throw new IOException("Not an HTTP connection");
	        try{
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            httpConn.setAllowUserInteraction(false);
	            httpConn.setInstanceFollowRedirects(true);
	            httpConn.setConnectTimeout(connectionTimeoutSeconds * 1000);
	            httpConn.setRequestMethod("GET");
	            httpConn.connect(); 
	            response = httpConn.getResponseCode();                 
	            if(response == HttpURLConnection.HTTP_OK) {
	                in = httpConn.getInputStream();  
	            }else{
		    		BT_debugger.showIt(objectName + ":downloadImage HTTP response NOT OK, URL: " + url);
	            }
	            Bitmap b = BitmapFactory.decodeStream(in);
	            d = new BitmapDrawable(b);
	            
	            //save to cache if we have a filename...
	            if(this.saveAsFileName.length() > 1 && b != null){
	            	BT_fileManager.saveImageToCache(b, this.saveAsFileName);
	            }
	            
	            
	        }catch (Exception ex){
	    		BT_debugger.showIt(objectName + ":downloadDrawable Exception in httpConnection: " + ex.toString() + " URL: " + url);
	            throw new IOException("Error connecting");            
	        }
	        
       
        }catch (Exception e) {
    		d = null;
        	BT_debugger.showIt(objectName + ":downloadImage from URL error: " + this.downloadURL);
    		BT_debugger.showIt(objectName + ":downloadImage Exception: " + e.toString());
        }
        setDownloadInProgress(false);
        return d;                
    } 
    
	//download image from URL, return as bitmap...
    public Bitmap downloadBitmap(){  
		BT_debugger.showIt(objectName + ":downloadBitmap from URL: " + this.downloadURL);
    	setDownloadInProgress(true);
        InputStream in = null;  
    	Bitmap b = null;
        int response = -1;
        try{
        	
	        URL url = new URL(this.downloadURL); 
	        URLConnection conn = url.openConnection();
	                 
	        if (!(conn instanceof HttpURLConnection))                     
	            throw new IOException("Not an HTTP connection");
	        try{
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            httpConn.setAllowUserInteraction(false);
	            httpConn.setInstanceFollowRedirects(true);
	            httpConn.setConnectTimeout(connectionTimeoutSeconds * 1000);
	            httpConn.setRequestMethod("GET");
	            httpConn.connect(); 
	            response = httpConn.getResponseCode();                 
	            if(response == HttpURLConnection.HTTP_OK) {
	                in = httpConn.getInputStream();  
	            }else{
		    		BT_debugger.showIt(objectName + ":downloadImage HTTP response NOT OK, URL: " + url);
	            }
	            b = BitmapFactory.decodeStream(in);
	            
	            //save to cache if we have a filename...
	            if(this.saveAsFileName.length() > 1 && b != null){
	            	BT_fileManager.saveImageToCache(b, this.saveAsFileName);
	            }
	            
	            
	        }catch (Exception ex){
	    		BT_debugger.showIt(objectName + ":downloadBitmap Exception in httpConnection: " + ex.toString());
	            throw new IOException("Error connecting");            
	        }
	        
       
        }catch (Exception e) {
    		b = null;
        	BT_debugger.showIt(objectName + ":downloadBitmap from URL error: " + this.downloadURL);
    		BT_debugger.showIt(objectName + ":downloadBitmap Exception: " + e.toString());
        }
        setDownloadInProgress(false);
        return b;                
    } 
    	
    
    //download text data from URL...
    public String downloadTextData(){  
		BT_debugger.showIt(objectName + ":downloadTextData from URL: " + this.downloadURL);
		setDownloadInProgress(true);
		String ret = "";
        
		try{
        	
			//create a URL for the desired data
            URL url = new URL(this.downloadURL);

            //read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()), 8192);
            
            String str;
            while ((str = in.readLine()) != null) {
                ret += str;
            }
            in.close(); 
            
            //save to cache if we have a filename...
            if(this.saveAsFileName.length() > 1 && ret.length() > 1){
            	BT_fileManager.saveTextFileToCache(ret, this.saveAsFileName);
            } 
            
        }catch (Exception e) {
    		ret = "";
        	BT_debugger.showIt(objectName + ":downloadTextData from URL EXCEPTION: " + e.toString() + " URL: " + this.downloadURL);
        }
          
        //flag, return...
        setDownloadInProgress(false);
        return ret;                
    }    
    
    //download binary data from URL...
    public String downloadAndSaveBinaryData(){  
		BT_debugger.showIt(objectName + ":downloadBinaryData from URL: " + this.downloadURL);
		BT_debugger.showIt(objectName + ":downloadBinaryData Save As File Name:: " + this.saveAsFileName);

		setDownloadInProgress(true);
		String ret = "";
        try{
        	
        	//save file location after it's done donwloading...
 			FileOutputStream f = myPlannerApp_appDelegate.getApplication().openFileOutput(saveAsFileName, Context.MODE_WORLD_READABLE);
            
 			//use Android's built in HTTP Client to download the file...
 			DefaultHttpClient httpClient = new DefaultHttpClient();

 			//setup HttpGet object (request object) using the URL)...
 			HttpGet request = new HttpGet(this.downloadURL);

 			//execute the request...
            HttpResponse response = httpClient.execute(request);
            
            //setup InputStream to read the bytes of data being downloaded, write bytes to the file...
            InputStream is = response.getEntity().getContent();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ( (len1 = is.read(buffer)) > 0 ) {
              f.write(buffer,0, len1);
            }
            f.flush();
            f.close();            
            
        }catch (Exception e) {
    		ret = "";
        	BT_debugger.showIt(objectName + ":downloadAndSaveBinaryData from URL EXCEPTION: " + e.toString() + " URL: " + this.downloadURL);
        }
        setDownloadInProgress(false);
        return ret;                
    }       
    
    
    
    //getters / setters
    public void setDownloadInProgress(boolean downloadInProgress){
    	this.downloadInProgress = downloadInProgress;
    }
    public boolean getDownloadInProgress(){
    	return downloadInProgress;
    }
    public void setDownloadURL(String downloadURL){
    	this.downloadURL = downloadURL;
    }
    public String getDownloadURL(){
    	return downloadURL;
    }
    public void setSaveAsFileName(String saveAsFileName){
    	this.saveAsFileName = saveAsFileName;
    }
    public String getSaveAsFileName(){
    	return saveAsFileName;
    } 
    
}





