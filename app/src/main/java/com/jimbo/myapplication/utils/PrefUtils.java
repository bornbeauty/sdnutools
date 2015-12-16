package com.jimbo.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 *
 * Created by Jimbo on 2015/7/28.
 */
public class PrefUtils {
    private static String defaultName(Context context) {
        return context.getPackageName();
    }

    private static SharedPreferences getPref(Context context, String preferenceName) {
        return context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context, String preferenceName) {
        return getPref(context, preferenceName).edit();
    }

    public static boolean putString(Context context, String key, String value) {
        return putString(context, defaultName(context), key, value);
    }

    public static boolean putString(Context context, String preferenceName, String key, String value) {
        return getEditor(context, preferenceName).putString(key, value).commit();
    }

    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    public static String getString(Context context, String key, String defValue) {
        return getString(context, defaultName(context), key, defValue);
    }

    public static String getString(Context context, String preferenceName, String key, String defValue) {
        return getPref(context, preferenceName).getString(key, defValue);
    }

    public static boolean putInt(Context context, String key, int value) {
        return putInt(context, defaultName(context), key, value);
    }

    public static boolean putInt(Context context, String preferenceName, String key, int value) {
        return getEditor(context, preferenceName).putInt(key, value).commit();
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    public static int getInt(Context context, String key, int defValue) {
        return getInt(context, defaultName(context), key, defValue);
    }

    public static int getInt(Context context, String preferenceName, String key, int defValue) {
        return getPref(context, preferenceName).getInt(key, defValue);
    }

    public static boolean putBoolean(Context context, String key, boolean value) {
        return putBoolean(context, defaultName(context), key, value);
    }

    public static boolean putBoolean(Context context, String preferenceName, String key, boolean value) {
        return getEditor(context, preferenceName).putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getBoolean(context, defaultName(context), key, defValue);
    }

    public static boolean getBoolean(Context context, String preferenceName, String key, boolean defValue) {
        return getPref(context, preferenceName).getBoolean(key, defValue);
    }

    public static boolean putFloat(Context context, String key, float value) {
        return putFloat(context, defaultName(context), key, value);
    }

    public static boolean putFloat(Context context, String preferenceName, String key, float value) {
        return getEditor(context, preferenceName).putFloat(key, value).commit();
    }

    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1f);
    }

    public static float getFloat(Context context, String key, float defValue) {
        return getFloat(context, defaultName(context), key, defValue);
    }

    public static float getFloat(Context context, String preferenceName, String key, float defValue) {
        return getPref(context, preferenceName).getFloat(key, defValue);
    }

    public static boolean putLong(Context context, String key, long value) {
        return putLong(context, defaultName(context), key, value);
    }

    public static boolean putLong(Context context, String preferenceName, String key, long value) {
        return getEditor(context, preferenceName).putLong(key, value).commit();
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, -1l);
    }

    public static long getLong(Context context, String key, long defValue) {
        return getLong(context, defaultName(context), key, defValue);
    }

    public static long getLong(Context context, String preferenceName, String key, long defValue) {
        return getPref(context, preferenceName).getLong(key, defValue);
    }

    public static boolean putStringSet(Context context, String key, Set<String> value) {
        return putStringSet(context, defaultName(context), key, value);
    }

    public static boolean putStringSet(Context context, String preferenceName, String key, Set<String> value) {
        return getEditor(context, preferenceName).putStringSet(key, value).commit();
    }

    public static Set<String> getStringSet(Context context, String key) {
        return getStringSet(context, key, null);
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defValue) {
        return getStringSet(context, defaultName(context), key, defValue);
    }

    public static Set<String> getStringSet(Context context, String preferenceName, String key, Set<String> defValue) {
        return getPref(context, preferenceName).getStringSet(key, defValue);
    }

    public static boolean clear(Context context) {
        return clear(context, defaultName(context));
    }

    public static boolean clear(Context context, String preferenceName) {
        return getEditor(context, preferenceName).clear().commit();
    }

    public static boolean remove(Context context, String key) {
        return remove(context, defaultName(context), key);
    }

    public static boolean remove(Context context, String preferenceName, String key) {
        return getEditor(context, preferenceName).remove(key).commit();
    }
}
