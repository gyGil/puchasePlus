/*
* FILE : ReceiptEntryActivity.java
* PROJECT : PROG3150 - Assignment #1
* PROGRAMMER : LingChen Meng(Walter) / Xuan Zhang / Marcus Rankin / GeunYoung Gil
* FIRST VERSION : 2016-02-07
* DESCRIPTION : This file is used to handle ReceiptEntryActivity.
*/
package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//================================================//
// NAME : ReceiptEntryActivity
// PURPOSE : Get input for new receipt item
//================================================//
public class ReceiptEntryActivity extends Activity {

    //====================================[ MEMBERS ]====================================//
    private EditText itemName,storeName,purchaseAmount;
    private DatePicker purchaseDate;
    RadioButton appliance,electronics,clothes,others;
    RadioGroup rg;
    String whichItem;
    int whichDays;
    CheckBox checkBox_One,checkBox_Two,checkBox_Three;
    Button saveButton;
    Button logoButton;
    List<String> myList = new ArrayList<String>();
    private WebView myWebView;
    String searchPolicy;
    Context receiptActivity;
    ItemInfoListDB db;
    StringBuilder sb;


    //====================================[ METHODS ]====================================//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_entry);

        itemName = (EditText) findViewById(R.id.editText_ItemName);
        storeName = (EditText) findViewById(R.id.editText_StoreName);
        purchaseDate = (DatePicker) findViewById(R.id.datePicker);
        purchaseAmount = (EditText) findViewById(R.id.editText_PurchaseAmount);
        rg = (RadioGroup) findViewById(R.id.radioGroup1);
        appliance = (RadioButton) findViewById(R.id.radioAppliance);
        electronics = (RadioButton) findViewById(R.id.radioElectronics);
        clothes = (RadioButton) findViewById(R.id.radioClothes);
        others = (RadioButton) findViewById(R.id.radioOthers);
        checkBox_One = (CheckBox) findViewById(R.id.checkBox1);
        checkBox_Two = (CheckBox) findViewById(R.id.checkBox2);
        checkBox_Three = (CheckBox) findViewById(R.id.checkBox3);
        saveButton = (Button) findViewById(R.id.button_Save);
        logoButton = (Button) findViewById(R.id.button_Webview);
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setVerticalScrollBarEnabled(true);
        myWebView.setHorizontalScrollBarEnabled(true);
        myWebView.loadUrl("file:///android_res/drawable/returnbox.jpg");
        receiptActivity = this;
        whichItem = ItemInfo.TYPE_APPLIANCE;
        db = new ItemInfoListDB(this);
        sb = new StringBuilder();

        try{

            // radio button group listener
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radioAppliance:
                            // if a item is checked, it show the toast box and save selected item
                            if (appliance.isChecked()) {
                                Toast.makeText(ReceiptEntryActivity.this, "You choose Appliance", Toast.LENGTH_SHORT).show();
                                whichItem = ItemInfo.TYPE_APPLIANCE;
                            }
                            break;

                        case R.id.radioElectronics:
                            if (electronics.isChecked()) {
                                Toast.makeText(ReceiptEntryActivity.this, "You choose Electronics", Toast.LENGTH_SHORT).show();
                                whichItem = ItemInfo.TYPE_ELECTORONICS;
                            }
                            break;

                        case R.id.radioClothes:
                            if (clothes.isChecked()) {
                                Toast.makeText(ReceiptEntryActivity.this, "You choose Clothes", Toast.LENGTH_SHORT).show();
                                whichItem = ItemInfo.TYPE_CLOTHES;
                            }
                            break;

                        case R.id.radioOthers:
                            if (others.isChecked()) {
                                Toast.makeText(ReceiptEntryActivity.this, "You choose Others", Toast.LENGTH_SHORT).show();
                                whichItem = ItemInfo.TYPE_CLOTHES;
                            }
                            break;
                    }
                }
            });
        }catch (Exception e)
        {
            Log.e("Exception: ", e.getMessage());
        }

        // check box group, checked only one
        try{
            checkBox_One.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox_Two.isChecked())
                        checkBox_Two.toggle();
                    if(checkBox_Three.isChecked())
                        checkBox_Three.toggle();
                    if (checkBox_One.isChecked()) {
                        Toast.makeText(ReceiptEntryActivity.this, "30 Days checked", Toast.LENGTH_SHORT).show();
                        whichDays = 30;
                    }
                }
            });
        }catch (Exception e)
        {
            Log.e("Exception: ",e.getMessage());
        }

        try{
            checkBox_Two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox_One.isChecked())
                        checkBox_One.toggle();
                    if(checkBox_Three.isChecked())
                        checkBox_Three.toggle();
                    if (checkBox_Two.isChecked()) {
                        Toast.makeText(ReceiptEntryActivity.this, "60 Days checked", Toast.LENGTH_SHORT).show();
                        whichDays = 60;
                    }
                }
            });
        }catch (Exception e)
        {
            Log.e("Exception: ",e.getMessage());
        }

        try{
            checkBox_Three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox_Two.isChecked())
                        checkBox_Two.toggle();
                    if(checkBox_One.isChecked())
                        checkBox_One.toggle();
                    if (checkBox_Three.isChecked()) {
                        Toast.makeText(ReceiptEntryActivity.this, "90 Days checked", Toast.LENGTH_SHORT).show();
                        whichDays = 90;
                    }
                }
            });
        }catch (Exception e)
        {
            Log.e("Exception: ",e.getMessage());
        }


        try{

            saveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                  //  public ItemInfo(long listId, String itemType, String itemName, String storeName,
                 //   double purchaseAmount, String purchaseDate, String refundDueDate, int hidden)

                    double price = Double.parseDouble(purchaseAmount.getText().toString());

                    // date format mm-dd-yyyy
                    String startDate = "";
                    startDate = Integer.toString(purchaseDate.getYear()) + "-";
                    if(purchaseDate.getMonth() + 1 < 10) startDate += "0";
                    startDate += Integer.toString(purchaseDate.getMonth() + 1) + "-"; // Months are 0 to 11 (want 1 to 12)
                    if(purchaseDate.getDayOfMonth() < 10) startDate += "0";
                    startDate += Integer.toString(purchaseDate.getDayOfMonth());


                    // calculate due date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    ParsePosition pp = new ParsePosition(0);
                    calendar.setTime(sdf.parse(startDate, pp));
                    calendar.add(Calendar.DAY_OF_MONTH, whichDays);
                    String dueDate = sdf.format(calendar.getTime());

                    // Data entity for information of database
                    ItemInfo itemInfo = new ItemInfo(1,whichItem,itemName.getText().toString(),
                                        storeName.getText().toString(), price, startDate,
                                        dueDate,ItemInfo.FALSE);

                    long insertId = db.insertItemInfo(itemInfo);
                    Toast.makeText(ReceiptEntryActivity.this, "Data saved.", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e)
        {
            Log.e("Exception: ",e.getMessage());
        }

        try{
            logoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                searchPolicy = "http://www.google.com/search?q=" + storeName.getText().toString() + " store number of days to return items&num=01";

                // WebView for retrieving Store Name logo
                myWebView.setWebViewClient(new PolicyWebViewClient(receiptActivity));

                PolicyWebViewClient logoSearch = new PolicyWebViewClient(receiptActivity);

                boolean result = logoSearch.shouldOverrideUrlLoading(myWebView, searchPolicy);

                if (!result) { myWebView.loadUrl("file:///android_res/drawable/noresultsfound.jpg"); }

                }
            });
        }catch (Exception e)
        {
            Log.e("Exception: ",e.getMessage());
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
