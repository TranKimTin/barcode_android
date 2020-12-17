package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.barcode.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private Button btnSendCode;
    private EditText edtPhoneNumber, edtOtp;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String TAG = "LOGIN_ACTIVITY";
    private String mVerificationId;

    @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser()!=null){
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("phoneNumber", mAuth.getCurrentUser().getPhoneNumber());
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Có lỗi xảy ra kh gửi mã xác nhận", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
                Toast.makeText(getApplicationContext(), "Đã gửi mã xác nhận", Toast.LENGTH_SHORT).show();
                btnSendCode.setOnClickListener(null);
                new CountDownTimer(60000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        btnSendCode.setText("Thử lại sau " + millisUntilFinished / 1000 + " giây");
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        btnSendCode.setText("Gửi mã OTP");
                        btnSendCode.setOnClickListener(LoginActivity.this);
                    }

                }.start();
            }
        };
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSendCode = (Button) findViewById(R.id.btnSendCode);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtOtp = (EditText) findViewById(R.id.edtOtp);

        btnLogin.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("phoneNumber", user.getPhoneNumber());
                            startActivity(i);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Mã xác thực không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if(edtOtp.getText() == null || edtOtp.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(), "Chưa nhập mã xác nhận", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mVerificationId == null || mVerificationId.equals("")){
                    Toast.makeText(getApplicationContext(), "Chưa gửi mã xác nhận", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = edtOtp.getText().toString().trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
                break;
            case R.id.btnSendCode:
                if (edtPhoneNumber.getText() == null || edtPhoneNumber.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    return;
                }
                String phoneNumber = edtPhoneNumber.getText().toString();
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+84" + phoneNumber)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
                break;
        }
    }
}