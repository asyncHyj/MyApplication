package com.hyj.lib.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences文件操作工具类
 *
 * @Author hyj
 * @Date 2015-12-15 下午3:12:49
 */
public class SPUtils {
    /**
     * 存放SharedPreferences对象集合
     */
    private static Map<String, SharedPreferences> mapSP = new HashMap<String, SharedPreferences>();

    /**
     * 获取一个SharedPrefernces对象
     *
     * @param context 上下文
     * @param spName  sp文件名
     * @return
     */
    private static SharedPreferences getSharedPre(Context context, String spName) {
        SharedPreferences sp = mapSP.get(spName);
        if (null == sp) {
            synchronized (SPUtils.class) {
                if (null == sp) {
                    sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
                    mapSP.put(spName, sp);
                }
            }
        }
        return sp;
    }

    /**
     * 保存数据到sharedPreference里面
     *
     * @param context 上下文
     * @param spName  sp文件名
     * @param key     关键字
     * @param value   对应的值
     */
    public static void putParam(Context context, String spName, String key, Object value) {
        Map<String, Object> mValues = new HashMap<String, Object>();
        mValues.put(key, value);
        putParam(context, spName, mValues);
    }

    /**
     * 保存数据到sharedPreference里面
     *
     * @param context 上下文
     * @param spName  sp文件名
     * @param mValues Map<String, Object>的格式，要保存的数据
     */
    @SuppressWarnings("unchecked")
    public static void putParam(Context context, String spName, Map<String, Object> mValues) {
        SharedPreferences sp = getSharedPre(context, spName);
        SharedPreferences.Editor editor = sp.edit();

        String key;
        Object value;
        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();

            if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof Set) {
                editor.putStringSet(key, (Set<String>) value);
            }
        }

        editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context  上下文
     * @param spName   sp文件名
     * @param key      关键字
     * @param defValue 默认值
     * @return Object
     */
    public static Object getParam(Context context, String spName, String key, Object defValue) {
        SharedPreferences sp = getSharedPre(context, spName);

        if (defValue instanceof String) {
            return sp.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            return sp.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            return sp.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Long) {
            return sp.getLong(key, (Long) defValue);
        } else if (defValue instanceof Set) {
            return sp.getStringSet(key, (Set<String>) defValue);
        }

        return null;
    }
}