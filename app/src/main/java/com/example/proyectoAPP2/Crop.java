package com.example.proyectoAPP2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class Crop extends AppCompatActivity{
    ImageView crpImage;
    ProgressBar load;
    private int GalleryPick = 1;
    Uri imagenUri;
    Intent intent;
    Button btn;
    SeekBar sb;
    SQLiteDatabase db;
    String imageLocation;
    int x = 80;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        load = findViewById(R.id.progressBar);
        load.setVisibility(View.INVISIBLE);
        crpImage = findViewById(R.id.imagen);
        intent = getIntent();
        int com = intent.getIntExtra("com",-1);
        switch (com){
            case 1:
                //Toast.makeText(this,"camara",Toast.LENGTH_SHORT).show();
                String imagen = intent.getStringExtra("image_path");
                if(imagen != null){
                    imagenUri = Uri.parse(imagen);
                    cambiarImagen(imagenUri);
                }
                break;
            case 2:
                //Toast.makeText(this,"galeria",Toast.LENGTH_SHORT).show();
                //crop();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GalleryPick);
                break;
             default:
                 //Toast.makeText(this,"ya valio chilindrin",Toast.LENGTH_SHORT).show();

                 break;
        }
        crpImage = findViewById(R.id.imagen);
        crpImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                crop(imagenUri);
            }
        });
        sb = findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                x = progress;
                crpImage =findViewById(R.id.imagen);
                float[] colorTransform = {
                        85, 85, 85, 0, -x * 255,
                        85, 85, 85, 0, -x * 255,
                        85, 85, 85, 0, -x * 255,
                        0, 0, 0, 1, 0 };
                ColorMatrix matrix = new ColorMatrix();
                //matrix.setSaturation(0);
                matrix.set(colorTransform);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                crpImage.setColorFilter(filter);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btn = findViewById(R.id.btnSubmit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private static void scanFile(Context context, Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }

    public void submit(){
        crpImage.buildDrawingCache();
        Bitmap bmp = crpImage.getDrawingCache();
        File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //context.getExternalFilesDir(null);
        String filename = "holasss";
        File file = new File(storageLoc, filename + System.currentTimeMillis() + ".jpg");
        imageLocation = file.toString();
        try{
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            scanFile(getApplicationContext(), Uri.fromFile(file));
            String image = imageToString(bmp);
            loading(true);
            send(image);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send(String bitmap){
      Toast.makeText(getApplicationContext(),"enviando...", Toast.LENGTH_LONG).show();
        String url = "https://ecg-super-api.herokuapp.com/predict";
        Map<String, String> params = new HashMap<String, String>();
        params.put("image", bitmap);
        JSONObject jsonObj = new JSONObject(params);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.wtf("Response", response.toString());
                        try{
                            boolean status = response.getBoolean("error");
                            Log.wtf("error", status+"");
                            if(!status){
                                JSONObject res = response.getJSONObject("data");
                                String accuracy = res.getString("accuracy");
                                String ecg_id = res.getString("ecg_id");
                                String result = res.getString("result");
                                String resultIndex = res.getString("resultIndex");
                                //TODO insertar estos datos en la base de datos junto con el path de la imagen
                                insertar(accuracy,ecg_id,result,resultIndex,imageLocation);
                                startActivity(new Intent(getApplicationContext(), Galeria.class));
                            }else{
                                loading(false);
                                Toast.makeText(getApplicationContext(),"Se ha producido un error, por favor intentelo de nuevo en unos minutos",Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Se ha producido un error, por favor intentelo de nuevo en unos minutos",Toast.LENGTH_LONG).show();
                            loading(false);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("Error.Response", error);
                        Toast.makeText(getApplicationContext(),"Se ha producido un error, por favor intentelo de nuevo en unos minutos",Toast.LENGTH_LONG).show();
                        loading(false);
                    }
                }
        );
        queue.add(getRequest);
    }
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedString = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedString;
    }
    public void insertar(String accuracy, String ecg_id, String result, String resultIndex, String imageLocation){
        SQLiteDatabase db = this.openOrCreateDatabase(
                "ScanImages",
                MODE_ENABLE_WRITE_AHEAD_LOGGING,
                null);
        try{
            db.execSQL("insert into imagesRes(accuracy, ecg_id, result, resultIndex, imageLocation) values " +
                            "('"+accuracy+"', '"+ecg_id+"', '"+result+"', '"+resultIndex+"', '"+imageLocation+"');");
            db.setTransactionSuccessful(); //commit your changes
        }catch (Exception e) {
            Log.getStackTraceString(e);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!=null){
            Toast.makeText(this, "hi" , Toast.LENGTH_LONG).show();
            imagenUri = data.getData();
            cambiarImagen(imagenUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode== RESULT_OK){
                assert result != null;
                imagenUri = result.getUri();
                cambiarImagen(imagenUri);
            }
        }
    }
    public void cambiarImagen(Uri uri){
        crpImage =findViewById(R.id.imagen);
        crpImage.setImageURI(uri);
        float[] colorTransform = {
                85, 85, 85, 0, -x * 255,
                85, 85, 85, 0, -x * 255,
                85, 85, 85, 0, -x * 255,
                0, 0, 0, 1, 0 };
        ColorMatrix matrix = new ColorMatrix();
        //matrix.setSaturation(0);
        matrix.set(colorTransform);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        crpImage.setColorFilter(filter);

    }
    public void crop(Uri uri){
        CropImage.activity(uri)
        .setBackgroundColor(Color.argb(200,20,20,238))
                .setActivityTitle("MIA")
                .setAllowRotation(true)
                .setAllowFlipping(false)
                .setCropMenuCropButtonIcon(R.drawable.oplogo1)
                .start(this);
        Toast.makeText(this,uri +" ",Toast.LENGTH_SHORT).show();
    }
    public void loading(boolean flag){
        if(flag){
            crpImage.setVisibility(View.INVISIBLE);
            sb.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.INVISIBLE);
            load.setVisibility(View.VISIBLE);
        }else{
            crpImage.setVisibility(View.VISIBLE);
            sb.setVisibility(View.VISIBLE);
            btn.setVisibility(View.VISIBLE);
            load.setVisibility(View.INVISIBLE);
        }

    }
}
