package com.github.chuross.rn;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGListener;
import com.socdm.d.adgeneration.nativead.ADGInformationIconView;
import com.socdm.d.adgeneration.nativead.ADGMediaView;
import com.socdm.d.adgeneration.nativead.ADGNativeAd;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RNAdGenerationBanner extends FrameLayout {

    public static final String EVENT_TAG_ON_MEASURE = "onMeasure";
    private ReactContext reactContext;
    private ADG adg;
    private int freeBannerWidth;
    private int freeBannerHeight;
    private Runnable measureRunnable = new Runnable() {
        @Override
        public void run() {
            int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY);
            measure(widthMeasureSpec, heightMeasureSpec);
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

    public RNAdGenerationBanner(@NonNull final Context context) {
        super(context);
        this.reactContext = (ReactContext) context;

        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        adg = new ADG(getContext());
        refreshBannerLayoutParams(ADG.AdFrameSize.SP);
        adg.setUsePartsResponse(true);

        adg.setAdListener(new ADGListener() {
            @Override
            public void onReceiveAd() {
            }
            @Override
            public void onReceiveAd(Object o) {

                View view = null;
                if (o instanceof ADGNativeAd) {
                    ADGNativeAdView nativeAdView = new ADGNativeAdView(context);
                    nativeAdView.apply((ADGNativeAd) o);
                    view = nativeAdView;
                } else if (o instanceof NativeAd) {
                    FBNativeAdView nativeAdView = new FBNativeAdView(context);
                    nativeAdView.apply((NativeAd) o);
                    view = nativeAdView;
                }

                if (view != null) {
                    // ローテーション時に自動的にViewを削除します
                    adg.setAutomaticallyRemoveOnReload(view);

                    addView(view);
                }
            }
            @Override
            public void onFailedToReceiveAd(ADGConsts.ADGErrorCode code) {
                super.onFailedToReceiveAd(code);

                switch (code) {
                    case EXCEED_LIMIT:
                    case NEED_CONNECTION:
                    case NO_AD:
                        break;
                    default:
                        if (adg != null) adg.start();
                        break;
                }
            }
        });

        addView(adg);
    }

    public void setLocationId(String locationId) {
        adg.setLocationId(locationId);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        post(measureRunnable);
    }

    /**
     * @param bannerType sp|rect|tablet|large
     */
    public void setBannerType(String bannerType) {
        ADG.AdFrameSize frameSize = bannerType != null ? ADG.AdFrameSize.valueOf(bannerType.toUpperCase()) : null;
        if (frameSize == null) return;

        adg.setAdFrameSize(frameSize);
        if (bannerType.equalsIgnoreCase("FREE")){
            adg.setAdFrameSize(frameSize.setSize(freeBannerWidth,freeBannerHeight));
        }
        refreshBannerLayoutParams(frameSize);
    }

    public void setBannerWidth(int bannerWidth) {
        freeBannerWidth = bannerWidth;
    }

    public void setBannerHeight(int bannerHeight) {
        freeBannerHeight = bannerHeight;
    }

    public void load() {
        if (adg != null) adg.start();
    }

    public void destroy() {
        if (adg != null) adg.stop();
        adg = null;
    }

    private Rect getBannerRect(ADG.AdFrameSize frameSize) {
        if (frameSize == null) return null;
        return new Rect(0, 0, (int) PixelUtil.toPixelFromDIP(frameSize.getWidth()), (int) PixelUtil.toPixelFromDIP(frameSize.getHeight()));
    }

    private void refreshBannerLayoutParams(ADG.AdFrameSize frameSize) {
        Rect bannerRect = getBannerRect(frameSize);
        adg.setLayoutParams(new LayoutParams(bannerRect.width(), bannerRect.height()));

        sendSizeChangedEvent(frameSize);
    }

    private void sendSizeChangedEvent(ADG.AdFrameSize frameSize) {
        WritableMap event = Arguments.createMap();
        event.putInt("width", frameSize.getWidth());
        event.putInt("height", frameSize.getHeight());

        sendEvent(EVENT_TAG_ON_MEASURE, event);
    }

    private void sendEvent(String eventTag, WritableMap event) {
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), eventTag, event);
    }

}

class ADGNativeAdView extends RelativeLayout {

    private Activity mActivity;
    private RelativeLayout mContainer;
    private ImageView mIconImageView;
    private TextView mTitleLabel;
    private TextView mDescLabel;
    private FrameLayout mMediaViewContainer;
    private TextView mSponsoredLabel;
    private TextView mCTALabel;

