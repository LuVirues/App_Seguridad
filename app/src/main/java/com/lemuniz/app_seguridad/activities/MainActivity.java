package com.lemuniz.app_seguridad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.lemuniz.app_seguridad.R;
import com.lemuniz.app_seguridad.providers.AuthProvider;

public class MainActivity extends AppCompatActivity {

    Button mButtonGoToSendCode;
    CountryCodePicker mCountryCode;
    EditText mEditTextPhone;

    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCountryCode = findViewById(R.id.ccp);
        mEditTextPhone = findViewById(R.id.editTextPhone);

        mAuthProvider = new AuthProvider();


        mButtonGoToSendCode = findViewById(R.id.btnSendCode);
        mButtonGoToSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
                //goToCodeVerificationActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuthProvider.getSessionUser() != null){
            Intent intent = new Intent(MainActivity.this, ContainerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private  void getData(){

        String code = mCountryCode.getSelectedCountryCodeWithPlus();
        String phone = mEditTextPhone.getText().toString();

        if (phone.equals("")){
            Toast.makeText(this, "Debe ingresar un número de télefono", Toast.LENGTH_SHORT).show();
        }else{
            goToCodeVerificationActivity(code + phone);
        }

    }

    private void goToCodeVerificationActivity(String phone){
        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);
    }
}