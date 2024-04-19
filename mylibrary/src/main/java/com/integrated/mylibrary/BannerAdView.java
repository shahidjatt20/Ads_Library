package com.integrated.mylibrary;


import android.content.Context;
import android.util.AttributeSet;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import android.widget.LinearLayout;

public class BannerAdView extends LinearLayout {

    private AdView adView;

    public BannerAdView(Context context) {
        super(context);
        initialize(context);
    }

    public BannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public BannerAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        adView = new AdView(context);
        AdSize adSize = AdSize.BANNER;
        adView.setAdSize(adSize);
        adView.setAdUnitId("YOUR_AD_UNIT_ID");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        addView(adView);
    }
}
