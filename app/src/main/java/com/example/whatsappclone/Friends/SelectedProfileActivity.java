package com.example.whatsappclone.Friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedProfileActivity extends AppCompatActivity {
    private String receiveruserid,senderorcurrentuserId,current_state;
    private CircleImageView  userprofileimage;
    private TextView userProfilename,userprofilestatus;
    private ImageView useronlinestatus;
    private DatabaseReference databaseReference,chatrequestreference,contactsreference,notificationreference;
    private Button sendfriendrequestbtn,rejectchatrequestbtn;
    private FirebaseAuth firebaseAuth;
    public RequestQueue requestQueue;

    private String URL="https://fcm.googleapis.com/fcm/send";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_profile);
        requestQueue= Volley.newRequestQueue(this);
        receiveruserid=getIntent().getExtras().get("selecteduserid").toString();
        FirebaseMessaging.getInstance().subscribeToTopic(receiveruserid);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        chatrequestreference=FirebaseDatabase.getInstance().getReference().child("Chat_requests");
        contactsreference=FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationreference=FirebaseDatabase.getInstance().getReference().child("Notifications");
        current_state="new";
        firebaseAuth=FirebaseAuth.getInstance();
        senderorcurrentuserId=firebaseAuth.getCurrentUser().getUid();
        //Toast.makeText(this, receiveruserid, Toast.LENGTH_SHORT).show();

        initiaizefields();
        gettheuserinfo();
    }

    private void gettheuserinfo() {
    databaseReference.child(receiveruserid).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if (snapshot.exists()&& snapshot.hasChild("image")){
                String userImage=snapshot.child("image").getValue().toString();
                String username=snapshot.child("username").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userprofileimage);
                userProfilename.setText(username);
                userprofilestatus.setText(status);
                managechatrequests();
            }else{
                String username="";
                String status="";
                 username=snapshot.child("username").getValue().toString();
                status=snapshot.child("status").getValue().toString();
                userProfilename.setText(username);
                userprofilestatus.setText(status);
                managechatrequests();

            }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });
    }

    private void managechatrequests() {
        chatrequestreference.child(senderorcurrentuserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiveruserid)){
                            String request_type=snapshot.child(receiveruserid).child("request_type").getValue().toString();
                            if (request_type.equals("sent")){
                                current_state="request_sent";
                                sendfriendrequestbtn.setText("Cancel chat request");
                           sendfriendrequestbtn.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
                             sendchatrequestnotification();
                            }
                            else if(request_type.equals("received")){
                                current_state="request_received";
                                sendfriendrequestbtn.setText("Accept chat request");
                                sendfriendrequestbtn.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
                              rejectchatrequestbtn.setVisibility(View.VISIBLE);
                              rejectchatrequestbtn.setEnabled(true);
                              rejectchatrequestbtn.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      cancelchatrequest();
                                  }
                              });
                            }
                        }else{
                                contactsreference.child(senderorcurrentuserId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                if (snapshot.hasChild(receiveruserid)){
                                                    current_state="friends";
                                                    sendfriendrequestbtn.setText("Remove this contact");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            if (!senderorcurrentuserId.equals(receiveruserid)){
                sendfriendrequestbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendfriendrequestbtn.setEnabled(false);
                        if (current_state.equals("new")){
                            sendchatrequest();
                        }if (current_state.equals("request_sent")){
                            cancelchatrequest();
                        }if (current_state.equals("request_received")){
                            Acceptchatrequest();
                        }
                        if (current_state.equals("friends")){
                            Removespecificcontact();
                        }

                    }
                });
            }else{
                sendfriendrequestbtn.setVisibility(View.INVISIBLE);

            }
    }

    private void sendchatrequestnotification() {
        JSONObject mainobj=new JSONObject();
        try {
           mainobj.put("to","/Users/"+receiveruserid );
           JSONObject notificationobject=new JSONObject();
            notificationobject.put("tile","any title");
            notificationobject.put("body","any body");
            mainobj.put("notification",notificationobject);
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL,
                    mainobj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SelectedProfileActivity.this, (CharSequence) error, Toast.LENGTH_SHORT).show();
                }
            }
            )
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
//                    return super.getHeaders();
                        Map<String,String> header=new HashMap<>();
                        header.put("content","application/json");
                        header.put("authorization","key=AAAAftFeVNs:APA91bEItr17_di1Zf85am_d86Nmw8ZKdgN1inHhhhJe5dmrs6td4c7YdScdhfb5go8NvVOfxh6TX4kULbQSmAaI5NRN177UVbzvD7W4NQ99FMVjTLmRkeB14Y4BnCNm6pDlpCP7YWa3");
                    return header;
                }
            };
            requestQueue.add(request);
                    ;
       }catch (Exception E){
               E.printStackTrace();
       }

    }

    private void Removespecificcontact() {
        contactsreference.child(senderorcurrentuserId).child(receiveruserid)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){
                    contactsreference.child(receiveruserid).child(senderorcurrentuserId).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        sendfriendrequestbtn.setEnabled(true);
                                        current_state="new";
                                        sendfriendrequestbtn.setText("Send chat request");
                                        sendfriendrequestbtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                                        rejectchatrequestbtn.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void Acceptchatrequest() {
     contactsreference.child(senderorcurrentuserId).child(receiveruserid)
             .child("Contacts").setValue("Saved")
     .addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull @NotNull Task<Void> task) {
             if (task.isSuccessful()){
                 chatrequestreference.child(receiveruserid).child(senderorcurrentuserId).child("Contacts")
                         .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull @NotNull Task<Void> task) {
                      if (task.isSuccessful()){
              chatrequestreference.child(senderorcurrentuserId).child(receiveruserid)
                      .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            chatrequestreference.child(receiveruserid).child(senderorcurrentuserId)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    sendfriendrequestbtn.setEnabled(true);
                                    current_state="friends";
                                    sendfriendrequestbtn.setText("Remove this contact");
                                    rejectchatrequestbtn.setVisibility(View.INVISIBLE);
                                    rejectchatrequestbtn.setEnabled(false);
                                }
                            });
                        }
                  }

              });

                      }else{
                          Toast.makeText(SelectedProfileActivity.this, "failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                      }
                     }
                 });
             }else{
                 Toast.makeText(SelectedProfileActivity.this, "failed:" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
             }
         }
     });
    }

    private void cancelchatrequest() {
    chatrequestreference.child(senderorcurrentuserId).child(receiveruserid)
            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull @NotNull Task<Void> task) {
            if (task.isSuccessful()){
                chatrequestreference.child(receiveruserid).child(senderorcurrentuserId).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()){
                             sendfriendrequestbtn.setEnabled(true);
                             current_state="new";
                             sendfriendrequestbtn.setText("Send chat request");
                             sendfriendrequestbtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                             rejectchatrequestbtn.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        }
    });
    }

    private void sendchatrequest() {

    chatrequestreference.child(senderorcurrentuserId).child(receiveruserid)
            .child("request_type")
            .setValue("sent")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()){
                        chatrequestreference.child(receiveruserid).child(senderorcurrentuserId)
                                .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()){
                                   //send a chat notification
                                    HashMap<String,String> chatnotification=new HashMap<>();
                                    chatnotification.put("from",senderorcurrentuserId);
                                    chatnotification.put("type","request");
                                    notificationreference.child(receiveruserid).push()
                                            .setValue(chatnotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendfriendrequestbtn.setEnabled(true);
                                                current_state="request_sent";
                                                sendfriendrequestbtn.setText("Cancel chat request");
                                                sendfriendrequestbtn.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
                                                // sendfriendrequestbtn.setRawInputType(R.drawable.borderradius);

                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }
                }
            });

    }

    private void initiaizefields() {
       userprofileimage=findViewById(R.id.visit_profile_image);
       userProfilename=findViewById(R.id.visit_username);
       userprofilestatus=findViewById(R.id.visit_status);
       sendfriendrequestbtn=findViewById(R.id.send_friendrequestbutton);
        rejectchatrequestbtn=findViewById(R.id.reject_friendrequestbutton);
    }
}