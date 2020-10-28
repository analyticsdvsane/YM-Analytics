package com.yuktamedia.analytics;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.common.base.Splitter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yuktamedia.analytics.internal.Utils.createMap;
import static java.util.stream.Collectors.toMap;

public class AdsDataCollector extends ValueMap implements AdEvent.AdEventListener, AdErrorEvent.AdErrorListener, AdsLoader.AdsLoadedListener {
    Context context;
    Map<String, Object> result = new HashMap<>();
    //    AdEvent[type=AD_PROGRESS,
    private static final String AdEvent_DETAILS = "AdEvent";
    private static final String AdEvent_KEY = "AD_EVENT";
    private static final String AdEvent_NAME = "AD_EVENT_NAME";
    private static final String adID = "adID";
    private static final String creativeId = "creativeId";
    private static final String creativeAdId = "creativeAdId";
    private static final String universalAdIdValue = "universalAdIdValue";
    private static final String universalAdIdRegistry = "universalAdIdRegistry";
    private static final String title = "title";
    private static final String description = "description";
    private static final String contentType = "contentType";
    private static final String adWrapperIds = "adWrapperIds";
    private static final String adWrapperSystems = "adWrapperSystems";
    private static final String adWrapperCreativeIds = "adWrapperCreativeIds";
    private static final String adSystem = "adSystem";
    private static final String advertiserName = "advertiserName";
    private static final String surveyUrl = "surveyUrl";
    private static final String dealId = "dealId";
    private static final String linear = "linear";
    private static final String skippable = "skippable";
    private static final String width = "width";
    private static final String height = "height";
    private static final String vastMediaHeight = "vastMediaHeight";
    private static final String vastMediaWidth = "vastMediaWidth";
    private static final String vastMediaBitrate = "vastMediaBitrate";
    private static final String traffickingParameters = "traffickingParameters";
    //    clickThroughUrl=https://pubads.g.doubleclick.net/pcs/click?xai=AKAOjsswJjGm2pQ4hdMNG9KyAKOvsqIOs06_xZJVXM6tLJH54y32h7VCmFj_ebaqlfjl_tyn3VojO7CQc8P78LDwqgW1k1Yrck2v0B1YwfWOuodcF2szqQY3Uqeo46Hypw54Jis7sItWTQhBuJrIsnJp5jMbZWZSGQam0eKyFHelChEkPEensUmlLeQj2RtVIlJ1LzGN9LIoLv7F4tmyXdHyvDF7tg-OuEWgvEUwQz79b2qqtFw7rP9KLmiDRBoYyYxe2uGEL2rpyEnlOhNVhPu-ZwDO&sai=AMfl-YRboCeFSnkXSyGTJe8KIfVPrp_4SVwdVASEK_Bp-g9rl2YcTKW-0vobQOYUkQXAqQeFh2gofj1cxFz9T0w2OY4chYEe0Rn-dPu6V0KQXAKqIURAp72bOWJOHubZKuZU7VwDcoeflikfF4oVdV6X3NO346iMjEtpWB_A&sig=Cg0ArKJSzEIg8TndljEM&adurl=http://www.google.com,
    private static final String duration = "duration";
    //    adPodInfo=AdPodInfo
    private static final String adpodinfo = "adpodinfo";
    private static final String AdPodInfo = "AdPodInfo";
    private static final String totalAds = "totalAds";
    private static final String adPosition = "adPosition";
    private static final String isBumper = "isBumper";
    private static final String maxDuration = "maxDuration";
    private static final String podIndex = "podIndex";
    private static final String timeOffset = "timeOffset";
    private static final String uiElements = "uiElements";
    private static final String disableUi = "disableUi";
    private static final String skipTimeOffset = "skipTimeOffset";
    Gson gson = new Gson();
//    void putLibrary() {
//        Map<String, Object> library = createMap();
//        library.put(LIBRARY_NAME_KEY, "analytics-android");
//        library.put(LIBRARY_VERSION_KEY, BuildConfig.VERSION_NAME);
//        put(LIBRARY_KEY, library);
//    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAdEvent(AdEvent event) {
        //AdEvent
        Map<String, Object> AdEventType = createMap();
        AdEventType.put(AdEvent_KEY, event.getType());
        put(AdEvent_KEY, AdEventType);

//        Map<String,String> admap = event.getAdData();
//        for (Map.Entry<String,String> entry : admap.entrySet()){
//            System.out.println("Key = " + entry.getKey() +
//                    ", Value = " + entry.getValue());
//
//        }


        Log.e("Event.getAdData", "Ads_Data" + event.toString());
//        convertAdsData(event.toString());
        switch (event.getType()) {
            case ALL_ADS_COMPLETED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_Size", "ALL_ADS_COMPLETED");
                put(AdEvent_KEY, AdEventType);
//                put(AdEvent_DETAILS,AdEventData);
                String rawdata = event.toString();
                String array = gson.toJson(rawdata);
//                Analytics.with(context).track(admap);
                convertAdsData(event.toString());
                Log.e("Name", "EVENT_ID" + event.getAd().getAdId());
                break;// Do nothing
            case AD_BREAK_FETCH_ERROR:
                Log.e("AdData_SIze", "AD_BREAK_FETCH_ERROR");
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                convertAdsData(event.toString());
                break;
            case CLICKED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
//                int a = admap.size();
                String rawdataClicked = event.toString();

//                Log.e("AdData_SIze", String.valueOf(a));
                convertAdsData(event.toString());
                break;
            case COMPLETED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "COMPLETED");

