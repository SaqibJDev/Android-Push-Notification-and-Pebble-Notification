package com.tumitfahrer_pushnotification;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class PushNotificationHelper {
	// Tag for logging purposes
	private static final String TAG = "PushNotificationHelper";
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	// Android Project Id
	// TODO change this ID to production one
	private final static String SENDER_ID = "936967013602";
	
	// Google Play services handler local variable
	private static GoogleCloudMessaging gcm = null;

	/*
	 * Checks if Google Play services available on this device. 
	 * If yes then it will register this device in google cloud in background process and 
	 * 		provide ID in onPlayServiceRegistrationComplete() callback function
	 * Else it will toast the unavailability of Google services and will send null in onPlayServiceRegistrationComplete()
	 * 
	 * @param Context which is Activity implementing PushNotificationInterface
	 */
	public void getRegistrationID(Context context) throws IOException{
		// Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
		
        if (checkPlayServices(context)) {
        	// Get Cloud Messaging instance
            gcm = GoogleCloudMessaging.getInstance(context);
            
            // Call cloud registration background process
            RegisterToPlayService registerToPlayService = new RegisterToPlayService(context);
            registerToPlayService.execute();
            
        } else {
            Log.i(TAG, context.getResources().getString(R.string.googlePlayServiceNotFound));
            Toast.makeText(context, context.getResources().getString(R.string.googlePlayServiceNotFound), Toast.LENGTH_LONG).show();
            ((PushNotificationInterface)context).onPlayServiceRegistrationComplete(null);
            
        }
	}
	
	private boolean checkPlayServices(Context context) {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)context,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, context.getResources().getString(R.string.deviceNotSupported));
	            Toast.makeText(context, context.getResources().getString(R.string.deviceNotSupported), Toast.LENGTH_LONG).show();
	        }
	        return false;
	    }
	    return true;
	}
	
	

	    private class RegisterToPlayService extends AsyncTask<Void, String, String> {

	    	private Context context;
	    	
	    	public RegisterToPlayService(Context context){
	    		this.context = context;
	    	}
	    	
	    	@Override
	        protected String doInBackground(Void... params) {
	            String msg = null;
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }

	                // Check if id already exists in Shared Preferences
	                String regid = getRegistrationIdFromSharedPreferences(context);
	                
	                // Register device and store id if not already exists
	                if(regid == null){
	                	regid = gcm.register(SENDER_ID);
		                // Persist the regID - no need to register again.
	                	storeRegistrationId(context, regid);
	                }
	                		
	                msg = regid;
	                Log.i("Main Activity", "RegistrationId"+regid);

	            } catch (IOException ex) {
	            	Log.i("Main Activity", "Error RegistrationId "+ex.getMessage());
	                // If there is an error, return null to tell something went wrong.
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	        	Log.i(TAG, msg);
//	            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	            ((PushNotificationInterface)context).onPlayServiceRegistrationComplete(msg);
	            
	        }
	        
	    }
	
	    /**
		 * Stores the registration ID in the application's SharedPreferences.
		 * @param context application's context.
		 * @param regId registration ID
		 */
		private void storeRegistrationId(Context context, String regId) {
			SharedPreferences prefs = 
				    PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = prefs.edit();
			editor.putString("googlePlayRegisterId", regId);
			editor.commit();
			
		}
		
		/**
		 * Retrieve the registration ID from application's SharedPreferences.
		 * @param context application's context.
		 */
		private String getRegistrationIdFromSharedPreferences(Context context) {
			SharedPreferences prefs = 
				    PreferenceManager.getDefaultSharedPreferences(context);
			String regId = prefs.getString("googlePlayRegisterId", null);
			return regId;
		}
		
		/**
		 * Removes the registration ID in the application's SharedPreferences.
		 * @param context application's context.
		 */
		public void clearRegistrationId(Context context) {
			SharedPreferences prefs = 
				    PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = prefs.edit();
			editor.remove("googlePlayRegisterId");
			editor.commit();
			
		}

}
