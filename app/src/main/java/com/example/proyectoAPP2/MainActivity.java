package com.example.proyectoAPP2;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Environment;
import android.transition.ChangeClipBounds;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {
    //private ImageView ivCapt;
    //private Context context = this;
    int h,w;
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.lay);
        Display display = ((WindowManager) Objects.requireNonNull(getSystemService(WINDOW_SERVICE)))
                .getDefaultDisplay();
        int ori = display.getRotation();
        Boolean flag = true;
        if(ori != 0) textureView = findViewById(R.id.layLand);
        Log.wtf("flag", flag+"");
        ImageView btnGal = findViewById(R.id.btnGaleria);
       //Log.wtf("width on create ",lay.getMeasuredWidth()+"");
       //Log.wtf("width on create ",lay.getMeasuredHeight()+"");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        h = displayMetrics.heightPixels;
        w = displayMetrics.widthPixels;
        //Toast.makeText(this,""+h,Toast.LENGTH_SHORT).show();
        btnGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Crop.class);
                intent.putExtra("com",2);
                startActivity(intent);
            }
        });
        if(allPermissionsGranted() && flag){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }
    @SuppressLint("SwitchIntDef")
    private void startCamera() {
        CameraX.unbindAll();
        Display display = ((WindowManager) Objects.requireNonNull(getSystemService(WINDOW_SERVICE)))
                .getDefaultDisplay();
        final Rational aspectRatio;
        Size screen;
        final int orientation = display.getRotation();
        aspectRatio = new Rational (2,1);
        screen = new Size(h,w); //size of the screen
        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen).setTargetRotation(orientation).build();
        //textureView.setLayoutParams();
        Preview preview = new Preview(pConfig);
        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    //to update the surface texture we  have to destroy it first then re-add it
                    @Override
                    public void onUpdated(Preview.PreviewOutput output){
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView,0);
                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);

        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory() + "/holas.png");
                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        //String msg = "Pic captured at " + file.getAbsolutePath();
                        // si la imagen existe
                        if(file.exists()){
                            Intent intent = new Intent(getApplicationContext(), Crop.class);
                            intent.putExtra("image_path",Uri.fromFile(file).toString());
                            intent.putExtra("com",1);
                            startActivity(intent);
                        }
                        //Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError,
                                        @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic capture failed : " + message;
                        Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
                        if(cause != null){
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });
        //bind to lifecycle:
        CameraX.bindToLifecycle(this, preview, imgCap);
    }
    private void updateTransform(){
        Matrix mx = new Matrix();
        Display display = ((WindowManager) Objects.requireNonNull(getSystemService(WINDOW_SERVICE)))
                .getDefaultDisplay();
        int orientation = display.getRotation();
        float cX = w/2f;
        float cY = h/2f;
        int rotationDgr;
        switch(orientation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 270;
                cX = w/3f;
                cY = h/2f;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 90;
                break;
            default:
                return;
        }
        mx.postRotate(rotationDgr,cX,cY);
        textureView.setTransform(mx);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}