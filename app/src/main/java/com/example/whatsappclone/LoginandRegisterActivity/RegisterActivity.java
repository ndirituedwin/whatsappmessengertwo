package com.example.whatsappclone.LoginandRegisterActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {
private Button registerbutton;
private EditText registeremail,registerpassword;
private FirebaseAuth firebaseAuth;
private TextView alreadyhavaeanaccount;
private ProgressDialog progressDialog;
private DatabaseReference rootreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //initialize fireaseauth
        firebaseAuth=FirebaseAuth.getInstance();
        rootreference= FirebaseDatabase.getInstance().getReference();

           initializecontrols();
           //if user already have an account
        alreadyhavaeaccount();
        //if user clicks on register button;
        registerbuttonclicked();
    }

    private void registerbuttonclicked() {
     registerbutton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             //then create a new account;
             createanewaccount();
         }
     });
    }

    private void createanewaccount() {
      String enteredemail=registeremail.getText().toString().toLowerCase().trim();

        String enteredpassword=registerpassword.getText().toString();
      if (TextUtils.isEmpty(enteredemail) ||TextUtils.isEmpty(enteredpassword)){
          Toast.makeText(this,"All details are required",Toast.LENGTH_SHORT).show();
      }else{
          progressDialog.setTitle("Creating a new Account");
          progressDialog.setMessage("Will be through in a moment...");
          progressDialog.setCanceledOnTouchOutside(false);
          progressDialog.show();
          firebaseAuth.createUserWithEmailAndPassword(enteredemail,enteredpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                  if (task.isSuccessful()){
                      String devicetoken= FirebaseMessaging.getInstance().getToken().toString();
                      String currentuserid=firebaseAuth.getCurrentUser().getUid();
                      rootreference.child("Users").child(currentuserid).setValue("");
                      rootreference.child("Users").child(currentuserid).child("device_token").setValue(devicetoken);
                      Toast.makeText(RegisterActivity.this,"Account for user "+enteredemail+" created successsfully",Toast.LENGTH_SHORT).show();
                     progressDialog.dismiss();
                     sendthemtomainactivity();
                  }else{
                      Toast.makeText(RegisterActivity.this,"failed: "+task.getException().toString(),Toast.LENGTH_SHORT).show();
                  progressDialog.dismiss();
                  }
              }
          });

      }
    }

    private void alreadyhavaeaccount() {
    alreadyhavaeanaccount.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if they have an account, send them to login activity
            loginactivity();
        }
    });
    }

    private void loginactivity() {
        Intent loginintent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginintent);
    }

    private void sendthemtomainactivity() {

        Intent mainactivityintent=new Intent(RegisterActivity.this, MainActivity.class);
        mainactivityintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainactivityintent);
        finish();
   
    }

    private void initializecontrols() {
    registerbutton=findViewById(R.id.register_button);
    registeremail=findViewById(R.id.register_email);
    registerpassword=findViewById(R.id.register_password);
    alreadyhavaeanaccount=findViewById(R.id.already_have_an_account);
    progressDialog=new ProgressDialog(this);
    }
}