/*
* FILE : SettingActivity.java
* PROJECT : PROG3150 - Assignment #1
* PROGRAMMER : LingChen Meng(Walter) / Xuan Zhang / Marcus Rankin / GeunYoung Gil
* FIRST VERSION : 2016-02-07
* DESCRIPTION : This file is used to handle SettingActivity.
*/
package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

/*
* NAME : SettingActivity
* PURPOSE : This Class handles the settings for the application
*/
public class SettingActivity extends Activity {

    private Switch passwordStatus, default30;
    private ToggleButton returnStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //For enabling or disabling password login protection
        passwordStatus = (Switch) findViewById(R.id.switchPassword);

        //For setting the default receipt expiration to 30 days
        default30 = (Switch) findViewById(R.id.switch30);

        //For enabling or disabling receipt expiration notifications
        returnStatus = (ToggleButton) findViewById(R.id.toggleReturn);

        /*
        * NAME : passwordStatus.setOnCheckedChangeListener()
        * PURPOSE : This function sets the password switch change listener to check for changes
        *           and notifies the user of the settings change.
        */
        passwordStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton passwordEnabled, boolean isChecked) {
                try {
                    if (isChecked)  //Password enabled
                    {
                        Toast.makeText(SettingActivity.this, "Application Status: Password Enabled", Toast.LENGTH_SHORT).show();

                    } else if (!isChecked)  // Password disabled
                    {
                        Toast.makeText(SettingActivity.this, "Application Status: Password Disabled", Toast.LENGTH_SHORT).show();
                    }

                    Log.i("Password State = ", "" + isChecked);
                } catch (Exception e) {
                    Log.e("Password State Error: ", "" + e.getMessage());
                }
                ;

            }
        });

        /*
        * NAME : default30.setOnCheckedChangeListener()
        * PURPOSE : This function sets the default receipt expiration date to 30 days or
        *           disables the default expiration date.
        */
        default30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton defaultEnabled, boolean isChecked) {
                try {
                    if (isChecked)  //Receipt Expiration Date set to 30 days
                    {
                        Toast.makeText(SettingActivity.this, "Receipt Expiration: Default 30 days Enabled", Toast.LENGTH_SHORT).show();

                    } else if (!isChecked)  // No Receipt Expiration Date set
                    {
                        Toast.makeText(SettingActivity.this, "Receipt Expiration: Default 30 days Disabled", Toast.LENGTH_SHORT).show();
                    }

                    Log.i("Default State = ", "" + isChecked);
                } catch (Exception e) {
                    Log.e("Default State Error: ", "" + e.getMessage());
                }
                ;

            }
        });

        /*
        * NAME : returnStatus.setOnCheckedChangeListener()
        * PURPOSE : This function sets return status notifications to be enabled or
        *           disabled. This will notify the user of an upcoming receipt expiration.
        */
        returnStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton returnEnabled, boolean isChecked) {
                try {
                    if (isChecked)  //Receipt Notification enabled
                    {
                        Toast.makeText(SettingActivity.this, "Receipt Notifications: Enabled", Toast.LENGTH_SHORT).show();

                    } else if (!isChecked)  // Receipt Notification disabled
                    {
                        Toast.makeText(SettingActivity.this, "Receipt Notifications: Disabled", Toast.LENGTH_SHORT).show();
                    }

                    Log.i("Notification State = ", "" + isChecked);
                } catch (Exception e) {
                    Log.e("Notification StateErr: ", "" + e.getMessage());
                }
                ;

            }
        });
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
