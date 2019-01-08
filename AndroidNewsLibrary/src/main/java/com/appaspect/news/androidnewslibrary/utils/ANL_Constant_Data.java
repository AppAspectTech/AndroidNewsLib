package com.appaspect.news.androidnewslibrary.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ANL_Constant_Data {

    // Tag used to cancel the request
    public static final String TAG_REQ_News = "TAG_REQ_News";
    public static final String News_keyword = "News_keyword";
    public static final String News_Color_Code = "News_Color_Code";
    public static int colorCode ;

    public static final SimpleDateFormat match_Date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    public static final  SimpleDateFormat news_pubDate_format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.US);

    public static final  SimpleDateFormat news_pubDate_format_yyyy_mm_dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final  SimpleDateFormat news_pubDate_format_display = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
    public static final  SimpleDateFormat date_format_full = new SimpleDateFormat("E, dd MMM yyyy");
    public static final  SimpleDateFormat HH_mm_ss_date_format = new SimpleDateFormat("HH:mm:ss");


    public static boolean isConnectionAvailable(Context context) {

        NetworkInfo networkInfo=null;
        try
        {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = cm.getActiveNetworkInfo();

        } catch (Exception e)
        {

        }

        return networkInfo != null && networkInfo.isConnected();
//        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
