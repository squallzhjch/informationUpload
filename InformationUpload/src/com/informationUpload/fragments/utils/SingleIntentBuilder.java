package com.informationUpload.fragments.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: SingleIntentBuilder
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class SingleIntentBuilder implements IntentBuilder {

    private final Context mContext;
    private final Intent mIntent;
    private final List<Intent> mIntents = new ArrayList<Intent>();

    public SingleIntentBuilder(Context context) {
        mContext = context;
        mIntent = new Intent();
        mIntents.add(mIntent);

    }

    @Override
    public IntentBuilder build(Class<?> clazz, Bundle bundle) {
        mIntent.setClass(mContext, clazz);
        if (bundle != null) {
            mIntent.putExtras(bundle);
        }
        return this;
    }

    @Override
    public IntentBuilder append(Class<?> clazz, Bundle bundle) {
        if (bundle != null) {
            mIntent.putExtras(bundle);
        }
        return this;
    }

    @Override
    public List<Intent> getIntents() {
        return mIntents;
    }

}