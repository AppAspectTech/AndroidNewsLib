package com.appaspect.news;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appaspect.news.androidnewslibrary.News_Activity;
import com.appaspect.news.androidnewslibrary.News_Details_Activity;
import com.appaspect.news.androidnewslibrary.News_Fragment;
import com.appaspect.news.androidnewslibrary.utils.ANL_Constant_Data;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            String str_tag = "News_Fragment";
            Bundle bundle_news=new Bundle();
            bundle_news.putString(ANL_Constant_Data.News_keyword,"Cricket");
            bundle_news.putString(ANL_Constant_Data.News_Header_BG_Color,"#000000"); // set Header Background Color
            bundle_news.putBoolean(ANL_Constant_Data.Header_Show,true);    // set Header show boolean
            bundle_news.putString(ANL_Constant_Data.News_Header_Text_Color,"#FFFFFF");    // set Header Text Color

            News_Fragment news_fragment=new News_Fragment();
            news_fragment.setArguments(bundle_news);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, news_fragment, str_tag).commit();



        } catch (Exception e)
        {

        }


//                try {
//
//            String str_tag = "News_Fragment";
//        String str_addToBackStack = "News_Fragment";;
//            Bundle bundle_news=new Bundle();
//            bundle_news.putString(ANL_Constant_Data.News_keyword,"Cricket");
//            bundle_news.putString(ANL_Constant_Data.News_Header_BG_Color,"#000000");
//            bundle_news.putBoolean(ANL_Constant_Data.Header_Show,true);    // set Header show boolean
//            bundle_news.putString(ANL_Constant_Data.News_Header_Text_Color,"#FFFFFF");    // set Header Text Color
//
//            News_Fragment news_fragment=new News_Fragment();
//            news_fragment.setArguments(bundle_news);
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.container, news_fragment, str_tag).addToBackStack(str_addToBackStack).commit();
//
//
//
//        } catch (Exception e)
//        {
//
//        }



//        try {
//
//            Bundle bundle_news=new Bundle();
//            bundle_news.putString(ANL_Constant_Data.News_keyword,"Cricket");
//            bundle_news.putString(ANL_Constant_Data.News_Header_BG_Color,"#FF0000");
//            bundle_news.putBoolean(ANL_Constant_Data.Header_Show,true);    // set Header show boolean
//            bundle_news.putString(ANL_Constant_Data.News_Header_Text_Color,"#000000");    // set Header Text Color
//
//            Intent intent_news=new Intent(this,News_Activity.class);
//            intent_news.putExtras(bundle_news);
//             startActivity(intent_news);
//
//
//        } catch (Exception e)
//        {
//
//        }



    }
}
