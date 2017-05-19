package org.projects.shoppinglist;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Emil Rotzler on 27-04-2017.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Log.d("firebase", "persistance enabled");
        } else {
            Log.d("firebase", "persistance not enabled");
        }
    }
}
