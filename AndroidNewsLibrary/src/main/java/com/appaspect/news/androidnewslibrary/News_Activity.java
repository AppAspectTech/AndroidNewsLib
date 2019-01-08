package com.appaspect.news.androidnewslibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appaspect.news.androidnewslibrary.data.RssFeedModel;
import com.appaspect.news.androidnewslibrary.networks.WebServicesURLs;
import com.appaspect.news.androidnewslibrary.utils.ANL_Constant_Data;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import ir.alirezabdn.wp7progress.WP7ProgressBar;


public class News_Activity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {

    private String TAG=News_Activity.class.getSimpleName();
    private LinearLayout adContainer;
    private TextView txtNoData;
    private SwipeRefreshLayout srl_news_list;
    private ArrayList<RssFeedModel> rssFeed_list=new ArrayList<RssFeedModel>();
    private WP7ProgressBar wp7progressBar;
    private RequestQueue mRequestQueue;
    private RecyclerView lst_news;
    private String str_getCountry,str_ned,str_gl,str_hl;
    private String imageURL = null;
    private Gson gson;
    private NewsAdapter newsAdapter;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription,str_keyword="",str_colorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news);

        Intent intent_data=getIntent();
        Bundle bundle_data=intent_data.getExtras();

        if(bundle_data!=null)
        {
            str_keyword=   bundle_data.getString(ANL_Constant_Data.News_keyword);
            str_colorCode =   bundle_data.getString(ANL_Constant_Data.News_Color_Code);
            ANL_Constant_Data.colorCode= Color.parseColor(str_colorCode);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(ANL_Constant_Data.colorCode)); // set your desired color
        actionBar.setTitle(getString(R.string.news_title));
        gson = new Gson();





        Locale locale = Locale.getDefault();

        String str_getLanguage=locale.getLanguage();
        if(TextUtils.isEmpty(str_getLanguage))
        {
            str_getLanguage="en";
        }

        try
        {
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            str_getCountry = tm.getSimCountryIso();

        }
        catch (Exception e)
        {
            Log.e("TelephonyManager:- ",e.getMessage());
        }


        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                locale= getResources().getConfiguration().getLocales().get(0);
            }
            else
            {
                //noinspection deprecation
                locale= getResources().getConfiguration().locale;
            }

        }
        catch (Exception e)
        {
            Log.e("getLocales:- ",e.getMessage());
        }


        if(TextUtils.isEmpty(str_getCountry))
        {
            str_getCountry=locale.getCountry();

            if(TextUtils.isEmpty(str_getCountry))
            {
                str_getCountry="US";
            }
        }

        str_ned=str_getCountry.toLowerCase();
        str_gl=str_getCountry.toUpperCase();
        str_hl=str_getLanguage;

        Log.e("str_ned:- ",str_ned);
        Log.e("str_gl:- ",str_gl);
        Log.e("str_hl:- ",str_hl);


        try
        {
            wp7progressBar = (WP7ProgressBar)findViewById(R.id.wp7progressBar);
            wp7progressBar.setIndicatorColor(ANL_Constant_Data.colorCode);

            txtNoData= (TextView) findViewById(R.id.txtNoData);

            lst_news = (RecyclerView) findViewById(R.id.lst_news);
            lst_news.setLayoutManager(new LinearLayoutManager(this));

            // SwipeRefreshLayout
            srl_news_list= (SwipeRefreshLayout) findViewById(R.id.srl_news_list);
            srl_news_list.setOnRefreshListener(this);
            srl_news_list.setColorSchemeResources(android.R.color.holo_purple,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark);

            /**
             * Showing Swipe Refresh animation on activity create
             * As animation won't start on onCreate, post runnable is used
             */
            srl_news_list.post(new Runnable() {

                @Override
                public void run() {

                    if(rssFeed_list.size()<=0)
                    {
                        srl_news_list.setRefreshing(false);
                        call_news_ws();
                    }


                }
            });



            newsAdapter = new NewsAdapter(rssFeed_list, this);
            lst_news.setAdapter(newsAdapter);

            
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

        //display_live_match();

    }
    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh()
    {

        // Fetching data from server
        call_news_ws();
        stop_SwipeRefreshLayout();
    }
    public void  stop_SwipeRefreshLayout()
    {
        if(srl_news_list!=null)
        {
            // Stopping swipe refresh
            srl_news_list.setRefreshing(false);
        }

    }

    public void  stop_wp7progressBar()
    {
        if(wp7progressBar!=null)
        {
            // Stopping wp7progressBar
            wp7progressBar.hideProgressBar();
            wp7progressBar.setVisibility(WP7ProgressBar.GONE);
        }

    }




    public void  display_live_match()
    {

        Log.e(TAG, "setOnDownloadListener rssFeed_list: " + rssFeed_list.size());
        if(rssFeed_list.size()<=0)
        {
            txtNoData.setVisibility(TextView.VISIBLE);
            newsAdapter.updateData(rssFeed_list);
        }
        else
        {
            txtNoData.setVisibility(TextView.GONE);
            newsAdapter.updateData(rssFeed_list);
        }


    }

    public void  call_news_ws()
    {


        if(ANL_Constant_Data.isConnectionAvailable(this))
        {
            txtNoData.setVisibility(TextView.GONE);

            wp7progressBar.setVisibility(WP7ProgressBar.VISIBLE);
            // for showing
            wp7progressBar.showProgressBar();

            String requestUrl = String.format(WebServicesURLs.Cricket_NEWS_URL_FULL,str_keyword,str_keyword,str_ned,str_gl,str_hl);
            Log.e("WebRequest_String requestUrl", " " + requestUrl);


            // Initialize a new StringRequest
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    requestUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            // Do something with response string


                            try {

                                if(result!=null)
                                {


                                    try
                                    {
                                        Log.e("WebRequest_String result", " " + result);
                                        rssFeed_list=new ArrayList<RssFeedModel>();


                                        if(!TextUtils.isEmpty(result))
                                        {

                                            InputStream inputStream = new ByteArrayInputStream(result.getBytes("UTF-8"));



                                            String title = null;
                                            String link = null;
                                            String description = null;
                                            String img_url="";
                                            String pubDate="";
                                            boolean isItem = false;
                                            boolean isImage = false;
                                            ArrayList<RssFeedModel> items = new ArrayList<RssFeedModel>();

                                            try {
                                                XmlPullParser xmlPullParser = Xml.newPullParser();
                                                xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                                xmlPullParser.setInput(inputStream, null);

                                                xmlPullParser.nextTag();
                                                while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                                                    int eventType = xmlPullParser.getEventType();

                                                    String name = xmlPullParser.getName();
                                                    if(name == null)
                                                        continue;


                                                    //To parse Image URL;
                                                    if(eventType == XmlPullParser.END_TAG) {
                                                        if(name.equalsIgnoreCase("image")) {
                                                            isImage = false;
                                                        }
                                                        continue;
                                                    }
                                                    if (eventType == XmlPullParser.START_TAG)
                                                    {
                                                        if(name.equalsIgnoreCase("image"))
                                                        {
                                                            isImage = true;
                                                            continue;
                                                        }

                                                    }


                                                    //To parse items
                                                    if(eventType == XmlPullParser.END_TAG) {
                                                        if(name.equalsIgnoreCase("item")) {
                                                            isItem = false;
                                                        }
                                                        continue;
                                                    }

                                                    if (eventType == XmlPullParser.START_TAG) {
                                                        if(name.equalsIgnoreCase("item")) {
                                                            isItem = true;
                                                            continue;
                                                        }
                                                    }

                                                    Log.d("MyXmlParser", "Parsing name ==> " + name);
                                                    String result_item = "";
                                                    if (xmlPullParser.next() == XmlPullParser.TEXT) {
                                                        result_item = xmlPullParser.getText();
                                                        xmlPullParser.nextTag();
                                                    }

                                                    if(name.equalsIgnoreCase("url"))
                                                    {
                                                        imageURL = result_item;
                                                    }

                                                    if (name.equalsIgnoreCase("title"))
                                                    {
                                                        title = result_item;
                                                    }
                                                    else if (name.equalsIgnoreCase("link"))
                                                    {
                                                        link = result_item;
                                                    }
                                                    else if (name.equalsIgnoreCase("description"))
                                                    {
                                                        description = result_item;


                                                    }
                                                    else if (name.equalsIgnoreCase("pubDate"))
                                                    {
                                                        pubDate = result_item;
                                                    }
                                                    else if(name.equalsIgnoreCase("media:content"))
                                                    {
                                                        //img_url="";
                                                        img_url = xmlPullParser.getAttributeValue(null, "url");
                                                        Log.e("imageURL",img_url);
                                                        //imageURL = result_item;
                                                    }

                                                    if (title != null && link != null && description != null)
                                                    {
                                                        if(isItem)
                                                        {
                                                            RssFeedModel item = new RssFeedModel(title, link, description,pubDate,img_url);
                                                            items.add(item);
                                                        }
                                                        else
                                                        {
                                                            mFeedTitle = title;
                                                            mFeedLink = link;
                                                            mFeedDescription = description;
                                                        }

                                                        title = null;
                                                        link = null;
                                                        description = null;
                                                        img_url=null;
                                                        pubDate=null;
                                                        isItem = false;
                                                    }
                                                }


                                            }
                                            finally
                                            {
                                                inputStream.close();
                                            }


                                            Log.e("RssFeedModel items",items.size()+"");
                                            if(items.size()>0)
                                            {
                                                for (int i = 0; i < items.size(); i++)
                                                {
                                                    RssFeedModel rssFeedModel=items.get(i);
                                                    Log.e("i ",i+"");
                                                    Log.e("title ",rssFeedModel.title+"");
                                                    Log.e("link ",rssFeedModel.link+"");
                                                    Log.e("pubDate ",rssFeedModel.pubDate+"");
                                                    Log.e("description ",rssFeedModel.description+"");
                                                    Log.e("img_url ",rssFeedModel.img_url+"");

                                                    Date date= ANL_Constant_Data.news_pubDate_format.parse(rssFeedModel.pubDate);
                                                    rssFeedModel.pubDate=  ANL_Constant_Data.news_pubDate_format_yyyy_mm_dd.format(date);
                                                    rssFeed_list.add(rssFeedModel);
                                                }


                                            }

                                            Log.e("Google imageURL",""+imageURL);
                                        }

                                        Collections.sort(rssFeed_list, new Comparator<RssFeedModel>(){
                                            public int compare(RssFeedModel obj1, RssFeedModel obj2) {
                                                // ## Ascending order
                                                return obj2.pubDate.compareToIgnoreCase(obj1.pubDate); // To compare string values
                                                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                                                // ## Descending order
                                                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                                            }
                                        });

                                        //for display power by google
                                        RssFeedModel rssFeedModel=null;
                                        rssFeed_list.add(rssFeedModel);
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e("RssFeedModel ",e.toString()+"");
                                    }
                                }


                            }
                            catch (Exception e)
                            {
                                Log.e("onRequestCompleted:", result.toString());
                            }


                            display_live_match();
                            stop_SwipeRefreshLayout();
                            stop_wp7progressBar();
                            getRequestQueue().cancelAll(ANL_Constant_Data.TAG_REQ_News);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something when get error
                            display_live_match();
                            stop_SwipeRefreshLayout();
                            stop_wp7progressBar();
                            getRequestQueue().cancelAll(ANL_Constant_Data.TAG_REQ_News);
                        }
                    }
            );


            // Adding request to request queue
            addToRequestQueue(stringRequest,ANL_Constant_Data.TAG_REQ_News);

        }
        else
        {
            txtNoData.setVisibility(TextView.VISIBLE);
            rssFeed_list=new ArrayList<RssFeedModel>();

            display_live_match();
            stop_SwipeRefreshLayout();
            stop_wp7progressBar();
            Toast.makeText(this,getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
        }


    }

    

    public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<RssFeedModel> rssFeedModelArrayList;
        Context context;
        int prevPos = -1;

        public NewsAdapter(ArrayList<RssFeedModel> liveMatches, Context context) {

            this.rssFeedModelArrayList = liveMatches;
            this.context = context;

        }

        public void updateData(ArrayList<RssFeedModel> liveMatches) {

            this.rssFeedModelArrayList = liveMatches;
            notifyDataSetChanged();

        }



        @Override
        public int getItemCount() {

            //return rssFeedModelArrayList.size();

            // for add power by google in end
            return rssFeedModelArrayList.size()+1;
        }

        @Override
        public int getItemViewType(int position)
        {
            if(position==rssFeedModelArrayList.size())
            {
                return 1;
            }
            else
                return 0;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            prevPos = position;

            switch (holder.getItemViewType())
            {


                case 0:
                    try
                    {
                        final NewsAdapter.NewsViewHolder holder1 = (NewsAdapter.NewsViewHolder) holder;

                        RssFeedModel rssFeedModel=rssFeedModelArrayList.get(position);

                        holder1.img_news.setTag(""+position);
                        holder1.txt_title.setTag(""+position);
                        holder1.txt_pubDate.setTag(""+position);

                        Date date= ANL_Constant_Data.news_pubDate_format_yyyy_mm_dd.parse(rssFeedModel.pubDate);
                        rssFeedModel.pubDate=  ANL_Constant_Data.news_pubDate_format_display.format(date);

                        holder1.txt_title.setText(rssFeedModel.title);
                        holder1.txt_pubDate.setText(rssFeedModel.pubDate);

                        try {

                            if(TextUtils.isEmpty(rssFeedModel.img_url))
                            {
                                //holder1.img_news.setImageResource(R.drawable.icon_200x200);

                                holder1.img_news.setImageResource(R.mipmap.news);
                                holder1.img_news.setColorFilter(ANL_Constant_Data.colorCode, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                //holder1.img_news.setImage
                            }
                            else
                            {
                                holder1.img_news.setColorFilter(ContextCompat.getColor(context, R.color.fully_transparent_color), android.graphics.PorterDuff.Mode.SRC_ATOP);

                                Log.e("onBindViewHolder rssFeedModel.img_url ",""+rssFeedModel.img_url);
                                Picasso.get()
                                        .load(rssFeedModel.img_url)
                                        .error(R.mipmap.arrow)
                                        .into(holder1.img_news);
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e("onBindViewHolder Picasso ",""+e.toString());
                        }






                        holder1.itemView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if(position>=0)
                                {
                                    if (rssFeedModelArrayList.get(position).title != null && rssFeedModelArrayList.get(position).title.length() > 0)
                                    {
                                        try {

                                            String str_title=rssFeedModelArrayList.get(position).title;
                                            String str_link=rssFeedModelArrayList.get(position).link;
                                            Bundle bundle_data=new Bundle();
                                            bundle_data.putString("news_url",str_link);
                                            bundle_data.putString("news_title",str_title);
                                            Intent marketIntent = new Intent(context,News_Details_Activity.class);
                                            marketIntent.putExtras(bundle_data);
                                            startActivity(marketIntent);

//                                            Uri marketUri = Uri.parse(str_link );
//                                            Intent marketIntent = new Intent( Intent.ACTION_VIEW ).setData( marketUri );
//                                            this.startActivity( marketIntent );


                                        } catch (Exception e) {
                                            // TODO: handle exception

                                        }
                                    }
                                }
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        Log.e("onBindViewHolder",""+e.toString());

                    }
                    break;

                case 1:


                    break;


            }

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = null;
            if(viewType==0)
            {
                if(rssFeedModelArrayList.get(viewType).title!=null && rssFeedModelArrayList.get(viewType).title.length()>0) {
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.row_news_item, parent, false);
                }
            }
            else if(viewType==1)
            {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listview_footer, parent, false);
                return new NewsAdapter.MyFooterViewHolder(itemView);
            }

            return new NewsAdapter.NewsViewHolder(itemView);
        }

        public class NewsViewHolder extends RecyclerView.ViewHolder {

            TextView txt_title,txt_pubDate;
            ImageView img_news;
            View itemView;

            public NewsViewHolder(View itemView) {

                super(itemView);
                this.itemView = itemView;
                txt_title = (TextView) itemView.findViewById(R.id.txt_title);
                txt_pubDate  = (TextView) itemView.findViewById(R.id.txt_pubDate);
                img_news = (ImageView) itemView.findViewById(R.id.img_news);


            }
        }
        public class MyFooterViewHolder extends RecyclerView.ViewHolder
        {
            private ImageView powered_by_google;


            public MyFooterViewHolder(View view)
            {
                super(view);

                powered_by_google=(ImageView)view.findViewById(R.id.powered_by_google);

            }
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(this);
        }

        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag)
    {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}




