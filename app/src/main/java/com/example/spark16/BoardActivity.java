package com.example.spark16;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.example.spark16.SetupActivity.Gallery_Pick;


public class BoardActivity extends AppCompatActivity {

    private ImageView imgNarrowUp, imgNarrowDown, imgNarrowRight, imgNarrowLeft;
    private ImageView newUp;
    private ImageView imgArduino, imgServo, imgNema, imgUser;
    private ImageView fondo;
    private ViewGroup rootLayout;
    private Display display;
    private int lwidth, lheight;
    private int _xDelta;
    private int _yDelta;

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private Button UpdatePostButton, captureScreenShot, galleryButton;
    private EditText PostDescription;

    final private int PICK_IMAGE = 1;
    final private int SCREEN_SHOT = 2;
    private Uri imgUri;
    private File imagePath;
    private String Description;



    private StorageReference PostsImagesRefrence;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;

    public static final int WORKPLACE_H = 200;
    public static final int WORKPLACE_V = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);


        rootLayout = (ViewGroup) findViewById(R.id.view_root);
        fondo = (ImageView) findViewById(R.id.image_fondo);
        imgNarrowUp = (ImageView) findViewById(R.id.image_arrow_up);



        imgNarrowDown  = (ImageView) findViewById(R.id.image_arrow_down);
        imgNarrowRight = (ImageView) findViewById(R.id.image_arrow_right);
        imgNarrowLeft = (ImageView) findViewById(R.id.image_arrow_up);
        imgArduino = (ImageView) findViewById(R.id.image_arduino);
        imgServo = (ImageView) findViewById(R.id.image_servo);
        imgNema = (ImageView) findViewById(R.id.image_nema);

        captureScreenShot = (Button) findViewById(R.id.capture_sol);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        galleryButton = (Button) findViewById(R.id.gallery);

        display = getWindowManager().getDefaultDisplay();
        lwidth = display.getWidth();
        lheight = display.getHeight();


        imgNarrowUp.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        imgNarrowUp.setX(lwidth-120);
        imgNarrowUp.setY(20);
        imgNarrowUp.setOnTouchListener(new ChoiceTouchListener());
        newUp= new ImageView(getApplicationContext());
        newUp.setImageBitmap(((BitmapDrawable) imgNarrowUp.getDrawable()).getBitmap());
        newUp.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        newUp.setX(lwidth-120);
        newUp.setY(20);
        rootLayout.addView(newUp);

        imgNarrowDown.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        imgNarrowDown.setX(lwidth-60);
        imgNarrowDown.setY(20);
        imgNarrowDown.setOnTouchListener(new ChoiceTouchListener());

        imgNarrowRight.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        imgNarrowRight.setX(lwidth-120);
        imgNarrowRight.setY(60);
        imgNarrowRight.setOnTouchListener(new ChoiceTouchListener());

        imgNarrowLeft.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        imgNarrowLeft.setX(lwidth-60);
        imgNarrowLeft.setY(60);
        imgNarrowLeft.setOnTouchListener(new ChoiceTouchListener());

        imgArduino.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        imgArduino.setX(lwidth-120);
        imgArduino.setY(90);
        imgArduino.setOnTouchListener(new ChoiceTouchListener());

        imgServo.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        imgServo.setX(lwidth-60);
        imgServo.setY(90);
        imgServo.setOnTouchListener(new ChoiceTouchListener());

        imgNema.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        imgNema.setX(lwidth-120);
        imgNema.setY(120);
        imgNema.setOnTouchListener(new ChoiceTouchListener());


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(WORKPLACE_H, WORKPLACE_V);
        layoutParams.setMargins(0,100,0,0);
        fondo.setLayoutParams(layoutParams);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostsImagesRefrence = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        PostDescription =(EditText) findViewById(R.id.post_description);
        loadingBar = new ProgressDialog(this);
/*

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });

        captureScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takeScreenshot();
                } else {
                    // Request permission from the user
                    ActivityCompat.requestPermissions(BoardActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

            }
        });
        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ValidatePostInfo();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                takeScreenshot();
        }
    }



    private void takeScreenshot() {

        try {
            // image naming and path  to include sd card  appending name you choose for file

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache(),0,100,WORKPLACE_H,WORKPLACE_V);
            v1.setDrawingCacheEnabled(false);


            FileOutputStream outputStream = new FileOutputStream(new File(getExternalFilesDir(null),"screen"));
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

            String imageFileName = now.toString() + "_";
            File mFileTemp = null;
            String root=BoardActivity.this.getDir("my_dir",Context.MODE_PRIVATE).getAbsolutePath();
            File myDir = new File(root + "/Img");
            if(!myDir.exists()){
                myDir.mkdirs();
            }
            try {
                mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if (mFileTemp != null) {
                FileOutputStream fout;
                try {
                    fout = new FileOutputStream(mFileTemp);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 70, fout);
                    fout.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imgUri=Uri.fromFile(mFileTemp);
            }

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }


        Log.d("uri de la imagen",imgUri.getLastPathSegment().toString());

        //imgUri = Uri.fromFile(new File(mPath));

        //Uri u = Uri.parse(imgUri.toString());
        //StoringImageToFirebaseStorage(imgUri);
    }


    private void openGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data!=null) {
            imgUri = data.getData();
            Log.d("galery result", imgUri.toString());

        }

    }

    private final class ChoiceTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    break;
            }
            rootLayout.invalidate();
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }



    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(BoardActivity.this, MainActivity.class);
        startActivity(mainIntent);


    }


    private void StoringImageToFirebaseStorage(Uri imageUri)
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        Log.d("uri", imageUri.getLastPathSegment()+postRandomName+".jpg");


        StorageReference filePath = PostsImagesRefrence.child("Post Images").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(BoardActivity.this, "imagen cargada con éxito en Almacenamiento ...", Toast.LENGTH_SHORT).show();
                    SavingPostInformationToDatabase();

                }
                else
                {
                    String message = task.getException().getMessage();
                    Log.e("imagen",message);
                    Toast.makeText(BoardActivity.this, "se produjo un error " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingPostInformationToDatabase()
    {


        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage = "" ;
                    if (dataSnapshot.child("profileimage").exists()) {
                        userProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    }
                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("postimage", downloadUrl);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("fullname", userFullName);
                    PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(BoardActivity.this, "La nueva publicación se ha actualizado con éxito.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(BoardActivity.this, "Se produjo un error al actualizar tu publicación.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ValidatePostInfo()
    {
        Description = PostDescription.getText().toString();

        if(imgUri == null)
        {
            Toast.makeText(this, "Seleccione la imagen del mensaje ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Por favor, di algo sobre tu imagen ...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setCanceledOnTouchOutside(true);
            StoringImageToFirebaseStorage(imgUri);
            SendUserToMainActivity();
        }
    }

}
