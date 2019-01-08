package com.appaspect.news.androidnewslibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appaspect.news.androidnewslibrary.utils.ANL_Constant_Data;
import com.google.gson.Gson;

import ir.alirezabdn.wp7progress.WP7ProgressBar;


public class News_Details_Activity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {

    private String TAG=News_Details_Activity.class.getSimpleName();
    private LinearLayout adContainer;
    private WebView webview ;
    private String str_url="https://www.google.co.in";
    private String str_title="Google";
    private WebView web_view;
    private TextView txtNoData;
    private WP7ProgressBar wp7progressBar;
    private SwipeRefreshLayout srl_news_list;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(ANL_Constant_Data.colorCode)); // set your desired color
        actionBar.setTitle(getString(R.string.news_details));
        gson = new Gson();


        Intent intent_data=getIntent();
        Bundle bundle_data=intent_data.getExtras();

        if(bundle_data!=null)
        {
            str_title=bundle_data.getString("news_title");
            str_url=bundle_data.getString("news_url");
            Log.e("str_url ",str_url);
            Log.e("str_title ",str_title);
            actionBar.setTitle(str_title);
        }


        try
        {
            wp7progressBar = (WP7ProgressBar)findViewById(R.id.wp7progressBar);
            wp7progressBar.setIndicatorColor(ANL_Constant_Data.colorCode);

            // SwipeRefreshLayout
            srl_news_list= (SwipeRefreshLayout) findViewById(R.id.srl_news_list);
            srl_news_list.setOnRefreshListener(this);
            srl_news_list.setColorSchemeResources(R.color.colorPrimary,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark);
            srl_news_list.setRefreshing(false);


            web_view=(WebView)findViewById(R.id.web_view);
            txtNoData=(TextView) findViewById(R.id.txtNoData);
            web_view.getSettings().setJavaScriptEnabled(true);
            web_view.setWebViewClient(new myWebClient());
            web_view.setInitialScale(1);
            web_view.getSettings().setBuiltInZoomControls(false);
            web_view.getSettings().setUseWideViewPort(true);


            web_view.setWebChromeClient(new WebChromeClient()
            {

                @Override
                public void onProgressChanged(WebView view, int progress)
                {

                    Log.e(TAG+" web_view progress ",progress+"");

                    if(progress == 100)
                    {
                        wp7progressBar.hideProgressBar();
                        wp7progressBar.setVisibility(WP7ProgressBar.GONE);
                    }
                }
            });

// for scroll view
            web_view.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (web_view.getScrollY() == 0) {
                        srl_news_list.setEnabled(true);
                    } else {
                        srl_news_list.setEnabled(false);
                    }
                }
            });

            display_live_match();
        }
        catch (Exception ex)
        {
            Log.e("News Detail webview err",""+ex.toString());
        }


//        try {
//            // Instantiate an AdView view
//            adView = new AdView(this, ConstantData.Facebook_Banner_UNIT_ID, AdSize.BANNER_HEIGHT_50);
//
//            // Find the Ad Container
//            adContainer = (LinearLayout) findViewById(R.id.banner_container);
//
//
//            // Add the ad view to your activity layout
//            adContainer.addView(adView);
//            adContainer.setVisibility(LinearLayout.VISIBLE);
//
//            adView.setAdListener(new AdListener() {
//                @Override
//                public void onError(Ad ad, AdError adError) {
//                    // Ad error callback
//                    Log.e("onError", "" + ad.toString());
//                }
//
//                @Override
//                public void onAdLoaded(Ad ad) {
//                    // Ad loaded callback
//                    Log.e("onAdLoaded", "" + ad.toString());
//                }
//
//                @Override
//                public void onAdClicked(Ad ad) {
//                    // Ad clicked callback
//                }
//
//                @Override
//                public void onLoggingImpression(Ad ad) {
//                    // Ad impression logged callback
//                }
//            });
//
//
//            // Request an ad
//            adView.loadAd();
//        } catch (Exception e) {
//
//        }



       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/





    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        display_live_match();

    }
    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh()
    {

        stop_SwipeRefreshLayout();
        // Fetching data from server
        display_live_match();

    }
    public void  stop_SwipeRefreshLayout()
    {
        if(srl_news_list!=null)
        {
            // Stopping swipe refresh
            srl_news_list.setRefreshing(false);
        }
    }


    public void  display_live_match()
    {


        if(ANL_Constant_Data.isConnectionAvailable(this))
        {
            wp7progressBar.setVisibility(WP7ProgressBar.VISIBLE);
            // for showing
            wp7progressBar.showProgressBar();
            txtNoData.setVisibility(TextView.GONE);
            web_view.loadUrl(str_url);

        }
        else
        {
            txtNoData.setVisibility(TextView.VISIBLE);
            wp7progressBar.setVisibility(WP7ProgressBar.GONE);
            web_view.setVisibility(WebView.GONE);
            Toast.makeText(this,getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();


        }



    }
    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            str_url=url;
            view.loadUrl(url);
            return true;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (web_view.canGoBack()) {
                        web_view.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }




    @Override
    protected void onDestroy() {
//        if (adView != null) {
//            adView.destroy();
//        }
        super.onDestroy();
    }



}
