package com.example.whatsappclone.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsappclone.LoginandRegisterActivity.LoginActivity;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
   private Button updateprofilebutton;
   private EditText updateusername,updatestatus;
   private CircleImageView updateprofilephoto;
   private String currentUserId;
   private FirebaseAuth firebaseAuth;
   private DatabaseReference databaseReference;
   private static  final int gallerypick=1;
   private StorageReference userprofileimagereference;
   private ProgressDialog progressDialog;
   private Toolbar settingstoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        userprofileimagereference= FirebaseStorage.getInstance().getReference().child("Profile_images");
        initializesettings();

        //when update profile button is clicked
          updateprofilebuttonclicked();
          retrivecurrentuserinfo();
          //when profile photo is clicked
          updateprofilephotoclicked();
    }

    private void updateprofilephotoclicked() {
     updateprofilephoto.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent galleryintent=new Intent();
             galleryintent.setAction(Intent.ACTION_GET_CONTENT);
             galleryintent.setType("image/*");
             startActivityForResult(galleryintent, gallerypick);

         }
     });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallerypick && resultCode==RESULT_OK && data !=null){
            Uri imageuri=data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK){
                progressDialog.setTitle("Profile Photo update");
                progressDialog.setMessage("Hold on while we update your profile photo...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                Uri resulturi=result.getUri();
                StorageReference  filepath=userprofileimagereference.child(currentUserId+".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                   if (task.isSuccessful()){
                       Toast.makeText(SettingsActivity.this, "Profile image successfully uploaded", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                   }else{

                       Toast.makeText(SettingsActivity.this, "failed:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   progressDialog.dismiss();
                   }
                   task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           String imageuri=uri.toString();
//                                    HashMap<String,Object> hashma=new HashMap<>();
//                                    hashma.put("image",uri.toString());
                           databaseReference.child("Users").child(currentUserId).child("image").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void unused) {
                                   Toast.makeText(SettingsActivity.this, "Image successfully saved", Toast.LENGTH_SHORT).show();
                                   progressDialog.dismiss();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull @NotNull Exception e) {
                                   Toast.makeText(SettingsActivity.this,"failed"+e,Toast.LENGTH_SHORT).show();
                             progressDialog.dismiss();
                               }
                           });
                       }
                   });
                    }
                });
            }
        }
    }

    private void retrivecurrentuserinfo() {
      databaseReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
              if ((snapshot.exists())&& (snapshot.hasChild("username")) && (snapshot.hasChild("image"))){
                String username=snapshot.child("username").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                String image=snapshot.child("image").getValue().toString();
                 updateusername.setText(username);
                 updatestatus.setText(status);
                  Picasso.get().load(image).placeholder(R.drawable.profile_image).into(updateprofilephoto);

              }else if (snapshot.exists()&&snapshot.hasChild("username")){
                  String username=snapshot.child("username").getValue().toString();
                  String status=snapshot.child("status").getValue().toString();
                  updateusername.setText(username);
                  updatestatus.setText(status);
              }else{
                  Toast.makeText(SettingsActivity.this, "Update your profile", Toast.LENGTH_SHORT).show();
              }
          }

          @Override
          public void onCancelled(@NonNull @NotNull DatabaseError error) {

          }
      });
    }
    private void updateprofilebuttonclicked() {
     updateprofilebutton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String username=updateusername.getText().toString();
             String status=updatestatus.getText().toString();
             if (TextUtils.isEmpty(username)|| TextUtils.isEmpty(status)){
                 Toast.makeText(SettingsActivity.this,"one or more empty fields",Toast.LENGTH_SHORT).show();
             }else{
                 HashMap<String, Object> profilemap=new HashMap<>();
                 profilemap.put("uid",currentUserId);
                 profilemap.put("username",username);
                 profilemap.put("status",status);
                 databaseReference.child("Users").child(currentUserId).updateChildren(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull @NotNull Task<Void> task) {
                         if (task.isSuccessful()){
                             sendthemtomainactivity();
                             Toast.makeText(SettingsActivity.this,"Profile updated",Toast.LENGTH_SHORT).show();
                         }else{
                             Toast.makeText(SettingsActivity.this,"failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
             }
         }
     });
    }
    private void sendthemtomainactivity() {

        Intent mainactivityintent=new Intent(SettingsActivity.this, MainActivity.class);
        mainactivityintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainactivityintent);
        finish();
    }
    private void initializesettings() {
        updateprofilebutton=findViewById(R.id.update_profile);
        updateusername=findViewById(R.id.set_profile_name);
        updatestatus=findViewById(R.id.set_profile_status);
        updateprofilephoto=findViewById(R.id.set_profile_image);
        progressDialog=new ProgressDialog(this);
        settingstoolbar=(Toolbar) findViewById(R.id.settingssss_toolbar);
        setSupportActionBar(settingstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("profile settings");


    }
}