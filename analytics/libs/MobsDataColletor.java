package com.yuktamedia.analytics;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.yuktamedia.analytics.AdsDataCollector;

import java.util.Map;

public class MobsDataColletor  implements  AdEvent.AdEventListener{
    Context context;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getAd(final AdView adView){
        adView.getAdSize();
        final String adID= adView.getAdUnitId();


        adView.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Analytics.with(context).track(adID+"Ads Closed");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Analytics.with(context).track(adID+"Ads FailedToLoad");
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Analytics.with(context).track(adID+"Ads LeftApplication");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Analytics.with(context).track(adID+"Ads Opened");
            }


            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                    Toast.makeText(context,"Ads Loaded",Toast.LENGTH_SHORT).show();
                    Analytics.with(context).track(adID+"Ads Loaded");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Toast.makeText(context,"Ads Clicked",Toast.LENGTH_SHORT).show();
                Analytics.with(context).track(adID+"Ad Clicked");

            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Toast.makeText(context,"Ads Impression",Toast.LENGTH_SHORT).show();
                Analytics.with(context).track(adID+"Ad Impression");
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onAdEvent(AdEvent event) {
        switch (event.getType()) {
            case ALL_ADS_COMPLETED:
                Log.e("AdData_SIze","ALL_ADS_COMPLETED");
                Analytics.with(context).track(event.toString());
                break;// Do nothing
            case AD_BREAK_FETCH_ERROR:
                Log.e("AdData_SIze","AD_BREAK_FETCH_ERROR");
                Analytics.with(context).track(event.toString());
                break;
            case CLICKED:
                Map<String,String> admap = event.getAdData();
                int a = admap.size();
                Log.e("AdData_SIze", String.valueOf(a));
                Analytics.with(context).track(event.toString());
                break;
            case COMPLETED:
                Log.e("AdData_SIze","COMPLETED");
                Analytics.with(context).track(event.toString());
                break;
            case CUEPOINTS_CHANGED:
                Log.e("AdData_SIze","CUEPOINTS_CHANGED");
                Analytics.with(context).track(event.toString());
                break;
            case CONTENT_PAUSE_REQUESTED:
                Log.e("AdData_SIze","CONTENT_PAUSE_REQUESTED");
                Analytics.with(context).track(event.toString());
                break;
            case CONTENT_RESUME_REQUESTED:
                Log.e("AdData_SIze","CONTENT_RESUME_REQUESTED");
                Analytics.with(context).track(event.toString());
                break;
            case FIRST_QUARTILE:
                Log.e("AdData_SIze","FIRST_QUARTILE");
                Analytics.with(context).track(event.toString());
                break;
            case LOG:
                Log.e("AdData_SIze","LOG");
                Analytics.with(context).track(event.toString());
                break;
            case AD_BREAK_READY:
                Log.e("AdData_SIze","AD_BREAK_READY");
                Analytics.with(context).track(event.toString());
                break;
            case MIDPOINT:
                Log.e("AdData_SIze","MIDPOINT");
                Analytics.with(context).track(event.toString());
                break;
            case PAUSED:
                Log.e("AdData_SIze","PAUSED");
                Analytics.with(context).track(event.toString());
                break;
            case RESUMED:
                Log.e("AdData_SIze","RESUMED");
                Analytics.with(context).track(event.toString());
                break;
            case SKIPPABLE_STATE_CHANGED:
                Log.e("AdData_SIze","SKIPPABLE_STATE_CHANGED");
                Analytics.with(context).track(event.toString());
                break;
            case SKIPPED:
                Log.e("AdData_SIze","SKIPPED");
                Analytics.with(context).track(event.toString());
                break;
            case STARTED:
                Log.e("AdData_SIze","STARTED");
                Analytics.with(context).track(event.toString());
            case TAPPED:
                Log.e("AdData_SIze","TAPPED");
                Analytics.with(context).track(event.toString());
                break;
            case ICON_TAPPED:
                Log.e("AdData_SIze","ICON_TAPPED");
                Analytics.with(context).track(event.toString());
                break;
            case ICON_FALLBACK_IMAGE_CLOSED:
                Log.e("AdData_SIze","ICON_FALLBACK_IMAGE_CLOSED");
                Analytics.with(context).track(event.toString());
                break;
            case THIRD_QUARTILE:
                Log.e("AdData_SIze","THIRD_QUARTILE");
                Analytics.with(context).track(event.toString());
                break;
            case LOADED:
                Log.e("AdData_SIze","LOADED");
                Analytics.with(context).track(event.toString());
                break;
            case AD_PROGRESS:
                Log.e("AdData_SIze","AD_PROGRESS");
                Analytics.with(context).track(event.toString());
                break;
            case AD_BUFFERING:
                Log.e("AdData_SIze","AD_BUFFERING");
                Analytics.with(context).track(event.toString());
                break;
            case AD_BREAK_STARTED:
                Log.e("AdData_SIze","AD_BREAK_STARTED");
                Analytics.with(context).track(event.toString());
                break;
            case AD_BREAK_ENDED:
                Log.e("AdData_SIze","AD_BREAK_ENDED");
                Analytics.with(context).track(event.toString());
                break;
            case AD_PERIOD_STARTED:
                Log.e("AdData_SIze","AD_PERIOD_STARTED");
                Analytics.with(context).track(event.toString());
                break;
            case AD_PERIOD_ENDED:
                Log.e("AdData_SIze","AD_PERIOD_ENDED");
                Analytics.with(context).track(event.toString());
                break;
            default:
                Log.e("AdData_SIze","Default");
                Log.e("Ads", event.getType().toString());
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadInrertical(final InterstitialAd interstitialAd){
        final String adID= interstitialAd.getAdUnitId();


        interstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Analytics.with(context).track(adID+"Int Ads Closed");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Analytics.with(context).track(adID+"Int Ads FailedToLoad");
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Analytics.with(context).track(adID+"Int Ads LeftApplication");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Analytics.with(context).track(adID+"Int Ads Opened");
            }


            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Toast.makeText(context,"Ads Loaded",Toast.LENGTH_SHORT).show();
                Analytics.with(context).track(adID+"IntAds Loaded");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Toast.makeText(context,"Ads Clicked",Toast.LENGTH_SHORT).show();
                Analytics.with(context).track(adID+"Int Ad Clicked");

            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Toast.makeText(context,"Ads Impression",Toast.LENGTH_SHORT).show();
                Analytics.with(context).track(adID+"Int Ad Impression");
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void rewardAds(RewardedAdLoadCallback adLoadCallback, RewardedAd rewardedAd ){
        adLoadCallback = new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                Analytics.with(context).track("Rewards  Ads Loaded");
            }
            @Override
            public void onRewardedAdFailedToLoad(int i) {
                super.onRewardedAdFailedToLoad(i);
                Analytics.with(context).track("Rewards Ads Closed");
            }
        };

        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }
}
