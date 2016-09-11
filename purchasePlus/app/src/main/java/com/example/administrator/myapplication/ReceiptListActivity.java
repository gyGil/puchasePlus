/*
* FILE : ReceiptListActivity.java
* PROJECT : PROG3150 - Assignment #1
* PROGRAMMER : LingChen Meng(Walter) / Xuan Zhang / Marcus Rankin / GeunYoung Gil
* FIRST VERSION : 2016-02-07
* DESCRIPTION : This file is used to handle ReceiptListActivity.
*/
package com.example.administrator.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//================================================//
// NAME : ReceiptListActivity
// PURPOSE : This Class sorts the receipts in the database
//           and shows the list in list view item
//================================================//
public class ReceiptListActivity extends Activity
                                        implements AdapterView.OnItemClickListener {

    private Button btnOrderbyDate = null;
    private Button btnSaveToCloud = null;
    private Button btnReadFromCloud = null;
    private boolean isOrderDesc = false;
    private Activity myActivity = null;
    private ListView lvReceipts = null;
    ArrayList<String> al;
    ListView myList;
    ArrayAdapter<String> adapter;
    ItemInfoListDB db;

    private static final int CREATE_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;


    class TmpReceiptItem {
        String TheDate;
        String TheName;
        String TheStore;

        String ItemType;
        String DueDate;
    }
    //for cloud saving
    private String getAllReceiptAsString() {
        StringBuilder sb = new StringBuilder();

        ArrayList<TmpReceiptItem> tmpList = new ArrayList<TmpReceiptItem>();
        //al = new ArrayList<>();
        db = new ItemInfoListDB(this);

        // Get data from database
        Cursor cur = db.getItemInfoCursor("MyPurchase");
        if(cur.moveToFirst()) {
            do{
                TmpReceiptItem newItem = new TmpReceiptItem();
                newItem.TheDate = cur.getString(ItemInfoListDB.ITEMINFO_REFUND_DUEDATE_COL);
                newItem.TheName =  cur.getString(ItemInfoListDB.ITEMINFO_ITEM_NAME_COL);
                newItem.TheStore = cur.getString(ItemInfoListDB.ITEMINFO_STORE_NAME_COL);

                newItem.ItemType = cur.getString(ItemInfoListDB.ITEMINFO_ITEM_TYPE_COL);
                newItem.DueDate = cur.getString(ItemInfoListDB.ITEMINFO_REFUND_DUEDATE_COL);
                tmpList.add(newItem);
            }while(cur.moveToNext());

            for (TmpReceiptItem item : tmpList) {
                sb.append("[");
                sb.append(item.TheDate);
                sb.append(" ");
                sb.append(item.TheName);
                sb.append(" ");
                sb.append("-");
                sb.append(item.TheStore);
                sb.append("]");
                sb.append(System.getProperty("line.separator"));
                sb.append("Item name: ");
                sb.append(item.TheName);
                sb.append(System.getProperty("line.separator"));

                sb.append("Item type: ");
                sb.append(item.ItemType);
                sb.append(System.getProperty("line.separator"));

                sb.append("Store name: ");
                sb.append(item.TheStore);
                sb.append(System.getProperty("line.separator"));

                sb.append("Purchase date: ");
                sb.append(item.TheDate);
                sb.append(System.getProperty("line.separator"));

                sb.append("Due date: ");
                sb.append(item.DueDate);
                sb.append(System.getProperty("line.separator"));
                sb.append(System.getProperty("line.separator"));

            }
        }
        return sb.toString();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ReceiptListActivity.this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show();
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS_READFILE = 123;
    final private int REQUEST_CODE_ASK_PERMISSIONS_WRITEFILE = 124;

    private boolean checkPermission(final boolean forReadFile) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(ReceiptListActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(ReceiptListActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showMessageOKCancel("You need to allow access to Storage",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //If this function is called on pre-M, OnRequestPermissionsResultCallback will be suddenly
                            // called with correct PERMISSION_GRANTED or PERMISSION_DENIED result.
                            ActivityCompat.requestPermissions(ReceiptListActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    forReadFile?REQUEST_CODE_ASK_PERMISSIONS_READFILE:REQUEST_CODE_ASK_PERMISSIONS_WRITEFILE);
                        }
                    });
                return false;
            }
            ActivityCompat.requestPermissions(ReceiptListActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    forReadFile?REQUEST_CODE_ASK_PERMISSIONS_READFILE:REQUEST_CODE_ASK_PERMISSIONS_WRITEFILE);
            return false;
        }
        return true;
    }

    private String readFileContent(Uri uri) throws IOException {

        InputStream inputStream =
                getContentResolver().openInputStream(uri);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(
                        inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null) {
            stringBuilder.append(currentline + "\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }

    private void writeFileContent(Uri uri)
    {
        try{
            ParcelFileDescriptor pfd =
                    this.getContentResolver().
                            openFileDescriptor(uri, "w");

            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());

            //get all receipt list in specific format as following
            //[2016-11-03 camera - walmart]
            //item name: camera
            //item type: Electronics
            //Store name: walmart
            //Purchase date: 2016-08-15
            //Due date: 2016-11-03
            //....space...
            String textContent =
                    getAllReceiptAsString();

            fileOutputStream.write(textContent.getBytes());

            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        Uri currentUri = null;

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == CREATE_REQUEST_CODE)
            {
//                if (resultData != null) {
//                    textView.setText("");
//                }
            } else if (requestCode == SAVE_REQUEST_CODE) {

                if (resultData != null) {
                    currentUri = resultData.getData();
                    writeFileContent(currentUri);
                }
            } else if (requestCode == OPEN_REQUEST_CODE)
            {

                if (resultData != null) {
                    currentUri = resultData.getData();

                    try {
                        String content =
                        readFileContent(currentUri);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle("Cloud Backup Receipt Text");
                        dialog.setMessage(content);
                        dialog.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                            }
                        });
                        dialog.show();
                    } catch (IOException e) {
                        // Handle error here
                    }
                }
            }
        }
    }
    public void saveFile(View view)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, SAVE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_READFILE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/plain");
                    startActivityForResult(intent, OPEN_REQUEST_CODE);
                } else {
                    // Permission Denied
                    Toast.makeText(ReceiptListActivity.this, "Read external files denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            case REQUEST_CODE_ASK_PERMISSIONS_WRITEFILE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/plain");
                    startActivityForResult(intent, SAVE_REQUEST_CODE);
                } else {
                    // Permission Denied
                    Toast.makeText(ReceiptListActivity.this, "Write external files denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // set click listener for widgets and grab the data from data base and display in list view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);

        btnOrderbyDate = (Button) findViewById(R.id.btnOrderbyDate);
        btnSaveToCloud = (Button) findViewById(R.id.btnSaveToCloud);
        btnReadFromCloud = (Button) findViewById(R.id.btnReadCloud);
        lvReceipts = (ListView) findViewById(R.id.lvReceipts);
        myList = (ListView)findViewById(R.id.lvReceipts);
        myList.setOnItemClickListener(this);
        myActivity = this;

        btnReadFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (checkPermission(true)) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/plain");
                        startActivityForResult(intent, OPEN_REQUEST_CODE);
                    }

                } catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                }
            }
        });

        btnSaveToCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkPermission(false)) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/plain");
                        startActivityForResult(intent, SAVE_REQUEST_CODE);
                    }
                } catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                }
            }
        });

        // implements the behave for button "order by date button"
        btnOrderbyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // sort in Asc/Descending order and notify adapter is changed
                    if (!isOrderDesc) {
                        Collections.sort(al, new Comparator<String>() {
                            @Override
                            public int compare(String str1, String str2) {
                                return str1.compareTo(str2);
                            }
                        });

                        adapter.notifyDataSetChanged();
                        Toast t = Toast.makeText(myActivity, "Order by Ascending date", Toast.LENGTH_SHORT);
                        t.show();
                    } else {
                        Collections.sort(al, new Comparator<String>() {
                            @Override
                            public int compare(String str1, String str2) {
                                return str2.compareTo(str1);
                            }
                        });

                        adapter.notifyDataSetChanged();
                        Toast t = Toast.makeText(myActivity, "Order by Descending date", Toast.LENGTH_SHORT);
                        t.show();
                    }

                    isOrderDesc = !isOrderDesc;
                } catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                }
            }
        });

        try{

            al = new ArrayList<>();
            db = new ItemInfoListDB(this);

            // Get data from database
            Cursor cur = db.getItemInfoCursor("MyPurchase");
            if(!cur.moveToFirst()) return;
            do{
                String content = cur.getString(ItemInfoListDB.ITEMINFO_REFUND_DUEDATE_COL);
                content += "  " + cur.getString(ItemInfoListDB.ITEMINFO_ITEM_NAME_COL);
                content += " - " + cur.getString(ItemInfoListDB.ITEMINFO_STORE_NAME_COL);
                al.add(content);
            }while(cur.moveToNext());

            // Sort data with descending order
            Collections.sort(al, new Comparator<String>() {
                @Override
                public int compare(String str1, String str2) {
                    return str2.compareTo(str1);
                }
            });

            adapter = new ArrayAdapter<String>(this,R.layout.format_receipts_list_item,al);
            myList.setAdapter(adapter);

        }catch (Exception ex){
            Log.e("Exception: ", ex.getMessage());
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {
        try{

            String tmp = al.get(position);

            // Parse the string with due date, item name, store name
            Pattern pattern = Pattern.compile("^([0-9-]+)  ([^-]+) - ([\\w\\s]+)$");
            Matcher matcher = pattern.matcher(tmp);
            String duedate = "", itemName = "", storeName = "";
            while (matcher.find()) {
                duedate = matcher.group(1);
                itemName = matcher.group(2);
                storeName = matcher.group(3);
            }

            // Retrieve a item data
            ItemInfo itemInfo = db.getItemInfo(duedate, itemName, storeName);
            ArrayList<String> content = new ArrayList<String>();

            // Create an intent and hand over data to ReciptListItem Activity
            Intent intent = new Intent(this, ReciptListItemActivity.class);

            intent.putExtra("itemName", itemInfo.getItemName());
            intent.putExtra("itemType", itemInfo.getItemType());
            intent.putExtra("storeName", itemInfo.getStoreName());
            intent.putExtra("purchaseDate", itemInfo.getPurchaseDate());
            intent.putExtra("dueDate",itemInfo.getRefundDueDate());

            this.startActivity(intent);

        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
        }
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
