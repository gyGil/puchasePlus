package com.example.administrator.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//========================================================================//
// NAME : RSSPullService
// PURPOSE : This Class run as the application background  service to get
//           RSSFeed from website and send notification as broadcast
//=======================================================================//
public class RSSPullService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_RSSRead = "com.example.administrator.myapplication.action.RSSRead";
    private static final String ACTION_FOO = "com.example.administrator.myapplication.action.FOO";
    private static final String ACTION_BAZ = "com.example.administrator.myapplication.action.BAZ";
    public static final String BROADCAST_ACTION =
            "com.example.administrator.myapplication.action.RSSBROADCAST";
    public static final String EXTENDED_DATA_STATUS =
            "com.example.administrator.myapplication.STATUS";


    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.administrator.myapplication.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.administrator.myapplication.extra.PARAM2";


    public RSSPullService() {
        super("RSSPullService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, RSSPullService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionRSSRead(Context context) {
        Intent intent = new Intent(context, RSSPullService.class);
        intent.setAction(ACTION_RSSRead);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, RSSPullService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
            else if (ACTION_RSSRead.equals(action)) {
                handleActionRSSFetch();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRSSFetch() {
        final String NEWS_FILENAME = "shopping_news_feed.xml";
        try{
            // Allocate resources to get read the data from url
            URL url = new URL("http://rss.cnn.com/rss/edition.rss");
            InputStream in = url.openStream();
            //Context context = NewsActivity.this;
            FileOutputStream out = this.openFileOutput(NEWS_FILENAME, Context.MODE_PRIVATE);

            // read data from url and write to file
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1){
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            out.close();
            in.close();

        }catch (Exception ioe){
            Log.e("Error: ", ioe.toString());
            try {
                throw ioe;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

            Intent localIntent = new Intent(BROADCAST_ACTION)
                            .putExtra(EXTENDED_DATA_STATUS, feed);
            this.sendBroadcast(localIntent);

        }catch (Exception ex){
            Log.e("Error: ", ex.toString());
            //return null;
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
