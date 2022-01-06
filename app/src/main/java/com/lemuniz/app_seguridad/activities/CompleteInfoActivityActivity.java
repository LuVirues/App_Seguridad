package com.lemuniz.app_seguridad.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.lemuniz.app_seguridad.R;
import com.lemuniz.app_seguridad.models.User;
import com.lemuniz.app_seguridad.providers.AuthProvider;
import com.lemuniz.app_seguridad.providers.ImageProvider;
import com.lemuniz.app_seguridad.providers.UsersProvider;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteInfoActivityActivity extends AppCompatActivity {

    EditText mEditTextPersonName, mEditTextAP, mEditTextAM;
    TextInputEditText mTextInputEditTextEmail;
    Button mButtonConfirm;
    CircleImageView mCircleImagePhoto;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    Options mOptions;

    ArrayList <String> mReturnValues = new ArrayList<>();

    File mImageFile;
    String mName = "", mApellidop="", mApellidom="";

    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info_activity);

        mEditTextAP = findViewById(R.id.editTextAP);
        mEditTextAM = findViewById(R.id.editTextAM);
        mEditTextPersonName = findViewById(R.id.editTextPersonName);
        mButtonConfirm = findViewById(R.id.btnConfirm);
        mCircleImagePhoto = findViewById((R.id.circleImagePhoto));

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();

        mDialog = new ProgressDialog(CompleteInfoActivityActivity.this);
        mDialog.setTitle("Espere un momento");
        mDialog.setMessage("Guardando información");

        mOptions = Options.init()
                .setRequestCode(100)
                .setCount(1)
                .setFrontfacing(false)
                .setPreSelectedUrls(mReturnValues)
                .setSpanCount(4)
                .setMode(Options.Mode.All)
                .setVideoDurationLimitinSeconds(0)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/pix/images");

        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = mEditTextPersonName.getText().toString();
                mApellidop = mEditTextAP.getText().toString();
                mApellidom = mEditTextAM.getText().toString();

                if (!mName.equals("") && !mApellidop.equals("") && !mApellidom.equals("") && mImageFile != null){
                    saveImage();
                }else{
                    Toast.makeText(CompleteInfoActivityActivity.this, "Debe llenar todos los campos", Toast.LENGTH_LONG).show();
                }
            }
        });

        mCircleImagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPix();
            }
        });

    }

    private void startPix() {
        Pix.start(CompleteInfoActivityActivity.this,mOptions);
    }

    private void updateUserInfo(String url) {

            User user = new User();
            user.setNombre(mName);
            user.setApellidop(mApellidop);
            user.setApellidom(mApellidom);
            user.setId(mAuthProvider.getId());
            user.setImage(url);
            mUsersProvider.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    goToHomeActivity();
                }
            });
    }

    private void goToHomeActivity() {
        mDialog.dismiss();
        Toast.makeText(CompleteInfoActivityActivity.this, "Información actualizada correctamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CompleteInfoActivityActivity.this, ContainerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveImage() {
        mDialog.show();
        mImageProvider.save(CompleteInfoActivityActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mImageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            updateUserInfo(url);
                        }
                    });
                }else{
                    mDialog.dismiss();
                    Toast.makeText(CompleteInfoActivityActivity.this, "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mReturnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            mImageFile = new File(mReturnValues.get(0));
            mCircleImagePhoto.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       if (requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS){
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Pix.start(CompleteInfoActivityActivity.this, mOptions);
           }else{
               Toast.makeText(CompleteInfoActivityActivity.this, "Por favor, conceda los permisos para acceder a la cámara", Toast.LENGTH_SHORT).show();
           }
       }
    }
}