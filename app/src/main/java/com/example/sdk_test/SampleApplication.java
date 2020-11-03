package com.example.sdk_test;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.yuktamedia.analytics.Analytics;
import com.yuktamedia.analytics.integrations.TrackPayload;

public class SampleApplication extends Application {
    private static final String ANALYTICS_WRITE_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTg5OTgwMjg3LCJpc3MiOiJZdWt0YU1lZGlhIn0.NCMx4SH99b-mfaSfg7Rw4uPrkMYZm0ww6i7dS8VvvfE";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        Analytics.Builder builder =
                new Analytics.Builder(this, ANALYTICS_WRITE_KEY)
                        .trackApplicationLifecycleEvents()
                       .trackAttributionInformation()
                        .logLevel(Analytics.LogLevel.VERBOSE)
                        .takeData()
                        .recordScreenViews();
        // Set the initialized instance as a globally accessible instance.
        Analytics.setSingletonInstance(builder.build());

        // Now anytime you call Analytics.with, the custom instance will be returned.
        Analytics analytics = Analytics.with(this);

        MobileAds.initialize(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("Admob Intialised","Intialised AdsMob" );
            }
        });

    }
}
