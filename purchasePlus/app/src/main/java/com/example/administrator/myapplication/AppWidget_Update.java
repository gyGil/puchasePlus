package com.example.administrator.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

//=========================================================================//
// NAME : AppWidget_Update
// PURPOSE : This Class implement application widget about receipt due date
//========================================================================//
/**
 * Implementation of App Widget functionality.
 */
public class AppWidget_Update extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget__update);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ItemInfoListDB db = new ItemInfoListDB(context);
        Cursor cur = db.getItemInfoCursor("MyPurchase");
        int receiptNumber = 0;
        int dueNumber = 0; //3 days
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //Date mydate = sdf.parse(datestring);
        Calendar c = Calendar.getInstance();
        //Date current = c.getTime();
        if(cur.moveToFirst()) {
            do {
                String dueDate = cur.getString(ItemInfoListDB.ITEMINFO_REFUND_DUEDATE_COL);
                try {
                    Date mydate = sdf.parse(dueDate);
                    Calendar c2 = Calendar.getInstance();
                    //Change to Calendar Date
                    c2.setTime(mydate);

                    //get Time in milli seconds
                    long ms1 = c2.getTimeInMillis();
                    long ms2 = c.getTimeInMillis();
                    //get difference in milli seconds
                    long diff = ms2 - ms1;
                    if (diff < 3 * 24 * 3600)
                        dueNumber++;

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //al.add(content);
                receiptNumber++;
            } while (cur.moveToNext());
        }
        String status_str = "Purchase Plus State\n\nTotal receipts: "+Integer.toString(receiptNumber) + "\nCloseDue receipts(3days):"
                + Integer.toString(dueNumber);
        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                AppWidget_Update.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.app_widget__update);

            remoteViews.setTextViewText(R.id.appwidget_text, status_str);

            // Register an onClickListener
            Intent intent = new Intent(context, ReceiptListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            remoteViews.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

