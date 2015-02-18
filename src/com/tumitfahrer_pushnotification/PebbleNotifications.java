package com.tumitfahrer_pushnotification;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PebbleNotifications {
	public static void sendNotificationToPebble(String title, String body, Context context){
    	final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map<String, String> data = new HashMap<String, String>();
        data.put("title", title);
        data.put("body", body);

        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();
        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "TUMitfahrerBackend");
        i.putExtra("notificationData", notificationData);

        Log.d("Test", "Sending to Pebble: " + notificationData);
        context.sendBroadcast(i);
    }

}
