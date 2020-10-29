package com.yuktamedia.analytics;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.yuktamedia.analytics.internal.Utils.toISO8601String;

public class AnalyticsActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private Analytics analytics;
    private ExecutorService analyticsExecutor;
    private Boolean shouldTrackApplicationLifecycleEvents;
    private Boolean trackAttributionInformation;
    private Boolean trackDeepLinks;
    private Boolean shouldRecordScreenViews;
    private PackageInfo packageInfo;

    private AtomicBoolean trackedApplicationLifecycleEvents;
    private AtomicInteger numberOfActivities;
    private AtomicBoolean isChangingActivityConfigurations;
    private AtomicBoolean firstLaunch;
    private Date activityLoadTime;
    private Date activityUnloadTime;

    private AnalyticsActivityLifecycleCallbacks(
            Analytics analytics,
            ExecutorService analyticsExecutor,
            Boolean shouldTrackApplicationLifecycleEvents,
            Boolean trackAttributionInformation,
            Boolean trackDeepLinks,
            Boolean shouldRecordScreenViews,
            PackageInfo packageInfo) {
        this.trackedApplicationLifecycleEvents = new AtomicBoolean(false);
        this.numberOfActivities = new AtomicInteger(1);
        this.isChangingActivityConfigurations = new AtomicBoolean(false);
        this.firstLaunch = new AtomicBoolean(false);
        this.analytics = analytics;
        this.analyticsExecutor = analyticsExecutor;
        this.shouldTrackApplicationLifecycleEvents = shouldTrackApplicationLifecycleEvents;
        this.trackAttributionInformation = trackAttributionInformation;
        this.trackDeepLinks = trackDeepLinks;
        this.shouldRecordScreenViews = shouldRecordScreenViews;
        this.packageInfo = packageInfo;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        analytics.runOnMainThread(IntegrationOperation.onActivityCreated(activity, bundle));

        if (!trackedApplicationLifecycleEvents.getAndSet(true)
                && shouldTrackApplicationLifecycleEvents) {
            numberOfActivities.set(0);
            firstLaunch.set(true);
            analytics.trackApplicationLifecycleEvents();

            if (trackAttributionInformation) {
                analyticsExecutor.submit(
                        new Runnable() {
                            @Override
                            public void run() {
                                analytics.trackAttributionInformation();

                            }
                        });
            }

            if (!trackDeepLinks) {
                return;
            }

            Intent intent = activity.getIntent();
            if (activity.getIntent() == null) {
                return;
            }

            Properties properties = new Properties();
            Uri uri = intent.getData();
            for (String parameter : uri.getQueryParameterNames()) {
                String value = uri.getQueryParameter(parameter);
                if (value != null && !value.trim().isEmpty()) {
                    properties.put(parameter, value);
                }
            }

            properties.put("url", uri.toString());
            analytics.track("Deep Link Opened", properties);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        PackageManager packageManager = activity.getPackageManager();
        ActivityInfo info =
                null;
        try {
            info = packageManager.getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("ACTIVITY NAME CHECK","NAME"+info.loadLabel(packageManager).toString());
        Log.e("ACTIVITY NAME CHECK",info.loadLabel(packageManager).toString());
        if (shouldRecordScreenViews) {
            analytics.recordScreenViews(activity);
        }
        analytics.runOnMainThread(IntegrationOperation.onActivityStarted(activity));
    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {
        this.activityLoadTime = new Date();
        PackageManager packageManager = activity.getPackageManager();
        try {
            ActivityInfo info =
                    packageManager.getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            CharSequence activityLabel = info.loadLabel(packageManager);
            Properties properties = new Properties();
            properties.putValue("loadTime", toISO8601String(this.activityLoadTime));
            properties.putValue("activityName", activityLabel.toString());
            analytics.track("Activity Started", properties);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        analytics.runOnMainThread(IntegrationOperation.onActivityResumed(activity));

        if (shouldTrackApplicationLifecycleEvents
                && numberOfActivities.incrementAndGet() == 1
                && !isChangingActivityConfigurations.get()) {

            Properties properties = new Properties();
            if (firstLaunch.get()) {
                properties
                        .putValue("version", packageInfo.versionName)
                        .putValue("build", String.valueOf(packageInfo.versionCode));
            }
            properties.putValue("from_background", !firstLaunch.getAndSet(false));
            analytics.track("Application Opened", properties);
        }
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {
        this.activityLoadTime = new Date();
        PackageManager packageManager = activity.getPackageManager();
        try {
            ActivityInfo info =
                    packageManager.getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            CharSequence activityLabel = info.loadLabel(packageManager);
            Properties properties = new Properties();
            properties.putValue("loadTime", toISO8601String(this.activityLoadTime));
            properties.putValue("activityName", activityLabel.toString());
            analytics.track("Activity Resumed", properties);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        analytics.runOnMainThread(IntegrationOperation.onActivityPaused(activity));
    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {
        this.activityUnloadTime = new Date();
        PackageManager packageManager = activity.getPackageManager();
        try {
            ActivityInfo info =
                    packageManager.getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            Log.e("ACTIVITY NAME CHECK",info.loadLabel(packageManager).toString());
            CharSequence activityLabel = info.loadLabel(packageManager);
            Properties properties = new Properties();
            properties.putValue("loadTime", toISO8601String(this.activityLoadTime));
            properties.putValue("unLoadTime", toISO8601String(this.activityUnloadTime));
            properties.putValue("activityName", activityLabel.toString());
            analytics.track("Activity Paused", properties);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        analytics.runOnMainThread(IntegrationOperation.onActivityStopped(activity));

        isChangingActivityConfigurations.set(activity.isChangingConfigurations());
        if (shouldTrackApplicationLifecycleEvents
                && numberOfActivities.decrementAndGet() == 0
                && !isChangingActivityConfigurations.get()) {
            analytics.track("Application Backgrounded");
        }
    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {
        this.activityUnloadTime = new Date();
        PackageManager packageManager = activity.getPackageManager();
        try {
            ActivityInfo info =
                    packageManager.getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            CharSequence activityLabel = info.loadLabel(packageManager);
            Properties properties = new Properties();
            properties.putValue("loadTime", toISO8601String(this.activityLoadTime));
            properties.putValue("unLoadTime", toISO8601String(this.activityUnloadTime));
            properties.putValue("activityName", activityLabel.toString());
            analytics.track("Activity Stopped", properties);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        analytics.runOnMainThread(IntegrationOperation.onActivitySaveInstanceState(activity, bundle));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        analytics.runOnMainThread(IntegrationOperation.onActivityDestroyed(activity));
    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {
        this.activityUnloadTime = new Date();
        PackageManager packageManager = activity.getPackageManager();
        try {
            ActivityInfo info =
                    packageManager.getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            CharSequence activityLabel = info.loadLabel(packageManager);
            Properties properties = new Properties();
            properties.putValue("loadTime", toISO8601String(this.activityLoadTime));
            properties.putValue("unLoadTime", toISO8601String(this.activityUnloadTime));
            properties.putValue("activityName", activityLabel.toString());
            analytics.track("Activity Destroyed", properties);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {
        private Analytics analytics;
        private ExecutorService analyticsExecutor;
        private Boolean shouldTrackApplicationLifecycleEvents;
        private Boolean trackAttributionInformation;
        private Boolean takedata;
        private Boolean trackDeepLinks;
        private Boolean shouldRecordScreenViews;
        private PackageInfo packageInfo;

        public Builder() {}

        public Builder analytics(Analytics analytics) {
            this.analytics = analytics;
            return this;
        }

        Builder analyticsExecutor(ExecutorService analyticsExecutor) {
            this.analyticsExecutor = analyticsExecutor;
            return this;
        }

        Builder shouldTrackApplicationLifecycleEvents(Boolean shouldTrackApplicationLifecycleEvents) {
            this.shouldTrackApplicationLifecycleEvents = shouldTrackApplicationLifecycleEvents;
            return this;
        }

        Builder trackAttributionInformation(Boolean trackAttributionInformation) {
            this.trackAttributionInformation = trackAttributionInformation;
            return this;
        }
        Builder takeData(boolean takedata){
            this.takedata = takedata;
            return  this;
        }

        Builder trackDeepLinks(Boolean trackDeepLinks) {
            this.trackDeepLinks = trackDeepLinks;
            return this;
        }

        Builder shouldRecordScreenViews(Boolean shouldRecordScreenViews) {
            this.shouldRecordScreenViews = shouldRecordScreenViews;
            return this;
        }

        Builder packageInfo(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
            return this;
        }

        public AnalyticsActivityLifecycleCallbacks build() {
            return new AnalyticsActivityLifecycleCallbacks(
                    analytics,
                    analyticsExecutor,
                    shouldTrackApplicationLifecycleEvents,
                    trackAttributionInformation,
                    trackDeepLinks,
                    shouldRecordScreenViews,
                    packageInfo);
        }
    }
}
