package com.example.administrator.myapplication;

import java.util.ArrayList;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//================================================//
// NAME : ItemInfoListDB
// PURPOSE:
// Class work with database
// create, drop table
// insert, update data to table
//================================================//
public class ItemInfoListDB {

    //====================================[ CONSTANTS ]====================================//

    // database constants
    public static final String DB_NAME = "itemInfoList.db";
    public static final int DB_VERSION = 1;

    // list table constants
    public static final String LIST_TABLE = "list";

    public static final String LIST_ID = "_id";
    public static final int LIST_ID_COL = 0;

    public static final String LIST_NAME = "list_name";
    public static final int LIST_NAME_COL = 1;

    // itemInfo table constants
    public static final String ITEMINFO_TABLE = "itemInfo";

    public static final String ITEMINFO_ID = "_id";
    public static final int ITEMINFO_ID_COL = 0;

    public static final String ITEMINFO_LIST_ID = "list_id";
    public static final int ITEMINFO_LIST_ID_COL = 1;

    public static final String ITEMINFO_ITEM_TYPE = "item_type";
    public static final int ITEMINFO_ITEM_TYPE_COL = 2;

    public static final String ITEMINFO_ITEM_NAME = "item_name";
    public static final int ITEMINFO_ITEM_NAME_COL = 3;

    public static final String ITEMINFO_STORE_NAME = "store_name";
    public static final int ITEMINFO_STORE_NAME_COL = 4;

    public static final String ITEMINFO_PURCHASE_AMOUNT = "purchase_name";
    public static final int ITEMINFO_PURCHASE_AMOUNT_COL = 5;

    public static final String ITEMINFO_PURCHASE_DATE = "purchase_date";
    public static final int ITEMINFO_PURCHASE_DATE_COL = 6;

    public static final String ITEMINFO_REFUND_DUEDATE = "refund_duedate";
    public static final int ITEMINFO_REFUND_DUEDATE_COL = 7;

    public static final String ITEMINFO_HIDDEN = "hidden";
    public static final int ITEMINFO_HIDDEN_COL = 8;



    // CREATE and DROP TABLE statements
    public static final String CREATE_LIST_TABLE =
                    "CREATE TABLE " + LIST_TABLE + " (" +
                    LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LIST_NAME + " TEXT NOT NULL UNIQUE);";

    public static final String CREATE_ITEMINFO_TABLE =
                    "CREATE TABLE " + ITEMINFO_TABLE + " (" +
                    ITEMINFO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ITEMINFO_LIST_ID + " INTEGER NOT NULL, " +
                    ITEMINFO_ITEM_TYPE + " TEXT, " +
                    ITEMINFO_ITEM_NAME + " TEXT NOT NULL, " +
                    ITEMINFO_STORE_NAME + " TEXT, " +
                    ITEMINFO_PURCHASE_AMOUNT + " REAL, " +
                    ITEMINFO_PURCHASE_DATE + " TEXT, " +
                    ITEMINFO_REFUND_DUEDATE + " TEXT, " +
                    ITEMINFO_HIDDEN + " INTEGER);";
    public static final String DROP_LIST_TABLE =
            "DROP TABLE IF EXISTS " + LIST_TABLE;

    public static final String DROP_ITEMINFO_TABLE =
            "DROP TABLE IF EXISTS " + ITEMINFO_TABLE;