    public ADGNativeAdView(Context context) {
        this(context, null);
    }

    public ADGNativeAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ADGNativeAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ADGNativeAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }
        View layout = LayoutInflater.from(context).inflate(R.layout.adg_ad_view, this);
        mContainer = (RelativeLayout) layout.findViewById(R.id.adg_nativead_view_container);
        mIconImageView = (ImageView) layout.findViewById(R.id.adg_nativead_view_icon);
        mTitleLabel = (TextView) layout.findViewById(R.id.adg_nativead_view_title);
        mTitleLabel.setText("");
        mMediaViewContainer = (FrameLayout) layout.findViewById(R.id.adg_nativead_view_mediaview_container);
        mSponsoredLabel = (TextView) layout.findViewById(R.id.adg_nativead_view_sponsored);

        GradientDrawable borders = new GradientDrawable();
        borders.setColor(Color.WHITE);
        borders.setCornerRadius(10);

    }

    public void apply(ADGNativeAd nativeAd) {

        // アイコン画像
        if (nativeAd.getIconImage() != null) {
            String url = nativeAd.getIconImage().getUrl();
            new DownloadImageAsync(mIconImageView).execute(url);
        }

        // タイトル
        if (nativeAd.getTitle() != null) {
            mTitleLabel.setText(nativeAd.getTitle().getText());
        }

        // メイン画像・動画
        if (nativeAd.canLoadMedia()) {
            ADGMediaView mediaView = new ADGMediaView(mActivity);
            mediaView.setAdgNativeAd(nativeAd);
            mMediaViewContainer.addView(mediaView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mediaView.load();
        }

        // インフォメーションアイコン
        ADGInformationIconView infoIcon = new ADGInformationIconView(getContext(), nativeAd);
        mMediaViewContainer.addView(infoIcon);

        // 広告主
        if (nativeAd.getSponsored() != null) {
            mSponsoredLabel.setText(nativeAd.getSponsored().getValue());
        } else {
            mSponsoredLabel.setText("sponsored");
        }

        // クリックイベント
        nativeAd.setClickEvent(getContext(), mContainer, null);
    }

    /**
     * 画像をロードします(方法については任意で行ってください)
     */
    private class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadImageAsync(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String imageUrl = params[0];
                return BitmapFactory.decodeStream(new URL(imageUrl).openStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.imageView.setImageBitmap(bitmap);
        }
    }
}

class FBNativeAdView extends RelativeLayout {

    private Context mContext;
    private View mContainer;
    private AdIconView mIconImageView;
    private RelativeLayout mMediaViewContainer;
    private TextView mSocialContextLabel;
    private TextView mCTALabel;
    private TextView mBodyLabel;
    private TextView mTitleLabel;

    public FBNativeAdView(Context context) {
        this(context, null);
    }

    public FBNativeAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FBNativeAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public FBNativeAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;

        View layout         = LayoutInflater.from(context).inflate(R.layout.adg_ad_view, this);
        mContainer          = layout.findViewById(R.id.adg_nativead_view_container);
        mIconImageView      = (AdIconView) layout.findViewById(R.id.adg_nativead_view_icon);
        mMediaViewContainer = (RelativeLayout) layout.findViewById(R.id.adg_nativead_view_mediaview_container);
        mTitleLabel         = (TextView) layout.findViewById(R.id.adg_nativead_view_title);
        mSocialContextLabel = (TextView) layout.findViewById(R.id.adg_nativead_view_sponsored);

        mTitleLabel.setText("");
        mSocialContextLabel.setText("");
    }

    public void apply(NativeAd nativeAd) {
        // MediaView
        MediaView mediaView = new MediaView(mContext);
        mMediaViewContainer.addView(mediaView);

        // タイトル
        mTitleLabel.setText(nativeAd.getAdHeadline());

        // 本文
        mBodyLabel.setText(nativeAd.getAdBodyText());

        // ソーシャルコンテキスト
        mSocialContextLabel.setText(nativeAd.getAdSocialContext());

        // AdChoice
        AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeAd, true);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) adChoicesView.getLayoutParams();
        layoutParams.addRule(ALIGN_PARENT_TOP);
        layoutParams.addRule(ALIGN_PARENT_RIGHT);
        mMediaViewContainer.addView(adChoicesView, layoutParams);

        //クリックイベント
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(mTitleLabel);
        clickableViews.add(mCTALabel);
        clickableViews.add(mBodyLabel);
        clickableViews.add(mSocialContextLabel);

        nativeAd.registerViewForInteraction(mContainer, mediaView, mIconImageView, clickableViews);
    }
}