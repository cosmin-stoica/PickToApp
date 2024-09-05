package com.example.picktolightapp;


import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "Configuration.ini";
    private static final String TAG = "ConfigManager";
    private Properties properties;

    public ConfigManager(Context context) {
        properties = new Properties();
        loadConfiguration(context);
    }

    private void loadConfiguration(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(CONFIG_FILE_NAME);
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "Configuration file not found in assets. Running with default settings.", e);
            properties.setProperty("Dev", "false");
            properties.setProperty("Login", "Guest");
            properties.setProperty("IP", "");
            properties.setProperty("Port", "");
        }
    }

    public boolean getDevMode() {
        String devMode = properties.getProperty("Dev", "false");
        return Boolean.parseBoolean(devMode);
    }

    public String getLogin(){
        return properties.getProperty("Login","");
    }

    public String getIp(){
        return properties.getProperty("IP","");
    }

    public String getPort(){
        return properties.getProperty("Port","");
    }
}