    private  static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, CursorFactory factory, int version){
            super(context,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            // Create tables
            db.execSQL(CREATE_LIST_TABLE);
            db.execSQL(CREATE_ITEMINFO_TABLE);

            // Insert default lists
            db.execSQL("INSERT INTO " + LIST_TABLE + " VALUES (1, 'MyPurchase')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.d("ItemInfo List", "Upgrading db from version "
            + oldVersion + "to" + newVersion);

            db.execSQL(ItemInfoListDB.DROP_LIST_TABLE);
            db.execSQL(ItemInfoListDB.DROP_ITEMINFO_TABLE);
            onCreate(db);
        }
    }

    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    //====================================[ METHODS ]====================================//

    // Constructor
    public ItemInfoListDB(Context context){
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // private methods
    private void openReadableDB() { db = dbHelper.getReadableDatabase(); }

    private void openWriteableDB() { db = dbHelper.getWritableDatabase(); }

    private void closeDB(){
        if(db != null)
            db.close();
    }

    private void closeCursor(Cursor cursor){
        if(cursor != null)
            cursor.close();
    }

    // public methods
    public ArrayList<List> getLists() {
        ArrayList<List> lists = new ArrayList<List>();
        openReadableDB();
        Cursor cursor = db.query(LIST_TABLE, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            List list = new List();
            list.setId(cursor.getInt(LIST_ID_COL));
            list.setName(cursor.getString(LIST_NAME_COL));

            lists.add(list);
        }
        closeCursor(cursor);
        closeDB();

        return lists;
    }

    // get list
    public  List getList(String name){
        String where = LIST_NAME + " = ?";
        String[] whereArgs = { name };

        openReadableDB();
        Cursor cursor = db.query(LIST_TABLE, null,
                where, whereArgs, null, null, null);
        List list = null;
        cursor.moveToFirst();
        list = new List(cursor.getInt(LIST_ID_COL),
                        cursor.getString(LIST_NAME_COL));
        this.closeCursor(cursor);
        this.closeDB();

        return list;
    }

    // Get all info for item choosen by duedate, itemname, storename
    public ItemInfo getItemInfo(String dueDate, String itemName, String storeName){
        String where =  ITEMINFO_REFUND_DUEDATE + " = ? AND " +
                        ITEMINFO_ITEM_NAME + " = ? AND " +
                        ITEMINFO_STORE_NAME + " = ? AND " +
                        ITEMINFO_HIDDEN + "!= 1";
        String[] whereArgs = { dueDate, itemName, storeName};

        this.openReadableDB();
        Cursor cursor = db.query(ITEMINFO_TABLE, null, where, whereArgs,
                null, null, null);
        cursor.moveToFirst();
        ItemInfo itemInfo = getItemInfoFromCursor(cursor);
        this.closeCursor(cursor);
        this.closeDB();

        return itemInfo;
    }


    // Get the cursor for specific list
    public Cursor getItemInfoCursor(String listName){
       String where = ITEMINFO_LIST_ID + " = ? AND " +
               ITEMINFO_HIDDEN + "!= 1";
        int listID = getList(listName).getId();
        String[] whereArgs = { Integer.toString(listID) };

        this.openReadableDB();
        Cursor cursor = db.query(ITEMINFO_TABLE, null, where, whereArgs,
                                null,null,null);
        return cursor;
    }

    // get the cursor
    private static ItemInfo getItemInfoFromCursor(Cursor cursor) {
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        else {
            try {
                ItemInfo itemInfo = new ItemInfo(
                        cursor.getInt(ITEMINFO_ID_COL),
                        cursor.getInt(ITEMINFO_LIST_ID_COL),
                        cursor.getString(ITEMINFO_ITEM_TYPE_COL),
                        cursor.getString(ITEMINFO_ITEM_NAME_COL),
                        cursor.getString(ITEMINFO_STORE_NAME_COL),
                        cursor.getDouble(ITEMINFO_PURCHASE_AMOUNT_COL),
                        cursor.getString(ITEMINFO_PURCHASE_DATE_COL),
                        cursor.getString(ITEMINFO_REFUND_DUEDATE_COL),
                        cursor.getInt(ITEMINFO_HIDDEN_COL));
                return  itemInfo;
            }
            catch (Exception e){
                return null;
            }
        }
    }

    // get items in list
    public ArrayList<ItemInfo> getItemInfos(String listName){
        String where = ITEMINFO_LIST_ID + " = ? AND " +
                        ITEMINFO_HIDDEN + " != 1";
        int listID = getList(listName).getId();
        String[] whereArgs = { Integer.toString(listID)};

        this.openReadableDB();
        Cursor cursor = db.query(ITEMINFO_TABLE, null, where, whereArgs,
                                null, null, null);
        ArrayList<ItemInfo> ItemInfos = new ArrayList<ItemInfo>();
        while (cursor.moveToNext()){
            ItemInfos.add(getItemInfoFromCursor(cursor));
        }
        this.closeCursor(cursor);
        this.closeDB();

        return ItemInfos;
    }

    // get item with specific id
    public ItemInfo getItemInfo(int id) {
        String where = ITEMINFO_ID + " = ?";
        String[] whereArgs = { Integer.toString(id)};

        this.openReadableDB();
        Cursor cursor = db.query(ITEMINFO_TABLE, null, where, whereArgs,
                                null, null, null);
        cursor.moveToFirst();
        ItemInfo itemInfo = getItemInfoFromCursor(cursor);
        this.closeCursor(cursor);
        this.closeDB();

        return itemInfo;
    }

    public long insertItemInfo(ItemInfo itemInfo){
        ContentValues cv = new ContentValues();
        cv.put(ITEMINFO_LIST_ID, itemInfo.getListId());
        cv.put(ITEMINFO_ITEM_TYPE, itemInfo.getItemType());
        cv.put(ITEMINFO_ITEM_NAME, itemInfo.getItemName());
        cv.put(ITEMINFO_STORE_NAME,itemInfo.getStoreName());
        cv.put(ITEMINFO_PURCHASE_AMOUNT,itemInfo.getPurchaseAmount());
        cv.put(ITEMINFO_PURCHASE_DATE, itemInfo.getPurchaseDate());
        cv.put(ITEMINFO_REFUND_DUEDATE, itemInfo.getRefundDueDate());
        cv.put(ITEMINFO_HIDDEN, itemInfo.getHidden());

        this.openWriteableDB();
        long rowID = db.insert(ITEMINFO_TABLE,null,cv);
        this.closeDB();

        return rowID;
    }

    public  int updateItemInfo(ItemInfo itemInfo){
        ContentValues cv = new ContentValues();
        cv.put(ITEMINFO_LIST_ID, itemInfo.getListId());
        cv.put(ITEMINFO_ITEM_TYPE, itemInfo.getItemType());
        cv.put(ITEMINFO_ITEM_NAME, itemInfo.getItemName());
        cv.put(ITEMINFO_STORE_NAME,itemInfo.getStoreName());
        cv.put(ITEMINFO_PURCHASE_AMOUNT,itemInfo.getPurchaseAmount());
        cv.put(ITEMINFO_PURCHASE_DATE, itemInfo.getPurchaseDate());
        cv.put(ITEMINFO_REFUND_DUEDATE, itemInfo.getRefundDueDate());
        cv.put(ITEMINFO_HIDDEN, itemInfo.getHidden());

        String where = ITEMINFO_ID + " = ?";
        String[] whereArgs = {String.valueOf(itemInfo.getItemId())};

        this.openWriteableDB();
        int rowCount = db.update(ITEMINFO_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    public int deleteItem(long id){
        String where = ITEMINFO_ID + " = ?";
        String[] whereArgs = { String.valueOf(id)};

        this.openWriteableDB();
        int rowCount = db.delete(ITEMINFO_TABLE,where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    // insert the sample data into database
    public void createSampleData(){
        int i = 0;
        ItemInfo itemInfo = new ItemInfo(1, ItemInfo.TYPE_APPLIANCE,"Toaster",
                "PHILIPS", 20.54, "2016-01-01",
                "2016-01-31",ItemInfo.FALSE);

        long insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_APPLIANCE,"Refrigerator",
                "LG", 1020.99, "2016-01-21",
                "2016-01-31",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_APPLIANCE,"Oven",
                "PHILIPS", 510.00, "2016-02-02",
                "2016-03-05",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_CLOTHES,"Shirt",
                "Louis Bitton", 76.10, "2016-03-02",
                "2016-03-15",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_CLOTHES,"Underware",
                "FILA", 30.00, "2016-03-02",
                "2016-03-15",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_CLOTHES,"Pant",
                "Revise", 71.20, "2016-03-02",
                "2016-03-15",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_CLOTHES,"Pant",
                "Revise", 71.20, "2016-03-02",
                "2016-03-15",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_ELECTORONICS,"Printer",
                "HP", 120.77, "2016-05-02",
                "2016-09-15",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_ELECTORONICS,"Moniter",
                "LG", 110.61, "2016-05-02",
                "2016-09-15",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_ELECTORONICS,"Television",
                "SONY", 980.70, "2016-03-02",
                "2016-06-15",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_OTHERS,"Banana",
                "WALMART", 1.70, "2016-04-01",
                "2016-04-01",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_OTHERS,"Books",
                "AMAZON", 120.70, "2016-06-02",
                "2016-06-15",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);

        itemInfo = new ItemInfo(1, ItemInfo.TYPE_OTHERS,"Tickets",
                "LENDMARK CINEMAS", 20.10, "2016-04-02",
                "2016-04-02",ItemInfo.FALSE);

        insertId = this.insertItemInfo(itemInfo);
    }
}
