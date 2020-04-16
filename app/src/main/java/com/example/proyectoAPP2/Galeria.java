package com.example.proyectoAPP2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

public class Galeria extends AppCompatActivity {
    ViewPager viewPager;
    InfoAdapter adapter;
    List<Info> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    SQLiteDatabase db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);
        models = new ArrayList<>();
        db = this.openOrCreateDatabase(
                "ScanImages",
                MODE_ENABLE_WRITE_AHEAD_LOGGING,
                null);
        context = this;
        //Todo poner un metodo aqui
        update();
        final Integer[] colors_ = {getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };


        colors = colors_;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                //viewPager.setBackground();
                Button btn = findViewById(R.id.btnDelete);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),""+ position,Toast.LENGTH_LONG).show();
                        eliminarAD(position);
                    }
                });

            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(getApplicationContext(),"" + position,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.wtf("state: ",""+ state);
            }
        });
    }
    private void eliminarAD(int pos){
        final int i = pos;
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setTitle("¿Seguro?");
        dialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String file = models.get(i).getImage();
                        eliminar(file);
                    }
                });
        dialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        Toast.makeText(getApplicationContext(),"ta gueno pues",Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
    public void eliminar(String file){
        String [] whereArgs = {file};
        //Toast.makeText(getApplicationContext(),whereArgs[0], Toast.LENGTH_LONG).show();
        int recAffected = db.delete( "tblAMIGO", "file = ?", whereArgs);
        Toast.makeText(getApplicationContext(),"status = " + recAffected,Toast.LENGTH_SHORT).show();
        update();

    }
    public void update(){
        models = new ArrayList<>();
        String sql = "select * from tblAmigo";
        Cursor c1 = db.rawQuery(sql, null);
        if(c1 != null){
            c1.moveToPosition(-1);
            while (c1.moveToNext()) {
                int recId = c1.getInt(0);
                String name = c1.getString(1);
                String phone = c1.getString(2);
                Log.wtf("nombre", name);
                if (name != null) {
                    models.add(new Info(name, phone));
                }
                Log.wtf("tel", phone);
            }
            c1.close();
        }
        adapter = new InfoAdapter(models, this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130,0,130,0);
    }


}
