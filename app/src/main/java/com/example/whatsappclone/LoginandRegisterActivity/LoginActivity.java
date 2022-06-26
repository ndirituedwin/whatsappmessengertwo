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
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
  private Button loginbutton, phoneloginbutton;
  private EditText enterloginemail,enterloginpassword;
  private TextView needanewaccountlink,forgotpasswordlink;
  private FirebaseAuth firebaseAuth;
  private ProgressDialog progressDialog;
  private DatabaseReference usersreference;
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
      usersreference= FirebaseDatabase.getInstance().getReference().child("Users");

      initializecontrols();
        //create a new account
        needanewaccount();
        //when a user clicks on the login button
        loginbuttonclicked();
        phoneloginbuttonclicked();
    }

    private void phoneloginbuttonclicked() {
   phoneloginbutton.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           Intent phoneloginintent=new Intent(LoginActivity.this,PhoneLoginActivity.class);
           startActivity(phoneloginintent);
       }
   });
  }

    private void loginbuttonclicked() {
       loginbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               signintheuser();
           }
       });
    }

    private void signintheuser() {
    String enteredemail=enterloginemail.getText().toString().toLowerCase().trim();
    String enteredpassword=enterloginpassword.getText().toString();
    if (TextUtils.isEmpty(enteredemail)||TextUtils.isEmpty(enteredpassword)){
        Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
    }else{
        progressDialog.setTitle("Signining in "+enteredemail);
        progressDialog.setMessage("Hold on....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(enteredemail,enteredpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String currentuserId=firebaseAuth.getCurrentUser().getUid();
                    String devicetoken= FirebaseMessaging.getInstance().getToken().toString();

                    usersreference.child(currentuserId).child("device_token")
                            .setValue(devicetoken).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Log.d("devicetoken",devicetoken);

                                   Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                   progressDialog.dismiss();
                                   sendthemtomainactivity();
                               }
                        }
                    });

                }else{
                progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Failed : "+ task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    }

    private void sendthemtomainactivity() {

      Intent mainactivityintent=new Intent(LoginActivity.this,MainActivity.class);
       mainactivityintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(mainactivityintent);
      finish();
  }

    private void needanewaccount() {
    needanewaccountlink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //send them to register activity
            sendthemtoregisteractivity();
        }
    });
    }

    private void sendthemtoregisteractivity() {
    Intent registerintent=new Intent(LoginActivity.this,RegisterActivity.class);
    startActivity(registerintent);
    }

    private void initializecontrols() {
        loginbutton=findViewById(R.id.login_button);
        phoneloginbutton=findViewById(R.id.login_with_mobile);
        enterloginemail=findViewById(R.id.login_email);
        enterloginpassword=findViewById(R.id.login_password);
        needanewaccountlink=findViewById(R.id.dont_have_an_account);
        forgotpasswordlink=findViewById(R.id.forgot_password_link);
         progressDialog=new ProgressDialog(this);
  }


}