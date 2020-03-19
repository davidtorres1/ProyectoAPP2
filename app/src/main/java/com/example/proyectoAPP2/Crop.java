package com.example.proyectoAPP2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

public class Crop extends AppCompatActivity {
    ImageView crpImage;
    private int GalleryPick = 1;
    Uri imagenUri;
    Intent intent;
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
    }
    /*public void crop(){
        Toast.makeText(this,"aqui",Toast.LENGTH_SHORT).show();
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).
                setBackgroundColor(Color.argb(200,20,20,238))
                .setAspectRatio(2,1)
                .start(this);
    }*/

    public void crop(Uri uri){
        CropImage.activity(uri)
        .setBackgroundColor(Color.argb(200,20,20,238))
                .setAspectRatio(2,1)
                .start(this);
        Toast.makeText(this,uri +" ",Toast.LENGTH_SHORT).show();
    }
}
