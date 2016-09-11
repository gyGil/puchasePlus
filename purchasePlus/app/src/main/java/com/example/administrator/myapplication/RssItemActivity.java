package com.example.administrator.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

//================================================//
// NAME : RssItemActivity
// PURPOSE : This Class display a item info from ReceiptList Activity
//================================================//
public class RssItemActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_item);

        // Get references to widgets
        TextView tvRssItemTitle = (TextView) findViewById(R.id.tvRssItemTitle);
        TextView tvRssItemPubDate = (TextView) findViewById(R.id.tvRssItemPubDate);
        TextView tvRssItemDescription = (TextView) findViewById(R.id.tvRssItemDescription);
        TextView tvRssItemLink = (TextView) findViewById(R.id.tvRssItemLink);

        // Get the intent
        Intent intent = getIntent();

        // get data from the intent
        String pubDate = intent.getStringExtra("pubdate");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description").replace('\n',' ');

        // Display data on the widgets
        tvRssItemPubDate.setText(pubDate);
        tvRssItemTitle.setText(title);
        tvRssItemDescription.setText(description);

        // Set listener
        tvRssItemLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        Intent intent = getIntent();

        // Get the URI for the link
        String link = intent.getStringExtra("link");
        Uri viewUri = Uri.parse(link);

        // Create the intent and start it
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, viewUri);
        startActivity(viewIntent);
    }

}
