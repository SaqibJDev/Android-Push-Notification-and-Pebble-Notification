package com.tumitfahrer_pushnotification;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static final String TAG = "GCMTumitfahrer Notification Service";

    public GcmIntentService() {
        super("GcmIntentService");
        Log.i(TAG, "Constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            	sendNotificationToNotificationDrawer("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
            	sendNotificationToNotificationDrawer("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                if(extras.getString("message")!=null)
                {
                	sendNotificationToNotificationDrawer(extras.getString("message"));
                    Log.i(TAG, "Received: " + extras.toString());
                    Log.i(TAG, "Received Bundle: " + extras.getString("message"));
                }
                
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it to notification drawer.
    private void sendNotificationToNotificationDrawer(String msg) {
    	Log.i(TAG, msg);
        mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO Change this MainActivity.class to the activity you want to start on notification click
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        // TODO Change Drawable to required logo
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(this.getResources().getString(R.string.messageTitle))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        
        // Send Notification to Extra connected devices
        sendExtraNotifications(this.getResources().getString(R.string.messageTitle), msg, (Context)this);
    }
    
    /*
     * Method to handle any extra notification needs to be send on other devices like Pebble
     * Any business logic about turning notification on/off to specific device should be applied in this method
     */
    public void sendExtraNotifications(String title, String body, Context context){
    	PebbleNotifications.sendNotificationToPebble(title, body, context);
    }
    
}