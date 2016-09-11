package com.example.administrator.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

//================================================//
// NAME : ReciptListItemActivity
// PURPOSE : This Class display a item info from ReceiptList Activity
//================================================//
public class ReciptListItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipt_list_item);

        TextView tvItemName = (TextView)findViewById(R.id.reciptItemName);
        TextView tvItemType = (TextView)findViewById(R.id.reciptItemType);
        TextView tvStoreName = (TextView)findViewById(R.id.reciptItemStore);
        TextView tvPurchaseDate = (TextView)findViewById(R.id.reciptItemPurDate);
        TextView tvDueDate = (TextView)findViewById(R.id.reciptItemDuedate);

        // Get intent
        Intent intent = getIntent();

        // get data from the intent
        String itemName = intent.getStringExtra("itemName");
        String itemType = intent.getStringExtra("itemType");
        String storeName = intent.getStringExtra("storeName");
        String purchaseDate = intent.getStringExtra("purchaseDate");
        String dueDate = intent.getStringExtra("dueDate");

        tvItemName.setText(itemName);
        tvItemType.setText(itemType);
        tvStoreName.setText(storeName);
        tvPurchaseDate.setText(purchaseDate);
        tvDueDate.setText(dueDate);
    }

}
