package com.example.whatsappclone.Chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String currentdate, currenttime;

    private TextView username, userlastseen;
  private CircleImageView userprofile,senderprofile;
  private DatabaseReference rootreference;
  private Toolbar chattoolbar;
  private FirebaseAuth firebaseAuth;
  private String messagereceiverid,messagesenderid,messagereceivername,messagereceiverimage;
  private ImageButton send_private_message_button, sendfilesbutton;
  private EditText messageinputtext;
  private final List<Messages> messagesList=new ArrayList<>();
  private LinearLayoutManager linearLayoutManager;
  private MessagesAdapter messagesAdapter;
  private RecyclerView private_message_list;
  private String checker="", myurl="";
  private StorageTask uploadtask;
  private Uri fileuri;
  private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseAuth=FirebaseAuth.getInstance();
        messagesenderid=firebaseAuth.getCurrentUser().getUid();
        rootreference= FirebaseDatabase.getInstance().getReference();
        messagereceiverid=getIntent().getExtras().get("visituserid").toString();
        messagereceivername=getIntent().getExtras().get("visitusername").toString();
        messagereceiverimage=getIntent().getExtras().get("image").toString();
        //Toast.makeText(this, messagereceiverid+messagereceivername, Toast.LENGTH_SHORT).show();
      initializefields();
        displayuserlastseen();
      username.setText(messagereceivername);

      sendfilebtnclicked();





        Picasso.get().load(messagereceiverimage).placeholder(R.drawable.profile_image).into(userprofile);

        send_private_message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                       sendmessage();
            }
        });
    }

    private void sendfilebtnclicked() {
      sendfilesbutton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              CharSequence  options[]=new CharSequence[]
                      {
                              "Images",
                              "PDF",
                              "MS word files"
                      };
              AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
              builder.setTitle("Select file");
              builder.setItems(options, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      if (which == 0) {
                          checker = "image";
                          Intent intent = new Intent();
                          intent.setAction(Intent.ACTION_GET_CONTENT);
                          intent.setType("image/*");
                          startActivityForResult(intent.createChooser(intent, "select image"), 438);

                      }
                      if (which == 1) {
                          checker = "pdf";
                          Intent intent = new Intent();
                          intent.setAction(Intent.ACTION_GET_CONTENT);
                          intent.setType("application/pdf");
                          startActivityForResult(intent.createChooser(intent, "select PDF file"), 438);

                      }
                      if (which == 2) {
                          checker = "docx";
                          Intent intent = new Intent();
                          intent.setAction(Intent.ACTION_GET_CONTENT);
                          intent.setType("application/msword");
                          startActivityForResult(intent.createChooser(intent, "select docx/msword file"), 438);

                      }

                  }


              });
              builder.show();
          }
      });
    }

    private void sendmessage() {
       String messagetext=messageinputtext.getText().toString();
       if (TextUtils.isEmpty(messagetext)){
           Toast.makeText(this, "enter message", Toast.LENGTH_SHORT).show();
       }else{
           String messagesenderref="Messages/" + messagesenderid + "/" + messagereceiverid;
           String messagereceiverref="Messages/" + messagereceiverid + "/"+ messagesenderid;
           DatabaseReference userMessagekeyref=rootreference.child("Messages").
                   child(messagesenderid).child(messagereceiverid).push();

           String messagepushid=userMessagekeyref.getKey();
           Map messageTextBody=new HashMap();
           messageTextBody.put("message",messagetext);
           messageTextBody.put("type","text");
           messageTextBody.put("from",messagesenderid);
           messageTextBody.put("to",messagereceiverid);
           messageTextBody.put("messageID",messagepushid);
           messageTextBody.put("time",currenttime);
           messageTextBody.put("date",currentdate);
           Map messageBodyDetails=new HashMap();
           messageBodyDetails.put(messagesenderref +"/"+ messagepushid,messageTextBody);
           messageBodyDetails.put(messagereceiverref +"/"+ messagepushid,messageTextBody);
              rootreference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                  @Override
                  public void onComplete(@NonNull @NotNull Task task) {
                      if (task.isSuccessful()){
                          Toast.makeText(ChatActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                      }else{
                          Toast.makeText(ChatActivity.this, "failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                      }
                      messageinputtext.setText("");
                  }
              });
       }
    }


    private void initializefields(){
        send_private_message_button=findViewById(R.id.send_private_message_button);
        sendfilesbutton=findViewById(R.id.send_files_button);
        messageinputtext=findViewById(R.id.enter_private_message);
           chattoolbar=(Toolbar) findViewById(R.id.chat_toolbar);
           setSupportActionBar(chattoolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarview=layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionbarview);
        username=findViewById(R.id.custom_profile_name);
        userlastseen=findViewById(R.id.custom_user_last_seen);
        userprofile=findViewById(R.id.custom_profile_image);
        senderprofile=findViewById(R.id.sender_profile_image);
         progressDialog=new ProgressDialog(this);

        messagesAdapter=new MessagesAdapter(messagesList);
        private_message_list=findViewById(R.id.private_message_list);
         linearLayoutManager=new LinearLayoutManager(this);
        private_message_list.setLayoutManager(linearLayoutManager);
        private_message_list.setAdapter(messagesAdapter);
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode==438 && resultCode==RESULT_OK && data !=null && data.getData() !=null){

             progressDialog.setTitle("sending image");
             progressDialog.setMessage("sending image....");
             progressDialog.setCanceledOnTouchOutside(false);

             progressDialog.show();
             fileuri=data.getData();
//             Log.d("imagemessage",fileuri.toString());
             if (!checker.equals("image")){

                 StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document_Files");
                 final   String messagesenderref="Messages/" + messagesenderid + "/" + messagereceiverid;
                 final    String messagereceiverref="Messages/" + messagereceiverid + "/"+ messagesenderid;
                 DatabaseReference userMessagekeyref=rootreference.child("Messages").
                         child(messagesenderid).child(messagereceiverid).push();
                 final   String messagepushid=userMessagekeyref.getKey();
                 final StorageReference filepath=storageReference.child(messagepushid+"."+checker );
                   filepath.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                           if (task.isSuccessful()){

                               task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                       String filepathuri=uri.toString();
                                       Log.d("filepathuri",filepathuri);





//                                       UploadTask.TaskSnapshot downloaduri= task.getResult();
//                                       myurl=downloaduri.toString();
//                                       Log.d("downloaduurl",myurl);
                                       Map messageimageBody=new HashMap();
                                       messageimageBody.put("message",uri.toString());
//                               messageimageBody.put("message",task.getResult().getStorage().getDownloadUrl().toString());
                                       messageimageBody.put("name",fileuri.getLastPathSegment());
                                       messageimageBody.put("type",checker);
                                       messageimageBody.put("from",messagesenderid);
                                       messageimageBody.put("to",messagereceiverid);
                                       messageimageBody.put("messageID",messagepushid);
                                       messageimageBody.put("time",currenttime);
                                       messageimageBody.put("date",currentdate);
                                       Map messageBodyDetails=new HashMap();
                                       messageBodyDetails.put(messagesenderref +"/"+ messagepushid,messageimageBody);
                                       messageBodyDetails.put(messagereceiverref +"/"+ messagepushid,messageimageBody);

                                       rootreference.updateChildren(messageBodyDetails);
                                       progressDialog.dismiss();



                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull @NotNull Exception e) {
                                       progressDialog.dismiss();
                                       Toast.makeText(ChatActivity.this, "failed: "+task.getException(), Toast.LENGTH_SHORT).show();
                                   }
                               });





//
//                               UploadTask.TaskSnapshot downloaduri= task.getResult();
//                               myurl=downloaduri.toString();
//                               Log.d("downloaduurl",myurl);
//                               Map messageimageBody=new HashMap();
//                               messageimageBody.put("message",myurl);
////                               messageimageBody.put("message",task.getResult().getStorage().getDownloadUrl().toString());
//                               messageimageBody.put("name",fileuri.getLastPathSegment());
//                               messageimageBody.put("type",checker);
//                               messageimageBody.put("from",messagesenderid);
//                               messageimageBody.put("to",messagereceiverid);
//                               messageimageBody.put("messageID",messagepushid);
//                               messageimageBody.put("time",currenttime);
//                               messageimageBody.put("date",currentdate);
//                               Map messageBodyDetails=new HashMap();
//                               messageBodyDetails.put(messagesenderref +"/"+ messagepushid,messageimageBody);
//                               messageBodyDetails.put(messagereceiverref +"/"+ messagepushid,messageimageBody);
//
//                             rootreference.updateChildren(messageBodyDetails);
//                             progressDialog.dismiss();
                           }
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull @NotNull Exception e) {
                           progressDialog.dismiss();
                           Toast.makeText(ChatActivity.this, "failed:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                           double p=(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                           progressDialog.setMessage((int)p+"% Uploaded...");
                       }
                   });

             }else if(checker.equals("image")){
                 StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image_Files");
               final   String messagesenderref="Messages/" + messagesenderid + "/" + messagereceiverid;
              final    String messagereceiverref="Messages/" + messagereceiverid + "/"+ messagesenderid;
                 DatabaseReference userMessagekeyref=rootreference.child("Messages").
                         child(messagesenderid).child(messagereceiverid).push();
               final   String messagepushid=userMessagekeyref.getKey();
                final StorageReference filepath=storageReference.child(messagepushid+"."+"jpg");
                 uploadtask=filepath.putFile(fileuri);

                 Log.d("imageuploadedtostorage",uploadtask.toString());
                 uploadtask.continueWithTask(new Continuation() {
                     @Override
                     public Object then(@NonNull @NotNull Task task) throws Exception {
                         if (!task.isSuccessful()){
                             throw task.getException();
                         }
                         return filepath.getDownloadUrl();
                     }
                 }).addOnCompleteListener(new OnCompleteListener <Uri> (){
                     @Override
                     public void onComplete(@NonNull @NotNull Task<Uri> task) {
                         if (task.isSuccessful()){
                             Uri downloaduri= task.getResult();
                             myurl=downloaduri.toString();
                            Log.d("downloadurl",myurl);
                            // Toast.makeText(ChatActivity.this, myurl, Toast.LENGTH_SHORT).show();
                             Map messageimageBody=new HashMap();
                             messageimageBody.put("message",myurl);
                             messageimageBody.put("name",fileuri.getLastPathSegment());
                             messageimageBody.put("type",checker);
                             messageimageBody.put("from",messagesenderid);
                             messageimageBody.put("to",messagereceiverid);
                             messageimageBody.put("messageID",messagepushid);
                             messageimageBody.put("time",currenttime);
                             messageimageBody.put("date",currentdate);
                             Map messageBodyDetails=new HashMap();
                             messageBodyDetails.put(messagesenderref +"/"+ messagepushid,messageimageBody);
                             messageBodyDetails.put(messagereceiverref +"/"+ messagepushid,messageimageBody);
                             rootreference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                 @Override
                                 public void onComplete(@NonNull @NotNull Task task) {
                                     if (task.isSuccessful()){
                                         progressDialog.dismiss();
                                         Toast.makeText(ChatActivity.this, "image  sent successfully", Toast.LENGTH_SHORT).show();
                                     }else{
                                         progressDialog.dismiss();
                                         Toast.makeText(ChatActivity.this, "failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                     }
                                     messageinputtext.setText("");
                                 }
                             });


                         }else{
                              progressDialog.dismiss();
                             Toast.makeText(ChatActivity.this, "failed to save"+task.getException(), Toast.LENGTH_SHORT).show();
                         }
                     }
                 });


             }else{
                 progressDialog.dismiss();
                 Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT).show();
             }

         }
    }

    private void displayuserlastseen(){
        rootreference.child("Users").child(messagereceiverid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child("userState").hasChild("state")){
                    String time=snapshot.child("userState").child("time").getValue().toString();
                    String date=snapshot.child("userState").child("date").getValue().toString();
                    String state=snapshot.child("userState").child("state").getValue().toString();
                    if (state.equals("online")){
                        userlastseen.setTextColor(getResources().getColor(R.color.fui_bgAnonymous));
                        userlastseen.setText("online");
                        //   holder.userstatus.setVisibility(View.VISIBLE);
                    }else if(state.equals("offline")){
                        userlastseen.setText("Last seen:" + date+ " " +time);
                    }
                }else{
                    userlastseen.setText("offline");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
          }

    @Override
    protected void onStart() {
        super.onStart();
        rootreference.child("Messages").child(messagesenderid).child(messagereceiverid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                        Messages messages=snapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messagesAdapter.notifyDataSetChanged();
                        private_message_list.smoothScrollToPosition(private_message_list.getAdapter().getItemCount());
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
}