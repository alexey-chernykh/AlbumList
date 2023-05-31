package com.example.albumlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

public class AddAlbumActivity extends AppCompatActivity {

    EditText editAlbumName, editActor, editYear;
    ImageView previewImage;
    AppCompatButton insertPicture;
    byte[] selectedPicture;
    Album newAlbum;
    AppCompatButton btnSubmit;
    public static final int STORAGE_REQUEST=101;
    public static final int CAMERA_REQUEST=100;
    String[] cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_album);
        editAlbumName = findViewById(R.id.editAlbumName);
        editActor = findViewById(R.id.editActor);
        editYear = findViewById(R.id.editYear);
        previewImage = findViewById(R.id.PreviewImage);
        insertPicture = findViewById(R.id.insertPicture);
        insertPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int avatar = 0;
                if (avatar == 0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromGallery();
                    }
                }else if(avatar == 1){
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }
                }
                pickFromGallery();
            }
        });
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckFields()){
                    newAlbum = new Album(editAlbumName.getText().toString(),
                            editActor.getText().toString(),
                            ImageViewToByte(previewImage),
                            Integer.parseInt(editYear.getText().toString()));
                    DBMain.getInstance().insertAlbum(newAlbum);
                    finish();
                }else{
                    Toast.makeText(AddAlbumActivity.this, "Enter data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean CheckFields() {
        if (ImageViewToByte(previewImage) == null || editAlbumName.getText().toString().equals("") ||
                editActor.getText().toString().equals("") || editYear.getText().toString().equals(""))
        {
            return false;
        }
        return true;
    }

    private byte[] ImageViewToByte(ImageView imageView) {
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            return imageInByte;
        }catch (Exception ex){
            return null;
        }
    }

    private boolean checkStoragePermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result1;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    public void pickFromGallery(){
        CropImage.activity().start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_REQUEST:{
                if (grantResults.length>0){
                    boolean camera_accept = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean storage_accept = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if (camera_accept && storage_accept){
                        pickFromGallery();
                    }
                }
            }
            break;
            case STORAGE_REQUEST:{
                if (grantResults.length>0){
                    boolean storage_accept = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (storage_accept){
                        pickFromGallery();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                Uri resultUri=result.getUri();
                Picasso.with(this).load(resultUri).into(previewImage);
            }
        }
    }
}