package com.example.proyectoAPP2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

    public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO checar bien que royo aqui
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(splashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
        final SQLiteDatabase db = this.openOrCreateDatabase(
                "ScanImages",
                MODE_ENABLE_WRITE_AHEAD_LOGGING,
                null);
        try {
            //perform your database operations here ..accuracy, String ecg_id, String result, String resultIndex, String imageLocation
            db.execSQL("create table imagesRes ("
                    + " recID integer PRIMARY KEY autoincrement, "
                    + "accuracy text,"
                    + "ecg_id text,"
                    + "result text,"
                    + "resultIndex text,"
                    + " imageLocation text );");
            db.setTransactionSuccessful(); //commit your changes
        }
        catch (SQLiteException e) {
            Log.getStackTraceString(e);
        }
    }
}
