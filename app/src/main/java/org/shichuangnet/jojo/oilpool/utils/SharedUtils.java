package org.shichuangnet.jojo.oilpool.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author jojo
 */
public class SharedUtils {

        private static final String NAME = "config";
        public static final String USER_NAME = "user_name";
        public static final String USER_LOGIN_DATE = "user_login_date";
        public static final String USER_TOKEN = "user_token";

        //键 值
        public static void putInt(Context mContext, String key, int value){
            SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            sp.edit().putInt(key ,value).apply();
        }

        public static void putString(Context mContext, String key, String value){
            SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            sp.edit().putString(key ,value).apply();
        }

        public static void putBoolean(Context mContext, String key, boolean value){
            SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            sp.edit().putBoolean(key ,value).apply();
        }

        //键 默认值
        public static String getString(Context mContext, String key, String defValue){
            SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            return sp.getString(key, defValue);
        }

        public static int getInt(Context mContext, String key, int defValue){
            SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            return sp.getInt(key, defValue);
        }

        public static boolean getBoolean(Context mContext, String key, boolean defValue){
            SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            return sp.getBoolean(key, defValue);
        }

        //删除单个
        public static void deleShare(Context mContext, String key){
            SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            sp.edit().remove(key).apply();
        }

        //删除全部键值对信息
        public static void deleAll(Context mContext){
            SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            sp.edit().clear().apply();
        }
}
