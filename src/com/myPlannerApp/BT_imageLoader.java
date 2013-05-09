
package com.myPlannerApp;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class BT_imageLoader {
	private final static String objectName = "BT_imageLoader";
	private static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();
	
	//loadImage called from activity context...
	public static void loadImage(final Activity activity, final BT_item listItem, final ImageView imageView, final String url) {
		
		synchronized (cache) {
			if (cache.containsKey(url)) {
				imageView.setVisibility(View.VISIBLE);
				
				Drawable d = new BitmapDrawable(cache.get(url));
				imageView.setImageDrawable(d);
				
				
				//imageView.setImageBitmap(cache.get(url));
				//BT_debugger.showIt(objectName + " loading image from the cache...");
				return;
			}
		}
		
		(new Thread() {
			public void run() {
				final Bitmap bitmap;
				
				try {
					bitmap = loadImage(url, listItem);
				} catch (ClientProtocolException e) {
					return;
				} catch (IOException e) {
					return;
				}
				
				activity.runOnUiThread(new Runnable() {
					public void run() {
						//BT_debugger.showIt(objectName + " setting image on UI thread...");
						imageView.setVisibility(View.VISIBLE);
						imageView.setImageBitmap(bitmap);
					}
				});
				
				synchronized (cache) {
					cache.put(url, bitmap);
				}
			}
		}).start();
	}
	
	private static Bitmap loadImage(String url, BT_item listItem) throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(new HttpGet(url));
		Bitmap image = BitmapFactory.decodeStream(response.getEntity().getContent());
		//BT_debugger.showIt(objectName + " loading image from " + url + "...");
		if (image == null) {
			BT_debugger.showIt(objectName + " downloaded image is null?");
			throw new IOException("Empty image.");
		}else{
			int listIconCornerRadius = Integer.parseInt(BT_strings.getStyleValueForScreen(listItem, "listIconCornerRadius", "0"));
			if(listIconCornerRadius < 1){
				return image;
			}else{
				return BT_viewUtilities.getRoundedImage(image, listIconCornerRadius);
			}
			
		}
		
	}
}







