package com.winnerwinter.myapplication;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;

import com.alibaba.fastjson.JSON;

public class UserManager {
    @SuppressLint("StaticFieldLeak")
    private static UserManager instance = null;
    private Context context = null;

    private static final String KEY_USER_MANAGER = "user_manager";
    private static final String KEY_USER = "user";

    private UserManager() {}

    public static UserManager getInstance(Context context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("应该在主线程初始化 UserManager。");
        }

        instance = new UserManager();
        instance.context = context.getApplicationContext();

        return instance;
    }

    public User loadUser() {
        SharedPreferences sharedPrefs = this.context.getSharedPreferences(KEY_USER_MANAGER, Context.MODE_PRIVATE);
        String userJSON = sharedPrefs.getString(KEY_USER, "null");
        return JSON.parseObject(userJSON, User.class);
    }

    public void saveUser(User user) {
        SharedPreferences sharedPrefs = this.context.getSharedPreferences(KEY_USER_MANAGER, Context.MODE_PRIVATE);
        String userJSON = JSON.toJSONString(user);
        sharedPrefs.edit().putString(KEY_USER, userJSON).apply();
    }
}
