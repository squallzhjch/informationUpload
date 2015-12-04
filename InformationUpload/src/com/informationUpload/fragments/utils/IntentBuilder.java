package com.informationUpload.fragments.utils;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: IntentBuilder
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public interface IntentBuilder {

    IntentBuilder build(Class<?> clazz, Bundle bundle);

    IntentBuilder append(Class<?> clazz, Bundle bundle);

    List<Intent> getIntents();
}