package com.example.whatsappclone.Group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.whatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupActivity extends AppCompatActivity {
   private ImageView sendgroupmessage;
   private Toolbar toolbar;
   private EditText entergroupmessage;
   private ScrollView scrollView;
   private TextView displaygrouptexts;
   private String currentgroupname,currentuserId,currentusername,currentdate, currenttime;
   private FirebaseAuth firebaseAuth;
   private DatabaseReference databaseReference,grouprefernce,groupMessageKeyref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        currentgroupname=getIntent().getExtras().get("groupName").toString();
       // Toast.makeText(this,currentgroupname,Toast.LENGTH_SHORT).show();
        grouprefernce=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentgroupname);
        currentuserId=firebaseAuth.getCurrentUser().getUid();

        initializefields();
        getuserinfo();
        //when sendmessagebtn is clicked;
        sendgroupmessagebtnclicked();
    }

    @Override
    protected void onStart() {
        super.onStart();
        grouprefernce.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                if (snapshot.exists()){
                    displaymessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void displaymessages(DataSnapshot snapshot) {
        Iterator iter=snapshot.getChildren().iterator();
        while (iter.hasNext()){
            String chatDate=(String) ((DataSnapshot)iter.next()).getValue();
            String chatmessage=(String) ((DataSnapshot)iter.next()).getValue();
            String chattime=(String) ((DataSnapshot)iter.next()).getValue();
            String chatname=(String) ((DataSnapshot)iter.next()).getValue();
            displaygrouptexts.append(chatname+ "\n "+chatmessage+ "\n"+ chatDate+" : "+chattime+"\n\n");
            displaygrouptexts.setTextColor(getResources().getColor(R.color.purple_200));
            //displaygrouptexts.setBackground(Drawable.createFromPath("#ffffff"));
           scrollView.fullScroll(scrollView.FOCUS_DOWN);
        }
    }

    private void sendgroupmessagebtnclicked() {
     sendgroupmessage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             savemessagetodatabase();
            entergroupmessage.setText("");
             scrollView.fullScroll(scrollView.FOCUS_DOWN);
         }
     });
    }

    private void savemessagetodatabase() {
        String text=entergroupmessage.getText().toString();
        String groupnamekey=grouprefernce.push().getKey();
        if(TextUtils.isEmpty(text)){
            Toast.makeText(GroupActivity.this, "message required", Toast.LENGTH_SHORT).show();
        }else{
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                simpleDateFormat=new SimpleDateFormat("dd MMM, YYYY");
            }
            currentdate=simpleDateFormat.format(calendar.getTime());
            Calendar calfortime=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
            currenttime=dateFormat.format(calfortime.getTime());
            HashMap<String, Object> groupmessage=new HashMap<>();
            grouprefernce.updateChildren(groupmessage);
            groupMessageKeyref=grouprefernce.child(groupnamekey);
            HashMap<String,Object>messageinfomap=new HashMap<>();
            messageinfomap.put("username",currentusername);
            messageinfomap.put("message",text);
            messageinfomap.put("date",currentdate);
            messageinfomap.put("time",currenttime);
            groupMessageKeyref.updateChildren(messageinfomap);
        }
    }

    private void getuserinfo() {
         FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserId).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                     currentusername=snapshot.child("username").getValue().toString();

                    // Toast.makeText(GroupActivity.this,currentusername+" exists"+currentgroupname,Toast.LENGTH_SHORT).show();

                 }else{
               Toast.makeText(GroupActivity.this,"user not found",Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onCancelled(@NonNull @NotNull DatabaseError error) {

             }
         });
    }

    private void initializefields() {
    toolbar=findViewById(R.id.group_chatbar_layout);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(currentgroupname);
    sendgroupmessage=findViewById(R.id.send_group_message);
    entergroupmessage=findViewById(R.id.input_group_message);
    scrollView=findViewById(R.id.specificgroup_scroolview);
    displaygrouptexts=findViewById(R.id.group_chat_textdisplay);
    }
}