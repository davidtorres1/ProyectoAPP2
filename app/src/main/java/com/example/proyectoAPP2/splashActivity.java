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
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(splashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);
        final SQLiteDatabase db = this.openOrCreateDatabase(
                "ScanImages",
                MODE_ENABLE_WRITE_AHEAD_LOGGING,
                null);
        try {
            //perform your database operations here ...
            db.execSQL("create table tblAMIGO ("
                    + " recID integer PRIMARY KEY autoincrement, "
                    + " file text, "
                    + " descr text ); " );
            db.setTransactionSuccessful(); //commit your changes
        }
        catch (SQLiteException e) {
            Log.getStackTraceString(e);
        }
    }
}
