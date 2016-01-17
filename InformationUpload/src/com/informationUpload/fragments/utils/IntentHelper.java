package com.informationUpload.fragments.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: IntentHelper
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class IntentHelper {

        private static volatile IntentHelper mInstance;
        private Context mContext;

        public static IntentHelper getInstance() {

            if (mInstance == null) {
                synchronized (IntentHelper.class) {
                    if (mInstance == null) {
                        mInstance = new IntentHelper();
                    }
                }
            }
            return mInstance;
        }

        public void init(Context context) {
            mContext = context;
        }


        public Intent getSingleIntent(Class<?> clazz, Bundle bundle) {
            return getSingleIntent(clazz, bundle, false);
        }


        public Intent getSingleIntent(Class<?> clazz, Bundle bundle, boolean enableAnimation) {
            if (bundle == null) {
                bundle = new Bundle();
            }
//        if (!bundle.containsKey(SystemConstant.ENABLE_FRAGMENT_ANIMATION)) {
//            bundle.putBoolean(SystemConstant.FRAGMENT_ENABLE_ANIMATION, enableAnimation);
//        }
            return new SingleIntentBuilder(mContext).build(clazz, bundle).getIntents().get(0);
        }
}