                convertAdsData(event.toString());
                break;
            case CUEPOINTS_CHANGED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "CUEPOINTS_CHANGED");
                convertAdsData(event.toString());
                break;
            case CONTENT_PAUSE_REQUESTED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "CONTENT_PAUSE_REQUESTED");
                convertAdsData(event.toString());
                break;
            case CONTENT_RESUME_REQUESTED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "CONTENT_RESUME_REQUESTED");
                convertAdsData(event.toString());
                break;
            case FIRST_QUARTILE:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                String rawdataFirstQuartlie = event.toString();
                Log.e("AdData_SIze", "FIRST_QUARTILE");
                String firstquartilearray = gson.toJson(rawdataFirstQuartlie);
//                Analytics.with(context).track(rawdataFirstQuartlie);
//                Analytics.with(context).trackJSON(admap);
                convertAdsData(event.toString());
                break;
            case LOG:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "LOG");
//                convertAdsData(event.toString());
                convertAdsData(event.toString());
                break;
            case AD_BREAK_READY:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "AD_BREAK_READY");
                convertAdsData(event.toString());
                break;
            case MIDPOINT:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "MIDPOINT");
                convertAdsData(event.toString());
                break;
            case PAUSED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "PAUSED");
                convertAdsData(event.toString());
                break;
            case RESUMED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "RESUMED");
                convertAdsData(event.toString());
                break;
            case SKIPPABLE_STATE_CHANGED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "SKIPPABLE_STATE_CHANGED");
                convertAdsData(event.toString());
                break;
            case SKIPPED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "SKIPPED");
                convertAdsData(event.toString());
                break;
            case STARTED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "STARTED");
                convertAdsData(event.toString());
            case TAPPED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "TAPPED");
                convertAdsData(event.toString());
                break;
            case ICON_TAPPED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "ICON_TAPPED");
                convertAdsData(event.toString());
                break;
            case ICON_FALLBACK_IMAGE_CLOSED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "ICON_FALLBACK_IMAGE_CLOSED");
                convertAdsData(event.toString());
//                Analytics.with(context).trackJSON(admap);
                break;
            case THIRD_QUARTILE:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "THIRD_QUARTILE");
                Map<String, String> admap1 = event.getAdData();
                convertAdsData(event.toString());
//                Analytics.with(context).trackJSON(admap1);
                break;
            case LOADED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "LOADED");
                event.getAdData();
                convertAdsData(event.toString());
                break;
            case AD_PROGRESS:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "AD_PROGRESS");

                convertAdsData(event.toString());
                break;
            case AD_BUFFERING:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "AD_BUFFERING");
                convertAdsData(event.toString());
                break;
            case AD_BREAK_STARTED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "AD_BREAK_STARTED");
                convertAdsData(event.toString());
                break;
            case AD_BREAK_ENDED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "AD_BREAK_ENDED");

                convertAdsData(event.toString());
                break;
            case AD_PERIOD_STARTED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "AD_PERIOD_STARTED");
//                Log.e("Name",event.getType().toSt
                convertAdsData(event.toString());
                break;
            case AD_PERIOD_ENDED:
                Log.e("eventname", "RAW_EVENT_DATA" + event.toString());
                Log.e("AdData_SIze", "AD_PERIOD_ENDED");
                Log.e("Name", event.toString());
                convertAdsData(event.toString());
                break;
            default:
                Log.e("AdData_SIze", "Default");
                Log.e("Ads", event.getType().toString());
                break;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void convertAdsData(String toString) {
        String whitedpace_removal4 = toString.replace("=[],", "=Null,");
        String removeAdEvent = whitedpace_removal4.replace("AdEvent[", "");
        String adAD = removeAdEvent.replace("ad=Ad [", "");
        String nulladAD = adAD.replace("]", "");

        String whitedpace_removal2 = nulladAD.replace("= ,", "=Null,");
        String whitedpace_removal3 = whitedpace_removal2.replace("=, ", "=Null,");
        String whitedpace_removal = whitedpace_removal3.replace("=,", "=Null,");
        String removeEmpty = whitedpace_removal.replace("[]", "");
        String deleted = removeEmpty.replace("adPodInfo=AdPodInfo", "");

        String brackets_removal = deleted.replace("[", "");
        String bracket_close = brackets_removal.replace("]", "");
        String finalString = bracket_close.trim();

        Log.e("eventname", "FinalString" + finalString);
//        Log.e("eventname", "CON_RAW_EVENT_DATA" + toString);
        TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
        splitter.setString(finalString);
        for (String data : splitter) {

            toMap(data);
            readyMap();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toMap(String data) {
        Log.e("eventname", "Data_to_be_seprate" + data);
        String rawString[] = data.split("=");
        String key = rawString[0].trim();
        Log.e("RawString", "KEY" + rawString[0] + "Value" + rawString[1]);

        String value = rawString[1].trim();
        if (result.isEmpty()) {
            result.put(key, value);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                result.computeIfAbsent(key, k -> value);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void readyMap() {
        for (Entry<String, Object> entry : result.entrySet()) {
            Log.e("", "[Key]:" + entry.getKey() + "   " + entry.getValue());
        }
        Log.e("MAP_SIZE", "Final_MAP_SIZE" + String.valueOf(result.size()));
        Gson gson = new Gson();
        String json = gson.toJson(result);
        Analytics.with(context).track(json);
    }


    @Override
    public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {

    }


}
