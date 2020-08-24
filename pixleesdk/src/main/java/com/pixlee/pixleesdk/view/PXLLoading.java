package com.pixlee.pixleesdk.view;

import android.content.Context;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.pixlee.pixleesdk.util.PXLViewUtil;

/**
 * Created by sungjun on 8/24/20.
 */
public class PXLLoading extends LottieAnimationView {
    public PXLLoading(Context context) {
        super(context);
        init();
    }

    public PXLLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PXLLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){
        String json = PXLViewUtil.getLottieLoadingJson(getContext());
        setAnimationFromJson(json, json);
        setRepeatCount(LottieDrawable.INFINITE);
        playAnimation();
    }
}
