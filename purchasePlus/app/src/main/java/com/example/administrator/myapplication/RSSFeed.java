package com.example.administrator.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

//================================================//
// NAME : RSSFeed
// PURPOSE : This Class make list of articles (RSSItem)
//================================================//
@SuppressLint("SimpleDateFormat")
public class RSSFeed  implements Parcelable {
    private String title = null;
    private String pubDate = null;
    private ArrayList<RSSItem> items;

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    public RSSFeed() {
        items = new ArrayList<RSSItem>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    public long getPubDateMillis() {
        try {
            Date date = dateInFormat.parse(pubDate.trim());
            return date.getTime();
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int addItem(RSSItem item) {
        items.add(item);
        return items.size();
    }

    public RSSItem getItem(int index) {
        return items.get(index);
    }

    public ArrayList<RSSItem> getAllItems() {
        return items;
    }

    // Parcelling part
    public RSSFeed(Parcel in){
        //String[] data = new String[2];
        //in.readStringArray(data);
        this.title = in.readString();
        this.pubDate = in.readString();
        items = new ArrayList<RSSItem>();
        in.readTypedList(items,RSSItem.CREATOR);//class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(pubDate);
        dest.writeTypedList(items);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RSSFeed createFromParcel(Parcel in) {
            return new RSSFeed(in);
        }

        public RSSFeed[] newArray(int size) {
            return new RSSFeed[size];
        }
    };
}