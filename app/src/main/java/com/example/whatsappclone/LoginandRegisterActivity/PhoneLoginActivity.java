package com.example.whatsappclone.LoginandRegisterActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
  private Button sendverificationcode,verificationcodebtn;
  private EditText entermobile,enterverificationcode;
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
  private String  mverificationId,phone;
  private FirebaseAuth firebaseAuth;
  private ProgressDialog progressDialog;
  private PhoneAuthProvider.ForceResendingToken mresendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        firebaseAuth=FirebaseAuth.getInstance();
        initializecontrols();
        //when sendverificationcodebtn is clickes
        sendverificationcodebtnclicked();
    }

    private void sendverificationcodebtnclicked() {
    sendverificationcode.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             phone=entermobile.getText().toString();
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(PhoneLoginActivity.this,"You must enter mobile number",Toast.LENGTH_SHORT).show();
            }else{
                progressDialog.setTitle("Verifying mobile "+phone);
                progressDialog.setMessage("Hold on while we verify you phone..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phone,
                        60,
                        TimeUnit.SECONDS,
                        PhoneLoginActivity.this,
                        callbacks);
            }
        }
    });
    verificationcodebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           sendverificationcode.setVisibility(View.INVISIBLE);
           entermobile.setVisibility(View.INVISIBLE);
           String verificationcode=enterverificationcode.getText().toString();
           if (TextUtils.isEmpty(verificationcode)){
               Toast.makeText(PhoneLoginActivity.this,"enter sent code",Toast.LENGTH_SHORT).show();
           }else{
               progressDialog.setTitle("Verifying code "+verificationcode);
               progressDialog.setMessage("Hold on while we verify you code..");
               progressDialog.setCanceledOnTouchOutside(false);
               progressDialog.show();
               PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mverificationId,verificationcode);
               SignInWithPhoneAuthCredentials(credential);
           }
        }
    });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
               SignInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
               // Log.d("TAGGG","onverificationfailed",e);
                progressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this,"failed: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                //make the buttons visible
                sendverificationcode.setVisibility(View.VISIBLE);
                entermobile.setVisibility(View.VISIBLE);
                verificationcodebtn.setVisibility(View.INVISIBLE);
                enterverificationcode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mverificationId=s;
                mresendToken=forceResendingToken;
                progressDialog.dismiss();
           Toast.makeText(PhoneLoginActivity.this,"verification code sent to"+phone,Toast.LENGTH_LONG).show();
           //then hide the buttons after code is sent
                sendverificationcode.setVisibility(View.INVISIBLE);
                entermobile.setVisibility(View.INVISIBLE);
                verificationcodebtn.setVisibility(View.VISIBLE);
                enterverificationcode.setVisibility(View.VISIBLE);
            }
        };
    }
    private void SignInWithPhoneAuthCredentials(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   Toast.makeText(PhoneLoginActivity.this,"verification successful",Toast.LENGTH_LONG).show();
                   progressDialog.dismiss();
                   sendthemtomainactivity();
               }else{
                   Toast.makeText(PhoneLoginActivity.this,"failed:"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
               }
            }
        });
    }


    private void sendthemtomainactivity() {
        Intent sendthemtomainactivity=new Intent(PhoneLoginActivity.this, MainActivity.class);
        sendthemtomainactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendthemtomainactivity);
        finish();
    }

    private void initializecontrols() {
     sendverificationcode=findViewById(R.id.send_verification_codebutton);
     verificationcodebtn=findViewById(R.id.verify_codebtn);
     entermobile=findViewById(R.id.phone_number_input);
     enterverificationcode=findViewById(R.id.phone_number_verification);
     progressDialog=new ProgressDialog(this);

    }
}