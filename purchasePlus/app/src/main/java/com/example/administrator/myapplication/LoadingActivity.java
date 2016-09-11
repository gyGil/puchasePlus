/*
* FILE : LoadingActivity.java
* PROJECT : PROG3150 - Assignment #1
* PROGRAMMER : LingChen Meng(Walter) / Xuan Zhang / Marcus Rankin / GeunYoung Gil
* FIRST VERSION : 2016-02-07
* DESCRIPTION : This file is used to handle with loading Activities.
*/

package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/*
* NAME : LoadingActivity
* PURPOSE : This Class is used to handle with loading Activities.
*/
public class LoadingActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        try{
            // get imageView object.
            final ImageView iv = (ImageView)findViewById(R.id.imageView);
            // set animation for rotate picture.
            final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

            // start animation.
            iv.startAnimation(an);
            an.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    // when rotate is finish.
                    finish();

                    // instance a intent and transfer to another page when loading page finished.
                    Intent in = new Intent(LoadingActivity.this, LogInActivity.class);
                    // goto another page.
                    startActivity(in);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            // create sample data
            ItemInfoListDB db = new ItemInfoListDB(this);
            db.createSampleData();

        }catch (Exception e)
        {
            // handle exception
            Log.e("Exception: ", e.getMessage());
        }
    }
}

