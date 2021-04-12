package com.winnerwinter.myapplication;

import android.content.Context;

public class ServerManager {
    public static String getServerUrl(Context context) {
        return "http://192.168.1.38:4000/graphql";
    }
}
