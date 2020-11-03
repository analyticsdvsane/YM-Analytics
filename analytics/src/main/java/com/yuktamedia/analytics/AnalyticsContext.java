package com.yuktamedia.analytics;

import android.annotation.SuppressLint;
import android.app.usage.EventStats;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.internal.ads;
import com.yuktamedia.analytics.integrations.Logger;
import com.yuktamedia.analytics.internal.Private;
import com.yuktamedia.analytics.internal.Utils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
import static android.net.ConnectivityManager.TYPE_BLUETOOTH;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static com.yuktamedia.analytics.internal.Utils.createMap;
import static com.yuktamedia.analytics.internal.Utils.getDeviceId;
import static com.yuktamedia.analytics.internal.Utils.getSystemService;
import static com.yuktamedia.analytics.internal.Utils.hasPermission;
import static com.yuktamedia.analytics.internal.Utils.isNullOrEmpty;
import static com.yuktamedia.analytics.internal.Utils.isOnClassPath;
import static java.util.Collections.unmodifiableMap;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AnalyticsContext extends ValueMap  {

    public Context context;
    private static final String LOCALE_KEY = "locale";
    private static final String TRAITS_KEY = "traits";
    private static final String USER_AGENT_KEY = "userAgent";
    private static final String TIMEZONE_KEY = "timezone";
    // App
    private static final String APP_KEY = "app";
    private static final String APP_NAME_KEY = "name";
    private static final String APP_VERSION_KEY = "version";
    private static final String APP_NAMESPACE_KEY = "namespace";
    private static final String APP_BUILD_KEY = "build";
    private static final String APP_TOTAL_TIME_USED = "time_used";
    // Campaign
    private static final String CAMPAIGN_KEY = "campaign";
    // Device
    private static final String DEVICE_KEY = "device";
    // Library
    private static final String LIBRARY_KEY = "Library";
    private static final String LIBRARY_NAME_KEY = "name";
    private static final String LIBRARY_VERSION_KEY = "version";
    // Location
    private static final String LOCATION_KEY = "location";
    // Network
    private static final String NETWORK_KEY = "network";
    private static final String NETWORK_BLUETOOTH_KEY = "bluetooth";
    private static final String NETWORK_CARRIER_KEY = "carrier";
    private static final String NETWORK_CELLULAR_KEY = "cellular";
    private static final String NETWORK_WIFI_KEY = "wifi";
    // OS
    private static final String OS_KEY = "os";
    private static final String OS_NAME_KEY = "name";
    private static final String OS_VERSION_KEY = "version";
    // Referrer
    private static final String REFERRER_KEY = "referrer";
    // Screen
    private static final String SCREEN_KEY = "screen";
    private static final String SCREEN_DENSITY_KEY = "density";
    private static final String SCREEN_HEIGHT_KEY = "height";
    private static final String SCREEN_WIDTH_KEY = "width";
    //Adevent
    private static final String ADS_EVENT = "adevent";
    UsageStats usage = null;
//    UsageStatsManager usageStats ;
//     usageStats = new UsageStats();
static LocationManager locationManager;

    AnalyticsContext(){

    }
    /**
     * Create a new {@link AnalyticsContext} instance filled in with information from the given {@link
     * Context}. The {@link Analytics} client can be called from anywhere, so the returned instances
     * is thread safe.
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static synchronized AnalyticsContext create(Context context, Traits traits, boolean collectDeviceId) {
        AnalyticsContext analyticsContext =
                new AnalyticsContext(new Utils.NullableConcurrentHashMap<String, Object>());
        analyticsContext.setTraits(traits);
        analyticsContext.putDevice(context, collectDeviceId);
        analyticsContext.putLibrary();
        analyticsContext.put(LOCALE_KEY, Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry());
        analyticsContext.putNetwork(context);
        analyticsContext.putOs();


        analyticsContext.putScreen(context);
//        analyticsContext.getAdsData();
        putUndefinedIfNull(analyticsContext, USER_AGENT_KEY, System.getProperty("http.agent"));
        putUndefinedIfNull(analyticsContext, TIMEZONE_KEY, TimeZone.getDefault().getID());


        return analyticsContext;
    }

//    private void getAdsData() {
//        putAdsData();
//    }

    private void putData() {
        Map<String, Object>  AdsData = createMap();
        AdsData.put(LIBRARY_NAME_KEY, "SAMPLE_DATA");
        AdsData.put(LIBRARY_VERSION_KEY, "SAMPLE_DATE");
        put(LIBRARY_KEY, AdsData    );
    }

    static void putUndefinedIfNull(Map<String, Object> target, String key, CharSequence value) {
        if (isNullOrEmpty(value)) {
            target.put(key, "undefined");
        } else {
            target.put(key, value);
        }
    }

    // For deserialization and wrapping
    AnalyticsContext(Map<String, Object> delegate) {
        super(delegate);
    }

    void attachAdvertisingId(Context context, CountDownLatch latch, Logger logger) {
        // This is done as an extra step so we don't run into errors like this for testing
        // http://pastebin.com/gyWJKWiu.
        if (isOnClassPath("com.google.android.gms.ads.identifier.AdvertisingIdClient")) {
            // This needs to be done each time since the settings may have been updated.
            new GetAdvertisingIdTask(this, latch, logger).execute(context);
        } else {
            logger.debug(
                    "Not collecting advertising ID because "
                            + "com.google.android.gms.ads.identifier.AdvertisingIdClient "
                            + "was not found on the classpath.");
            latch.countDown();
        }
    }

    @Override
    public AnalyticsContext putValue(String key, Object value) {
        super.putValue(key, value);
        return this;
    }

    /** Returns an unmodifiable shallow copy of the values in this map. */
    public AnalyticsContext unmodifiableCopy() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(this);
        return new AnalyticsContext(unmodifiableMap(map));
    }

    /**
     * Attach a copy of the given {@link Traits} to this instance. This creates a copy of the given
     * {@code traits}, so exposing {@link #traits()} to the public API is acceptable.
     */
    void setTraits(Traits traits) {
        put(TRAITS_KEY, traits.unmodifiableCopy());
    }

    /**
     * Note: Not for public use. Clients should modify the user's traits with {@link
     * Analytics#identify(String, Traits, Options)}. Modifying this instance will not reflect changes
     * to the user's information that is passed onto bundled integrations.
     *
     * <p>Return the {@link Traits} attached to this instance.
     */
    public Traits traits() {
        return getValueMap(TRAITS_KEY, Traits.class);
    }

    /**
     * Fill this instance with application info from the provided {@link Context}. No need to expose a
     * getter for this for bundled integrations (they'll automatically fill what they need
     * themselves).
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void putApp(Context context) {

//       UsageStats usageStats = (UsageStats) context.getSystemService(Context.USAGE_STATS_SERVICE);
//        UsageStats usageStats = new UsageStats(this.usage);
//        EventStats eventStats = new EventStats();



        try {
            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            Map<String, Object> app = createMap();
            putUndefinedIfNull(app, APP_NAME_KEY, packageInfo.applicationInfo.loadLabel(packageManager));
            putUndefinedIfNull(app, APP_VERSION_KEY, packageInfo.versionName);
            putUndefinedIfNull(app, APP_NAMESPACE_KEY, packageInfo.packageName);
            putUndefinedIfNull(app, APP_TOTAL_TIME_USED, "GETTING_DATA");
            app.put(APP_BUILD_KEY, String.valueOf(packageInfo.versionCode));
            put(APP_KEY, app);
        } catch (PackageManager.NameNotFoundException e) {
            // ignore
        }
    }

    /** Set information about the campaign that resulted in the API call. */
    public AnalyticsContext putCampaign(Campaign campaign) {
        return putValue(CAMPAIGN_KEY, campaign);
    }

    public Campaign campaign() {
        return getValueMap(CAMPAIGN_KEY, Campaign.class);
    }

    /** Fill this instance with device info from the provided {@link Context}. */
    void putDevice(Context context, boolean collectDeviceID) {
        Device device = new Device();
        String identifier = collectDeviceID ? getDeviceId(context) : traits().anonymousId();
        device.put(Device.DEVICE_ID_KEY, identifier);
        device.put(Device.DEVICE_MANUFACTURER_KEY, Build.MANUFACTURER);
        device.put(Device.DEVICE_MODEL_KEY, Build.MODEL);
        device.put(Device.DEVICE_NAME_KEY, Build.DEVICE);
        put(DEVICE_KEY, device);
    }


    void putAdsData( Map<String, Object> adsData){
//         adsData = createMap();
         if (adsData.isEmpty()){
             adsData.put("ADS_DATA" ,"No Data Found");
         }else {
             adsData.put("ADS_DATA" ,adsData);
         }
    }

    public Device device() {
        return getValueMap(DEVICE_KEY, Device.class);
    }

    /** Set a device token. Convenience method for {@link Device#putDeviceToken(String)} */
    public AnalyticsContext putDeviceToken(String token) {
        device().putDeviceToken(token);
        return this;
    }

    /** Fill this instance with library information. */
    void putLibrary() {
        Map<String, Object> library = createMap();
        library.put(LIBRARY_NAME_KEY, BuildConfig.LIBRARY_PACKAGE_NAME);
        library.put(LIBRARY_VERSION_KEY, BuildConfig.VERSION_NAME);
        put(LIBRARY_KEY, library);
    }

    /** Set location information about the device. */
    public AnalyticsContext  putLocation(Location location) {
        return putValue(LOCATION_KEY, location);
    }

    public Location location() {
        return getValueMap(LOCATION_KEY, Location.class);
    }

    /**
     * Fill this instance with network information. No need to expose a getter for this for bundled
     * integrations (they'll automatically fill what they need themselves).
     */
    @SuppressLint("MissingPermission")
    void putNetwork(Context context) {
        Map<String, Object> network = createMap();
        if (hasPermission(context, ACCESS_NETWORK_STATE)) {
            ConnectivityManager connectivityManager = getSystemService(context, CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(TYPE_WIFI);
                network.put(NETWORK_WIFI_KEY, wifiInfo != null && wifiInfo.isConnected());
                NetworkInfo bluetoothInfo = connectivityManager.getNetworkInfo(TYPE_BLUETOOTH);
                network.put(NETWORK_BLUETOOTH_KEY, bluetoothInfo != null && bluetoothInfo.isConnected());
                NetworkInfo cellularInfo = connectivityManager.getNetworkInfo(TYPE_MOBILE);
                network.put(NETWORK_CELLULAR_KEY, cellularInfo != null && cellularInfo.isConnected());
            }
        }

        TelephonyManager telephonyManager = getSystemService(context, TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            network.put(NETWORK_CARRIER_KEY, telephonyManager.getNetworkOperatorName());
        } else {
            network.put(NETWORK_CARRIER_KEY, "unknown");
        }

        put(NETWORK_KEY, network);
    }

    /** Fill this instance with operating system information. */
    void putOs() {
        Map<String, Object> os = createMap();
        os.put(OS_NAME_KEY, "Android");
        os.put(OS_VERSION_KEY, Build.VERSION.RELEASE);
        put(OS_KEY, os);
    }

    /** Set the referrer for this session. */
    public AnalyticsContext putReferrer(Referrer referrer) {
        return putValue(REFERRER_KEY, referrer);
    }

    /**
     * Fill this instance with application info from the provided {@link Context}. No need to expose a
     * getter for this for bundled integrations (they'll automatically fill what they need
     * themselves).
     */
    void putScreen(Context context) {
        Map<String, Object> screen = createMap();
        WindowManager manager = getSystemService(context, Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        screen.put(SCREEN_DENSITY_KEY, displayMetrics.density);
        screen.put(SCREEN_HEIGHT_KEY, displayMetrics.heightPixels);
        screen.put(SCREEN_WIDTH_KEY, displayMetrics.widthPixels);
        put(SCREEN_KEY, screen);
    }




//    void putAdsData(AdsDataCollector adsDataCollector){
//
////        adsDataCollector.onAdEvent();
////        Map<String, Object> adsData = createMap();
////        adsData.put(ADS_EVENT,event.getType().toString());
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)


    /**
     * Information about the campaign that resulted in the API call, containing name, source, medium,
     * term and content. This maps directly to the common UTM campaign parameters.
     *
     * @see <a href="https://support.google.com/analytics/answer/1033867?hl=en">UTM parameters</a>
     */
    public static class Campaign extends ValueMap {

        private static final String CAMPAIGN_NAME_KEY = "name";
        private static final String CAMPAIGN_SOURCE_KEY = "source";
        private static final String CAMPAIGN_MEDIUM_KEY = "medium";
        private static final String CAMPAIGN_TERM_KEY = "term";
        private static final String CAMPAIGN_CONTENT_KEY = "content";

        // Public Constructor
        public Campaign() {}

        // For deserialization
        private Campaign(Map<String, Object> map) {
            super(map);
        }

        @Override
        public Campaign putValue(String key, Object value) {
            super.putValue(key, value);
            return this;
        }

        /** Set the UTM campaign name. */
        public Campaign putName(String name) {
            return putValue(CAMPAIGN_NAME_KEY, name);
        }

        public String name() {
            return getString(CAMPAIGN_NAME_KEY);
        }

        /** Set the UTM campaign source. */
        public Campaign putSource(String source) {
            return putValue(CAMPAIGN_SOURCE_KEY, source);
        }

        public String source() {
            return getString(CAMPAIGN_SOURCE_KEY);
        }

        /** Set the UTM campaign medium. */
        public Campaign putMedium(String medium) {
            return putValue(CAMPAIGN_MEDIUM_KEY, medium);
        }

        public String medium() {
            return getString(CAMPAIGN_MEDIUM_KEY);
        }

        /** Set the UTM campaign term. */
        public Campaign putTerm(String term) {
            return putValue(CAMPAIGN_TERM_KEY, term);
        }

        /** @deprecated Use {@link #term()} instead. */
        public String tern() {
            return term();
        }

        public String term() {
            return getString(CAMPAIGN_TERM_KEY);
        }

        /** Set the UTM campaign content. */
        public Campaign putContent(String content) {
            return putValue(CAMPAIGN_CONTENT_KEY, content);
        }

        public String content() {
            return getString(CAMPAIGN_CONTENT_KEY);
        }
    }

    /** Information about the device. */
    public static class Device extends ValueMap {

        @Private static final String DEVICE_ID_KEY = "id";
        @Private static final String DEVICE_MANUFACTURER_KEY = "manufacturer";
        @Private static final String DEVICE_MODEL_KEY = "model";
        @Private static final String DEVICE_NAME_KEY = "name";
        @Private static final String DEVICE_TOKEN_KEY = "token";
        @Private static final String DEVICE_ADVERTISING_ID_KEY = "advertisingId";
        @Private static final String DEVICE_AD_TRACKING_ENABLED_KEY = "adTrackingEnabled";

        @Private
        Device() {}

        // For deserialization
        private Device(Map<String, Object> map) {
            super(map);
        }

        @Override
        public Device putValue(String key, Object value) {
            super.putValue(key, value);
            return this;
        }

        /** Set the advertising information for this device. */
        void putAdvertisingInfo(String advertisingId, boolean adTrackingEnabled) {
            if (adTrackingEnabled && !isNullOrEmpty(advertisingId)) {
                put(DEVICE_ADVERTISING_ID_KEY, advertisingId);
            }
            put(DEVICE_AD_TRACKING_ENABLED_KEY, adTrackingEnabled);
        }

        /** Set a device token. */
        public Device putDeviceToken(String token) {
            return putValue(DEVICE_TOKEN_KEY, token);
        }
    }



    /** Information about the location of the device. */
    public static class Location extends ValueMap implements LocationListener {

        private static final String LOCATION_LATITUDE_KEY = "latitude";
        private static final String LOCATION_LONGITUDE_KEY = "longitude";
        private static final String LOCATION_SPEED_KEY = "speed";

        // Public constructor
        public Location() {}

        // For deserialization
        private Location(Map<String, Object> map) {
            super(map);
        }

        @Override
        public Location putValue(String key, Object value) {
            super.putValue(key, value);
            return this;
        }

        /** Set the latitude for the location of the device. */
        public Location putLatitude(double latitude) {
            return putValue(LOCATION_LATITUDE_KEY, latitude);
        }

        public double latitude() {
            return getDouble(LOCATION_LATITUDE_KEY, 0);
        }

        /** Set the longitude for the location of the device. */
        public Location putLongitude(double longitude) {
            return putValue(LOCATION_LONGITUDE_KEY, longitude);
        }

        public double longitude() {
            return getDouble(LOCATION_LONGITUDE_KEY, 0);
        }

        /** Set the speed of the device. */
        public Location putSpeed(double speed) {
            return putValue(LOCATION_SPEED_KEY, speed);
        }

        public double speed() {
            return getDouble(LOCATION_SPEED_KEY, 0);
        }

        @Override
        public void onLocationChanged(@NonNull android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            putLatitude(latitude);
            putLongitude(longitude);
        }
    }

    /** Information about the referrer that resulted in the API call. */
    public static class Referrer extends ValueMap {

        private static final String REFERRER_ID_KEY = "id";
        private static final String REFERRER_LINK_KEY = "link";
        private static final String REFERRER_NAME_KEY = "name";
        private static final String REFERRER_TYPE_KEY = "type";
        private static final String REFERRER_URL_KEY = "url";

        // Public constructor
        public Referrer() {}

        // For deserialization
        public Referrer(Map<String, Object> map) {
            super(map);
        }

        @Override
        public Referrer putValue(String key, Object value) {
            super.putValue(key, value);
            return this;
        }

        /** Set the referrer ID. */
        public Referrer putId(String id) {
            return putValue(REFERRER_ID_KEY, id);
        }

        public String id() {
            return getString(REFERRER_ID_KEY);
        }

        /** Set the referrer link. */
        public Referrer putLink(String link) {
            return putValue(REFERRER_LINK_KEY, link);
        }

        public String link() {
            return getString(REFERRER_LINK_KEY);
        }

        /** Set the referrer name. */
        public Referrer putName(String name) {
            return putValue(REFERRER_NAME_KEY, name);
        }

        public String name() {
            return getString(REFERRER_NAME_KEY);
        }

        /** Set the referrer type. */
        public Referrer putType(String type) {
            return putValue(REFERRER_TYPE_KEY, type);
        }

        public String type() {
            return getString(REFERRER_TYPE_KEY);
        }

        /** @deprecated Use {@link #putUrl(String)} */
        public Referrer putTerm(String url) {
            return putValue(REFERRER_URL_KEY, url);
        }

        /** Set the referrer url. */
        public Referrer putUrl(String url) {
            return putValue(REFERRER_URL_KEY, url);
        }

        public String url() {
            return getString(REFERRER_URL_KEY);
        }
    }
}
