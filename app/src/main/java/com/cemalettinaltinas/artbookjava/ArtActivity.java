package com.cemalettinaltinas.artbookjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.cemalettinaltinas.artbookjava.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ArtActivity extends AppCompatActivity {
    Bitmap selectedImage;
    SQLiteDatabase database;
    ActivityResultLauncher<Intent> galeryResultLauncher;
    ActivityResultLauncher<String> izinlerResultLauncher;
    private ActivityArtBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityArtBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        registerGalery();

    }

    public void  registerGalery(){
        galeryResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK){
                    //Intent intentSonuc=getIntent();
                    Intent intentSonuc=result.getData();
                    if (intentSonuc!=null){
                        Uri fotoVeri=intentSonuc.getData();
                        //imageView.setImageURI(fotoVeri);

                        try {
                            if(Build.VERSION.SDK_INT>=28){
                                ImageDecoder.Source source=ImageDecoder.createSource(ArtActivity.this.getContentResolver(),fotoVeri);
                                Bitmap selectedImage=ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }else{
                                Bitmap selectedImage=MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),fotoVeri);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        izinlerResultLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //izin var galeriye gidilecektir.
                    Intent galeri=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galeryResultLauncher.launch(galeri);
                }
                else{
                    //izin istenmesi gerekecektir.
                    Toast.makeText(ArtActivity.this,"İzin vermeniz gerekir.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void galeriyeGit(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            //İzin yok, izin istenecektir.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                //Snackbar gösterilmesi gerekirse yazılacak kodlar.
                Snackbar.make(view,"İzin vermeniz gerekmektedir.",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Snackbar gösterilerek izin istenecektir.
                        izinlerResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                //Snackbar gösterilmeden izin istenecek kodlar
                izinlerResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            //İzin var, galeriye git.
            //Snackbar gösterilmeden izin istenek kodlar
            Intent galeri=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galeryResultLauncher.launch(galeri);
        }
    }

    public void save(View view){

        String artName = binding.nameText.getText().toString();
        String painterName = binding.artistText.getText().toString();
        String year = binding.yearText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {

            database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY,artname VARCHAR, paintername VARCHAR, year VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO arts (artname, paintername, year, image) VALUES (?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,artName);
            sqLiteStatement.bindString(2,painterName);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();


        } catch (Exception e) {

        }
        Intent intent = new Intent(ArtActivity.this,MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

}