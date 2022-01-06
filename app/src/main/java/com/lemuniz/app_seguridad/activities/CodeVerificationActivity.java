package com.lemuniz.app_seguridad.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lemuniz.app_seguridad.R;
import com.lemuniz.app_seguridad.models.User;
import com.lemuniz.app_seguridad.providers.AuthProvider;
import com.lemuniz.app_seguridad.providers.UsersProvider;

public class CodeVerificationActivity extends AppCompatActivity {

    Button mButtomCodeVerification;
    EditText mEditTextCode;
    TextView mTextViewSMS;
    ProgressBar mProgressBar;

    String mExtraPhone;
    String mVerificationId;

    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mTextViewSMS = findViewById(R.id.textViewSMS);
        mProgressBar = findViewById(R.id.progressBar);
        mButtomCodeVerification = findViewById(R.id.btnCodeVerification);
        mEditTextCode = findViewById(R.id.editTextCodeVerification);

        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        mExtraPhone = getIntent().getStringExtra("phone");

        mAuthProvider.sendCodeVerification(mExtraPhone, mCallbacks);

        mButtomCodeVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mEditTextCode.getText().toString();
                if(!code.equals("") && code.length() >= 6){
                    signIn(code);
                }else{
                    Toast.makeText(CodeVerificationActivity.this, "Ingrese el código", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility((View.GONE));

            String code = phoneAuthCredential.getSmsCode();

            if(code != null){

                mEditTextCode.setText(code);
                signIn(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility((View.GONE));

            Toast.makeText(CodeVerificationActivity.this, "Se prrodujo un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            Toast.makeText(CodeVerificationActivity.this, "Código enviado", Toast.LENGTH_SHORT).show();
            mVerificationId = verificationId;
        }
    };

    private void signIn(String code){

        mAuthProvider.signInPhone(mVerificationId, code).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    final User user = new User();
                    user.setId(mAuthProvider.getId());
                    user.setPhone(mExtraPhone);

                    mUsersProvider.getUserInfo(mAuthProvider.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (!documentSnapshot.exists()){

                                mUsersProvider.create(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        goToCompleteInfo();
                                    }
                                });
                            }else if (documentSnapshot.contains("nombre") && documentSnapshot.contains("apellidop") && documentSnapshot.contains("apellidom") && documentSnapshot.contains("image")){
                                String name = documentSnapshot.getString("nombre");
                                String apeP = documentSnapshot.getString("apellidop");
                                String apeM = documentSnapshot.getString("apellidom");
                                String image = documentSnapshot.getString("image");

                                if (name != null && apeP != null && apeM != null && image !=null){
                                    if (!name.equals("") && !apeP.equals("") && !apeM.equals("") && !image.equals("")){
                                        goToHomeActivity();
                                    }else{
                                        goToCompleteInfo();
                                    }
                                }else{
                                    goToCompleteInfo();
                                }
                            }
                        }
                    });


                }
                else{
                    Toast.makeText(CodeVerificationActivity.this, "No se puedo realizar la autentificación", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void goToHomeActivity() {
        Intent intent = new Intent(CodeVerificationActivity.this, ContainerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToCompleteInfo(){
        Intent intent = new Intent(CodeVerificationActivity.this, CompleteInfoActivityActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}