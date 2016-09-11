package com.example.administrator.myapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.sql.SQLException;
//===================================================================//
// NAME : dbContentProvider
// PURPOSE : This Class expose SQLite db content as content provider
//           So other application can access if needed
//===================================================================//
public class dbContentProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.example.administrator.myapplication";
    static final String URL = "content://" + PROVIDER_NAME + "/itemInfo";
    static final Uri CONTENT_URI = Uri.parse(URL);

    public dbContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //ItemInfoListDB db = new ItemInfoListDB(getContext());
        int rowDeleted = 0;
        if (selection.equals("ID")) {
            for (String idStr : selectionArgs) {
                int id = Integer.parseInt(idStr);
                db.deleteItem(id);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    @Override
    public String getType(Uri uri) {

        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //ItemInfoListDB db = new ItemInfoListDB(getContext());
        ItemInfo newItem = new ItemInfo();
        newItem.setItemId(values.getAsLong("itemId"));
        newItem.setListId(values.getAsLong("listId"));
        newItem.setItemType(values.getAsString("itemType"));
        newItem.setItemName(values.getAsString("itemName"));
        newItem.setStoreName(values.getAsString("storeName"));
        newItem.setPurchaseAmount(values.getAsDouble("purchaseAmount"));
        newItem.setPurchaseDate(values.getAsString("purchaseDate"));
        newItem.setRefundDueDate(values.getAsString("refundDueDate"));
        long rowID = db.insertItemInfo(newItem);
        /**
         * If record is added successfully
         */
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new UnsupportedOperationException("insert has issue");
    }

    private ItemInfoListDB db;
    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        //return false;
        db = new ItemInfoListDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        if (selection.equals("listName")) {
            Cursor cursor = db.getItemInfoCursor(selectionArgs[0]);

            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
            return cursor;
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        ItemInfoListDB db = new ItemInfoListDB(getContext());
        ItemInfo itemInfo = new ItemInfo();
        if (selection.equals("ID")) {
            int rowID = Integer.parseInt(selectionArgs[0]);
            itemInfo.setItemId(rowID);
        }

        itemInfo.setListId(values.getAsLong("listId"));
        itemInfo.setItemType(values.getAsString("itemType"));
        itemInfo.setItemName(values.getAsString("itemName"));
        itemInfo.setStoreName(values.getAsString("storeName"));
        itemInfo.setPurchaseAmount(values.getAsDouble("purchaseAmount"));
        itemInfo.setPurchaseDate(values.getAsString("purchaseDate"));
        itemInfo.setRefundDueDate(values.getAsString("refundDueDate"));

        int rowNum = db.updateItemInfo(itemInfo);
        // notify all listeners of changes:
        if (rowNum > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowNum;
    }
}
