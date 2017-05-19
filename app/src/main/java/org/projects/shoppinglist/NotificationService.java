package org.projects.shoppinglist;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Emil Rotzler on 04-05-2017.
 */

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    String message;

    Handler mHandler = new Handler(Looper.getMainLooper());
    Runnable runner = new Runnable() {
        @Override
        public void run() {
            Toast toastelse3 = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toastelse3.setGravity(Gravity.CENTER| Gravity.CENTER, 0, 0);
            toastelse3.show();
        }
    };
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        message = remoteMessage.getNotification().getBody();
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        mHandler.post(runner);
    }
}