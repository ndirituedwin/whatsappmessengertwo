package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.whatsappclone.Fragments.TabsAccessorAdapter;
import com.example.whatsappclone.Friends.FindFriendsActivity;
import com.example.whatsappclone.LoginandRegisterActivity.LoginActivity;
import com.example.whatsappclone.Settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Toolbar mainpagetoolbar;
  private ViewPager viewPager;
  private TabLayout mytabTabLayout;
  private TabsAccessorAdapter mytabTabsAccessorAdapter;
  private String currentUserId;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference();
        mainpagetoolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mainpagetoolbar);
        getSupportActionBar().setTitle("Kampus knekt");
       // currentUser=FirebaseAuth.getInstance().getCurrentUser();
        viewPager=(ViewPager) findViewById(R.id.main_tabs_pager);
        mytabTabsAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mytabTabsAccessorAdapter);
        mytabTabLayout=(TabLayout) findViewById(R.id.mainTabs);
        mytabTabLayout.setupWithViewPager(viewPager);



    }
    //checking if user is logged in
    @Override
    protected void onStart() {
        super.onStart();
       FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if (currentUser==null){
                //ifthey are not authenticated,send them to loginactivity
            sendthemtologinactivity();
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                UpdateUserStatus("online");
            }
            verifyuserexistance();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if (currentUser !=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                UpdateUserStatus("offline");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if (currentUser !=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                UpdateUserStatus("offline");
            }
        }
    }

    private void verifyuserexistance() {

        String currentUserId=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child("username").exists()){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }else{
                  sendthemtosettingsactivity();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendthemtologinactivity() {
        Intent loginactivity=new Intent(MainActivity.this, LoginActivity.class);
           loginactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginactivity);
    }
    //show options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
    if (item.getItemId()==R.id.main_logout_option){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            UpdateUserStatus("offline");
        }
        firebaseAuth.signOut();
         sendthemtologinactivity();
    }
    if (item.getItemId()==R.id.main_settings_option){
        sendthemtosettingsactivity();

    }
    if(item.getItemId()==R.id.main_find_friends_option){
        sendusertofindfriendsactivity();

        }
        if(item.getItemId()==R.id.main_create_group_option){
             Createanewgroup();
        }

    return true;
    }

    private void Createanewgroup() {

        AlertDialog.Builder createnewgroupdialog=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        createnewgroupdialog.setTitle("Enter group name");
        final EditText groupnamefield=new EditText(MainActivity.this);
        groupnamefield.setHint("Kampus connect");
        createnewgroupdialog.setView(groupnamefield);
        createnewgroupdialog.setIcon(R.drawable.group);
        createnewgroupdialog.setCancelable(false);
        createnewgroupdialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupname=groupnamefield.getText().toString();
           if (TextUtils.isEmpty(groupname)){
               Toast.makeText(MainActivity.this,"enter group name",Toast.LENGTH_SHORT).show();
           }else{
               //save group to database
               creategroup(groupname);
           }
            }
        });
        createnewgroupdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        createnewgroupdialog.show();

    }

    private void creategroup(String groupname) {
    databaseReference.child("Groups").child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull @NotNull Task<Void> task) {
            if (task.isSuccessful()){
                Toast.makeText(MainActivity.this,groupname+" group added",Toast.LENGTH_SHORT).show();
            }
        }
    });
    }

    private void sendthemtosettingsactivity() {

        Intent settingsactivity=new Intent(MainActivity.this,SettingsActivity.class);
        //settingsactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsactivity);
        //finish();
    }
    private void sendusertofindfriendsactivity() {

        Intent findfriendsactivity=new Intent(MainActivity.this, FindFriendsActivity.class);
        //findfriendsactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(findfriendsactivity);
        //finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void UpdateUserStatus(String state){
        String currentdate, currenttime;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            simpleDateFormat=new SimpleDateFormat("dd MMM, YYYY");
        }

        currentdate=simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat time=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            time=new SimpleDateFormat("hh:mm a");
        }
       currenttime=time.format(calendar.getTime());
        HashMap<String,Object> onlinestate=new HashMap<>();
        onlinestate.put("time",currenttime);
        onlinestate.put("date",currentdate);
        onlinestate.put("state",state);
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(currentUserId).child("userState").updateChildren(onlinestate);
    }


//    private void UploadData(){
//        progressDialog.setTitle("Profile Photo update");
//        progressDialog.setMessage("Hold on while we update your profile info...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//        StorageReference filepath=storagereference.child(constants.STORAGEREFERENCE).child(firebaseAuth.getUid()+".jpg");
//        filepath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(SettingsActivity.this, "Profile image  uploaded", Toast.LENGTH_SHORT).show();
//
//                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String imageuri=uri.toString();
//                            String curretuser=firebaseAuth.getCurrentUser().getUid();
//                            databasereference.child(constants.USERS_PATH).child(firebaseAuth.getUid()).child(constants.USERPROFILEPHOTO).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    //  Toast.makeText(SettingsActivity.this, "Image successfully saved to database", Toast.LENGTH_SHORT).show();
//                                    upload();
//
//                                    progressDialog.dismiss();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull @NotNull Exception e) {
//                                    Toast.makeText(SettingsActivity.this,"failed to save image to database"+e,Toast.LENGTH_SHORT).show();
//                                    progressDialog.dismiss();
//                                }
//                            });
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull @NotNull Exception e) {
//                            Toast.makeText(SettingsActivity.this, "failed to upload image "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//
//
//                    progressDialog.dismiss();
//                }else{
//
//                    Toast.makeText(SettingsActivity.this, "failed:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//
//            }
//        });
//    }

}