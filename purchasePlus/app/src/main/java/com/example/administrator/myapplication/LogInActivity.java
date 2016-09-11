/*
* FILE : LogInActivity.java
* PROJECT : PROG3150 - Assignment #1
* PROGRAMMER : LingChen Meng(Walter) / Xuan Zhang / Marcus Rankin / GeunYoung Gil
* FIRST VERSION : 2016-02-07
* DESCRIPTION : This file is used to handle login Activities.
*/
package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/*
* NAME : LogInActivity
* PURPOSE : This Class is used to handle with LogIn Activities.
*/
public class LogInActivity extends Activity {

    // instance a button object.
    private Button btnLogin = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get button id
        btnLogin = (Button)findViewById(R.id.btnLogin);

        try{
            // set onclick listener to button.
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // instance a intent go to another page.
                    Intent in = new Intent(LogInActivity.this, ReceiptListActivity.class);
                    // goto login page.
                    startActivity(in);
                }
            });
        }catch (Exception e)
        {
            // handle exception
            Log.e("Exception: ",e.getMessage());
        }

    }
}
