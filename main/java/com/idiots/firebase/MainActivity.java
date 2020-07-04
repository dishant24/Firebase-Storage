package com.idiots.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private static final int Image_Request=1;
    private Button btn_fileUpload;
    private Button btn_chooseFile;
    private EditText edit_File;
    private TextView showPhoto;
    private ProgressBar mProgressBar;
    private Uri ImageUri;
    private ImageView mImageView;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask mStorageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_chooseFile = findViewById(R.id.button_choose);
        btn_fileUpload = findViewById(R.id.button_Upload);
        edit_File = findViewById(R.id.editText);
        showPhoto = findViewById(R.id.textView_show);
        mProgressBar = findViewById(R.id.progressBar);
        mImageView = findViewById(R.id.imageView);


        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        btn_fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStorageTask !=null && mStorageTask.isInProgress()){

                }else
                    UploadFile();

            }
        });
        btn_chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });
        showPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagesActivity();
            }
        });
    }
    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,Image_Request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Image_Request && resultCode == RESULT_OK
            && data != null && data.getData() != null){
            ImageUri = data.getData();
            Glide.with(this).load(ImageUri).fitCenter().into(mImageView);

        }

    }
    private String getFileExtension(Uri uri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }
    private void UploadFile(){
        if(ImageUri != null){
            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(ImageUri));
          mStorageTask =  fileReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            },1000);
                            Upload upload = new Upload(edit_File.getText().toString().trim(),taskSnapshot.getUploadSessionUri().toString());
                            String UploadId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(UploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }else {

        }

    }
    private void openImagesActivity(){
        Intent i = new Intent(this,ImageActivity.class);
        startActivity(i);
    }

}
