package com.example.administrator.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//================================================//
// NAME : NewsActivity
// PURPOSE : Get the RSS info and save to file and
//           get read data from file. (behind threads)
//          display RSS to window
//================================================//
public class NewsActivity extends Activity
                    implements AdapterView.OnItemClickListener{

    // CONSTANTS
    final String NEWS_FILENAME = "shopping_news_feed.xml";

    // MEMBERS
    RSSFeed feed = null;
    TextView tvRSSTitle = null;
    ListView lvNews = null;
    Button btnFeedRss = null;

    //this method supposed to be called by BroadcastReceiver
    public void BroadcastCallback(final RSSFeed aFeed) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                NewsActivity.this.feed = aFeed;
                NewsActivity.this.updateDisplay();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // link with resource
        btnFeedRss = (Button)findViewById(R.id.btnFeedRss);
        tvRSSTitle = (TextView)findViewById(R.id.tvRSSTitle);
        lvNews = (ListView) findViewById(R.id.lvNews);
        lvNews.setOnItemClickListener(this);

//
//        btnFeedRss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                new DownloadFeed().execute(new String[]{"http://rss.cnn.com/rss/edition.rss"});
//            }
//        });

        //Start the RSS feed gathering service
        Intent serviceIntent = new Intent(this, RSSPullService.class);
        startService(serviceIntent);



        //create receiver

        //http://stackoverflow.com/questions/14643385/how-to-update-ui-in-a-broadcastreceiver

        // Instantiates a new DownloadStateReceiver
        RssFeedReceiver mDownloadStateReceiver =
                new RssFeedReceiver(this);

        // Registers the DownloadStateReceiver and its intent filters
        this.registerReceiver(
                mDownloadStateReceiver,
                new IntentFilter(RSSPullService.BROADCAST_ACTION));

        RSSPullService.startActionRSSRead(this);


    }



    // Download the information form internet and store to local file
    class DownloadFeed extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params){

            try{
                // Allocate resources to get read the data from url
                URL url = new URL(params[0]);
                InputStream in = url.openStream();
                Context context = NewsActivity.this;
                FileOutputStream out = context.openFileOutput(NEWS_FILENAME, Context.MODE_PRIVATE);

                // read data from url and write to file
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                while (bytesRead != -1){
                    out.write(buffer, 0, bytesRead);
                    bytesRead = in.read(buffer);
                }
                out.close();
                in.close();

            }catch (IOException ioe){
                Log.e("Error: ", ioe.toString());
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.d("News reader", "Feed download: " + new Date());
            new ReadFeed().execute();
        }
    }

    class ReadFeed extends AsyncTask<Void, Void, RSSFeed>{
        @Override
        protected RSSFeed doInBackground(Void... params){
            try{
                // Get the XML reader
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader xmlReader = parser.getXMLReader();

                // Set content handler
                RSSHandler theRssHandler = new RSSHandler();
                xmlReader.setContentHandler(theRssHandler);

                // Read the file from internal storage
                FileInputStream ins = openFileInput(NEWS_FILENAME);

                //Parse the date
                InputSource is = new InputSource(ins);
                xmlReader.parse(is);

                // Return the feed
                RSSFeed feed = theRssHandler.getFeed();
                return feed;
            }catch (Exception ex){
                Log.e("Error: ", ex.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(RSSFeed result){
            Log.d("News reader", "Feed read: " + new Date());

            // Update the display for the activity
            NewsActivity.this.feed = result;
            NewsActivity.this.updateDisplay();
        }
    }

    // Display RSS to page
    public void updateDisplay(){
        // fail to read RSS
        if(feed == null){
            this.tvRSSTitle.setText("Unable to get RSS feed");
            return;
        }

        // Set the title for the feed
        this.tvRSSTitle.setText(feed.getTitle());

        // Get the items for the feed
        ArrayList<RSSItem> items = this.feed.getAllItems();

        // Create a list of Map<String, ?> objects
        ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
        for (RSSItem item: items){
            HashMap<String, String> map = new HashMap<String, String>();
            //map.put("data", item.getFormattedPubDate());
            map.put("data",item.getPubDateFormatted());
            map.put("title", item.getTitle());
            data.add(map);
        }

        // Create the resource, from, and to variables
        int resource = R.layout.rss_listitem;
        String[] from = {"data", "title"};
        int[] to = {R.id.tvPubDate,R.id.tvTitle};

        // Create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        this.lvNews.setAdapter(adapter);

        Log.d("News reader", "Feed displayed: " + new Date());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    {
        RSSItem item = this.feed.getItem(position);

        // create an intent
        Intent intent = new Intent(this, RssItemActivity.class);

        intent.putExtra("pubdate",item.getPubDate());
        intent.putExtra("title",item.getTitle());
        intent.putExtra("description",item.getDescription());
        intent.putExtra("link",item.getLink());

        this.startActivity(intent);
    }

    // get menu button linked with content in main_menu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        try {
            getMenuInflater().inflate(R.menu.main_menu,menu);
        }catch (Exception e)
        {
            Log.e("Exception: ",e.getMessage());
        }
        return true;
    }

    // define the behaves when user clicks the item on menu
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        try{
            switch (item.getItemId()){
                case R.id.menu_login_activity:
                    Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),LogInActivity.class)); // move to clicked activity page
                    return true;
                case R.id.menu_receiptlist_activity:
                    Toast.makeText(this,"Listing by descending date", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ReceiptListActivity.class));
                    return true;
                case R.id.menu_receipt_entry_activity:
                    Toast.makeText(this,"Moving to entry page", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ReceiptEntryActivity.class));
                    return true;
                case R.id.menu_setting_activity:
                    Toast.makeText(this,"Moving to setting page", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                    return true;
                case R.id.menu_rss_activity:
                    Toast.makeText(this,"Moving to news page", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),NewsActivity.class));
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
            return super.onOptionsItemSelected(item);
        }
    }

}
