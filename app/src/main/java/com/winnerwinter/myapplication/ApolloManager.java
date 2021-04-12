package com.winnerwinter.myapplication;

import android.content.Context;
import android.os.Looper;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.winnerwinter.type.CustomType;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class ApolloManager {
    private static ApolloClient instance = null;
    private static String initializedServerUrl = null;

    private static CustomTypeAdapter<Date> dateAdapter = new CustomTypeAdapter<Date>() {
        final String isoDatePattern = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
        final SimpleDateFormat dateFormat = new SimpleDateFormat(isoDatePattern, Locale.US);

        @Override
        public Date decode(@NotNull CustomTypeValue<?> customTypeValue) {
            try {
                return dateFormat.parse(customTypeValue.value.toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        @NotNull
        @Override
        public CustomTypeValue<?> encode(Date date) {
            return new CustomTypeValue.GraphQLString(dateFormat.format(date));
        }
    };

    public static ApolloClient getInstance(Context context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("应该在主线程初始化 ApolloClient");
        }

        String serverUrl = ServerManager.getServerUrl(context);

        if (instance != null && serverUrl.equals(initializedServerUrl)) {
            return instance;
        }

        // 初始化 CookieJar
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        // 初始化 OkHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

        // 初始化 ServerUrl
        initializedServerUrl = serverUrl;

        // 初始化 ApolloClient
        instance = ApolloClient.builder().
                serverUrl(serverUrl).
                okHttpClient(okHttpClient).
                build();

        return instance;
    }
}
