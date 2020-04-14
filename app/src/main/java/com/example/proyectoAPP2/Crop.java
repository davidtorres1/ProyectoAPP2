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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Crop extends AppCompatActivity {
    ImageView crpImage;
    private int GalleryPick = 1;
    Uri imagenUri;
    Intent intent;
    SeekBar sb;
    String imageLocation;
    int x = 80;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        crpImage = findViewById(R.id.imagen);
        intent = getIntent();
        int com = intent.getIntExtra("com",-1);
        switch (com){
            case 1:
                Toast.makeText(this,"camara",Toast.LENGTH_SHORT).show();
                String imagen = intent.getStringExtra("image_path");
                if(imagen != null){
                    imagenUri = Uri.parse(imagen);
                    cambiarImagen(imagenUri);
                }
                break;
            case 2:
                Toast.makeText(this,"galeria",Toast.LENGTH_SHORT).show();
                //crop();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GalleryPick);
                break;
             default:
                 Toast.makeText(this,"ya valio chilindrin",Toast.LENGTH_SHORT).show();
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
                matrix.setSaturation(0);
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
        Button btn = findViewById(R.id.btnSubmit);
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
        File file = new File(storageLoc, filename + ".jpg");
        try{
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            scanFile(getApplicationContext(), Uri.fromFile(file));
            //Toast.makeText(getApplicationContext(),file+"",Toast.LENGTH_LONG).show();
            send(bmp);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(final Bitmap bitmap){
      Toast.makeText(getApplicationContext(),"enviando...", Toast.LENGTH_LONG).show();
      String url = "http://192.168.1.72//pruebaImagen.php";
        final StringRequest string = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error + "",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String imageData = imageToString(bitmap);
                params.put("image",imageData);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Crop.this);
        requestQueue.add(string);

    }
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedString = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        Log.wtf("string",encodedString);
        return encodedString;
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
        matrix.setSaturation(0);
        matrix.set(colorTransform);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        crpImage.setColorFilter(filter);

    }
    public void contrasts(Uri uri){

    }

    public void crop(Uri uri){
        CropImage.activity(uri)
        .setBackgroundColor(Color.argb(200,20,20,238))
                .setAspectRatio(2,1)
                .setActivityTitle("MIA")
                .setAllowRotation(true)
                .setAllowFlipping(false)
                .setCropMenuCropButtonIcon(R.drawable.oplogo1)
                .start(this);
        Toast.makeText(this,uri +" ",Toast.LENGTH_SHORT).show();
    }
}
