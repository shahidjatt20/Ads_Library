package com.integrated.mylibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.Objects;


public class Admob_Helper extends Activity {
    static AdView adView;
    static InterstitialAd interstitialAd1;
    static AdRequest adRequest;
    @SuppressLint("StaticFieldLeak")
    static View inflatedView;
    @SuppressLint("StaticFieldLeak")
    static AdLoader adLoader;
    @SuppressLint("StaticFieldLeak")
    static NativeAdView nativeAdView;
    @SuppressLint("StaticFieldLeak")
    static FrameLayout nativeContainer;
    static NativeAd nativeAd1;
    static boolean connected = false;
    static String TAG = "admob_ad";
    @SuppressLint("StaticFieldLeak")
    static FrameLayout bannerLayout;
    static ShimmerFrameLayout shimmerFrameLayout;
    @SuppressLint("StaticFieldLeak")
    static ScrollView scrollView;

    public static void iniSDk(Activity activity,String interstitial_id) {

        MobileAds.initialize(activity, initializationStatus -> Log.e(TAG, "admob sdk initialized success"));
//         Admob_Helper.loadAdmobInterstitialAd(activity,interstitial_id,null);
    }

    //.. adaptive banner
    public static void loadAdaptiveBannerAd(Activity activity,String banner_id) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adView = new AdView(activity);
                adView.setAdUnitId(banner_id);
                bannerLayout = activity.findViewById(R.id.adView);
                shimmerFrameLayout = activity.findViewById(R.id.shimmer_layout);
                bannerLayout.addView(adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e(TAG, "banner ad: loading failed " + loadAdError.getMessage());
                        Log.e(TAG, "banner ad: loading failed " + loadAdError);
                        Log.e(TAG, "banner ad: loading failed " + loadAdError.getDomain());
                        Log.e(TAG, "banner ad: loading failed " + loadAdError.getResponseInfo());
                        Log.e(TAG, "banner ad: loading failed " + loadAdError.getCause());
                        LinearLayout linearLayout=activity.findViewById(R.id.view);
                        linearLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.e(TAG, "banner ad: loaded success");
                        bannerLayout.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                });
                AdSize adSize = getAdSize(activity);
                adView.setAdSize(adSize);
                adView.loadAd(adRequest);
            }
        }, 1000);
    }

    private static AdSize getAdSize(Activity activity) {
        // Step 2 - Determine the screen width (less decorations)
        // to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize
                (activity, adWidth);
    }




    // Native Ad
    private static void populateNativeAdView(NativeAd nativeAd,
                                             NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every NativeAd.
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null || nativeAd.getStarRating() < 3) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView()))
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }

    //... load native ad
    public static void loadAdmobNativeAd(Activity activity,String native_id) {

        new Handler().postDelayed(() -> {
            adLoader = new AdLoader.Builder(activity,
                    native_id)
                    .forNativeAd(nativeAd -> {
                        nativeAd1 = nativeAd;
                        Log.e(TAG, "native ad: loaded success");
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError
                                                             loadAdError) {
                            Log.e(TAG, "native ad : loading failed " +
                                    "k" + loadAdError);
                            Log.e(TAG, "native ad: loading failed " + loadAdError);
                            Log.e(TAG, "native ad: loading failed " + loadAdError.getDomain());
                            Log.e(TAG, "native ad: loading failed " + loadAdError.getResponseInfo());
                            Log.e(TAG, "native ad: loading failed " + loadAdError.getCause());

                        }
                    }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }, 1000);
    }

    //..... load native ad after internet connected
    private static void loadNativeAdAfterInternetConnected(
            Activity activity, View view,String native_id) {
        new Handler().postDelayed(() -> {

            adLoader = new AdLoader.Builder(activity,native_id)
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @SuppressLint("InflateParams")
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {

                            nativeAd1 = nativeAd;
                            Log.e(TAG, "native ad: loaded success");

                            nativeContainer = view.findViewById(R.id.native_container);
                            scrollView = view.findViewById(R.id.scroolview);
                            shimmerFrameLayout=view.findViewById(R.id.shimmer_layout);
                            nativeAdView = (NativeAdView) activity.getLayoutInflater()
                                    .inflate(R.layout.mediation_native_ad, null);

                            populateNativeAdView(nativeAd1, nativeAdView);
                            nativeContainer.removeAllViews();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            nativeContainer.addView(nativeAdView);
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(TAG, "native ad: loading failed   " + loadAdError);
                            Log.e(TAG, "native ad: loading failed " + loadAdError);
                            Log.e(TAG, "native ad: loading failed " + loadAdError.getDomain());
                            Log.e(TAG, "native ad: loading failed " + loadAdError.getResponseInfo());
                            Log.e(TAG, "native ad: loading failed " + loadAdError.getCause());
                            nativeAd1 = null;
                        }
                    }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }, 1000);
    }


    //... show native ad
    @SuppressLint("InflateParams")
    public static void show_AdmobNativeAd(Activity activity,
                                          View view, String native_id) {
        if (nativeAd1 != null){
            nativeContainer = view.findViewById(R.id.native_container);
            scrollView = view.findViewById(R.id.scroolview);
            shimmerFrameLayout=view.findViewById(R.id.shimmer_layout);
            nativeAdView = (NativeAdView) activity.getLayoutInflater()
                    .inflate(R.layout.mediation_native_ad, null);

            populateNativeAdView(nativeAd1, nativeAdView);
            nativeContainer.removeAllViews();
            shimmerFrameLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            nativeContainer.addView(nativeAdView);
            Log.e(TAG,"native ad: already loaded ,ad shown successfully "
                    +activity.getLocalClassName());
        }
        else  {
            loadNativeAdAfterInternetConnected(activity, view,native_id);
            Log.e(TAG, "native ad: connected to internet, loading native..., "
                    +activity.getLocalClassName());
        }

    }



    //... load interstitial ad
    public static void loadAdmobInterstitialAd(Activity activity,String interstitial_id,Intent intent){

            new Handler().postDelayed(() -> {

                adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity,interstitial_id,
                        adRequest, new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                interstitialAd1 = interstitialAd;

                                Log.e(TAG, "interstitial ad: loaded success: 1st attempt");
                                interstitialAd1.setFullScreenContentCallback
                                        (new FullScreenContentCallback(){
                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                // Called when fullscreen content is dismissed.
                                                Log.d(TAG, "interstitial ad: The ad was dismissed.");
                                                if (intent!=null){
                                                    activity.startActivity(intent);
                                                }

                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                                // Called when fullscreen content failed to show.
                                                Log.d(TAG, "interstitial ad: The ad failed to show.");
                                                if (intent!=null){
                                                    activity.startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onAdShowedFullScreenContent() {
                                                // Called when fullscreen content is shown.
                                                // Make sure to set your reference to null so you don't
                                                // show it a second time.
                                                interstitialAd1 = null;
                                                Log.d(TAG, "interstitial ad: The ad was shown.");
                                            }
                                        });
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                Log.e(TAG, "interstitial ad: loading failed: "+loadAdError.getMessage());
                                Log.e(TAG,"interstitial ad: loading failed " + loadAdError);
                                Log.e(TAG,"interstitial ad: loading failed " + loadAdError.getDomain());
                                Log.e(TAG,"interstitial ad: loading failed " + loadAdError.getResponseInfo());
                                Log.e(TAG,"interstitial ad: loading failed " + loadAdError.getCause());
                                interstitialAd1 = null;
                            }
                        });
            },1000);
    }

    //... show interstitial ad
    @SuppressLint("InflateParams")
    public static void showAdmobInterstitial(Activity activity, String interstitial_id, Intent intent){
        // Inflate the layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        inflatedView = inflater.inflate(R.layout.interstitial_loader, null);
        if (interstitialAd1 != null){
//            inflatedView.setVisibility(View.GONE);
            interstitialAd1.show(activity);
            loadAdmobInterstitialAd(activity,interstitial_id,intent);
            Log.e(TAG, "interstitial ad: already loaded: ad shown successfully "
                    +activity.getLocalClassName());
        }
        else {
//            inflatedView.setVisibility(View.GONE);
//            ((ViewGroup) inflatedView.getParent()).removeView(inflatedView);
            if (intent!=null){
                activity.startActivity(intent);
            }
                loadAdmobInterstitialAd(activity,interstitial_id,intent);
            Log.e(TAG, "interstitial ad: loading again 2nd attempt"
                    +activity.getLocalClassName());
        }
    }

    public static void Secret_Codes(Activity activity){
//        Intent intent = new Intent(activity, SecretCodesActivity.class);
//        activity.startActivity(intent);
    }

    public static void Mobile_Tricks(Activity activity){
//        Intent intent = new Intent(activity, TricksActivity.class);
//        activity.startActivity(intent);
    }


    public static void Mobile_Tips(Activity activity){
//        Intent intent = new Intent(activity, TipsActivity.class);
//        activity.startActivity(intent);
    }

    public static void Device_Info(Activity activity){
//        Intent intent = new Intent(activity, DeviceInfoActivity.class);
//        activity.startActivity(intent);
    }



    //..... show activity intent
    private static void getSwitch(Activity activity){
//            switch (App_Const.Const){
//                case 1:
//                    Secret_Codes(activity);
//                    break;
//
//                case 2:
//                    Mobile_Tricks(activity);
//                    //activity.startActivity(new Intent(activity, DataScreen.class));
//
//                    break;
//
//                case 3:
//                    Mobile_Tips(activity);
//                    //activity.startActivity(new Intent(activity, GameScreen.class));
//                    break;
//
//                case 4:
//                    Device_Info(activity);
//                    //activity.startActivity(new Intent(activity, SavedMusic_Screen.class));
//
//                    break;
//
//                case 5:
//                    //activity.startActivity(new Intent(activity, PdfBooksActivity.class));
//                    break;
//
//                case 6:
//                    //activity.startActivity(new Intent(activity, Menu_Screen.class));
//                    //activity.finish();
//                    break;
//            }
    }


    //...... method for load and show native ad
    public static void loadandshowAdmobNative(Activity activity,FrameLayout nativeAdContainerView,String native_id) {
        if (isOnline(activity)){
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("InflateParams")
                @Override
                public void run() {


                    adLoader= new AdLoader.Builder(activity,native_id)
                            .forNativeAd(nativeAd -> {

                            })
                            .withAdListener(new AdListener() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);
                                    Log.e(TAG, "native ad : loading failed " +
                                            "k" + loadAdError);
                                    Log.e(TAG, "native ad: loading failed " + loadAdError);
                                    Log.e(TAG, "native ad: loading failed " + loadAdError.getDomain());
                                    Log.e(TAG, "native ad: loading failed " + loadAdError.getResponseInfo());
                                    Log.e(TAG, "native ad: loading failed " + loadAdError.getCause());

                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }
                            }).build();

                    adLoader.loadAd(new AdRequest.Builder().build());


                    if (nativeAd1 != null){
                        nativeContainer = nativeAdContainerView.findViewById(R.id.native_container);
                        nativeAdView = (NativeAdView) activity.getLayoutInflater()
                                .inflate(R.layout.mediation_native_ad, null);

                        populateNativeAdView(nativeAd1, nativeAdView);
                        nativeContainer.removeAllViews();
                        nativeContainer.addView(nativeAdView);
                        Log.e(TAG,"native ad: already loaded ,ad shown successfully "
                                +activity.getLocalClassName());
                    }
                    else  {
                        loadNativeAdAfterInternetConnected(activity, nativeAdContainerView,native_id);
                        Log.e(TAG, "native ad: connected to internet, loading native..., "
                                +activity.getLocalClassName());
                    }
                }
            },1000);
        }
        else {
            Log.e(TAG, "native ad: cannot load no internet,");
        }
    }

    //....... check internet connection
    public static boolean isOnline(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        //.....connected to a network
        connected = Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE))
                .getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI))
                        .getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }


}