package com.example.administrator.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.SimpleAdapter;
//=============================================================//
// NAME : RSSFeedReceiver
// PURPOSE : This Class used by NewsActivity to receive the RSS
//           broadcast from RSSPullService
//============================================================//
public class RssFeedReceiver extends BroadcastReceiver {
    NewsActivity refActivity;
    public RssFeedReceiver(NewsActivity aActivity) {
        this.refActivity = aActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        RSSFeed feed = (RSSFeed)intent.getParcelableExtra(RSSPullService.EXTENDED_DATA_STATUS);
        refActivity.BroadcastCallback(feed);
    }
}
