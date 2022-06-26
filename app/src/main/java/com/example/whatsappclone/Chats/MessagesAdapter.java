package com.example.whatsappclone.Chats;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.ImageViewer.ImageviewerActivity;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {


    private List<Messages> usermessageslists;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference,senderdatabaserefe;
    public MessagesAdapter(List<Messages> usermessageslists){
        this.usermessageslists=usermessageslists;

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView sendermessagetext, receivermessagetext,messagesendertextview,messagereceivertextview;
        public CircleImageView receiverprofileimage,senderprofileimage;
        public ImageView  messagesenderpicture, messagereceiverpicture;
        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            messagesendertextview=itemView.findViewById(R.id.message_sender_textview);
            messagereceivertextview=itemView.findViewById(R.id.message_receiver_textview);
            messagereceiverpicture=itemView.findViewById(R.id.message_receiver_image);
            messagesenderpicture=itemView.findViewById(R.id.message_sender_image);
            sendermessagetext=(TextView) itemView.findViewById(R.id.sender_messages);
            receivermessagetext=(TextView) itemView.findViewById(R.id.receiver_messages);
            receiverprofileimage=(CircleImageView) itemView.findViewById(R.id.message_profile_image);
            senderprofileimage=(CircleImageView) itemView.findViewById(R.id.sender_profile_image);

        }
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
               View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);
         firebaseAuth=FirebaseAuth.getInstance();
        return new MessageViewHolder(view);

    }



    @Override
    public void onBindViewHolder(@NonNull @NotNull MessagesAdapter.MessageViewHolder holder, int position) {
                     String messengesenderid=firebaseAuth.getCurrentUser().getUid();
                 Messages messages=usermessageslists.get(position);
                 String fromuserid=messages.getFrom();
                 String frommessagetype=messages.getType();
                  Log.d("fillli", frommessagetype);
                  databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserid);
                 databaseReference.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild("image")){
                            String receiverimage=snapshot.child("image").getValue().toString();
                            Picasso.get().load(receiverimage).placeholder(R.drawable.profile_image).into(holder.receiverprofileimage);
                        }
                     }

                     @Override
                     public void onCancelled(@NonNull @NotNull DatabaseError error) {

                     }
                 });
                 /**getting current user profile image**/
                 senderdatabaserefe=FirebaseDatabase.getInstance().getReference().child("Users").child(messengesenderid);
        senderdatabaserefe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("image")){
                    String ssenderimage=snapshot.child("image").getValue().toString();
                    Picasso.get().load(ssenderimage).placeholder(R.drawable.profile_image).into(holder.senderprofileimage);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

                 /**through**/
                 holder.receivermessagetext.setVisibility(View.GONE);
        holder.receiverprofileimage.setVisibility(View.GONE);
        holder.senderprofileimage.setVisibility(View.GONE);
        holder.sendermessagetext.setVisibility(View.GONE);
        holder.messagesenderpicture.setVisibility(View.GONE);
        holder.messagereceiverpicture.setVisibility(View.GONE);
        holder.messagesendertextview.setVisibility(View.GONE);
        holder.messagereceivertextview.setVisibility(View.GONE);


        if (frommessagetype.equals("text")){
                     Log.d("frmtype",frommessagetype);


                     if (fromuserid.equals(messengesenderid)){

                         holder.sendermessagetext.setVisibility(View.VISIBLE);
                         holder.senderprofileimage.setVisibility(View.VISIBLE);
                         holder.sendermessagetext.setBackgroundResource(R.drawable.sebder_messages_layout);
                         holder.sendermessagetext.setTextColor(Color.BLACK);
                         holder.sendermessagetext.setText(messages.getMessage()+"\n\n"+messages.getTime()+":"+messages.getDate());
                     }else{
                         holder.receiverprofileimage.setVisibility(View.VISIBLE);
                         holder.receivermessagetext.setVisibility(View.VISIBLE);

                         holder.receivermessagetext.setBackgroundResource(R.drawable.receiver_messages_layout);
                         holder.receivermessagetext.setTextColor(Color.BLACK);
                         holder.receivermessagetext.setText(messages.getMessage()+"\n\n"+messages.getTime()+":"+messages.getDate());

                     }
                 }else if(frommessagetype.equals("image")){

                        if (fromuserid.equals(messengesenderid)){
                            holder.senderprofileimage.setVisibility(View.VISIBLE);
                            holder.messagesenderpicture.setVisibility(View.VISIBLE);
                             Picasso.get().load(messages.getMessage()).into(holder.messagesenderpicture);

                        }else{
                            holder.receiverprofileimage.setVisibility(View.VISIBLE);
                            holder.messagereceiverpicture.setVisibility(View.VISIBLE);
                            Picasso.get().load(messages.getMessage()).into(holder.messagereceiverpicture);

                        }
        }
        else  if (frommessagetype.equals("pdf")||frommessagetype.equals("docx")){

            if (fromuserid.equals(messengesenderid)){
                holder.senderprofileimage.setVisibility(View.VISIBLE);

                holder.messagesenderpicture.setVisibility(View.VISIBLE);
                // holder.messagesenderpicture.setBackgroundResource(R.drawable.file);
                 Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/javawhatsapp-9caf9.appspot.com/o/Image_Files%2Ffile.png?alt=media&token=021f8f42-995a-45f2-b367-2d21fafa5ef0").into(holder.messagesenderpicture);
                holder.messagesendertextview.setVisibility(View.VISIBLE);
                holder.messagesendertextview.setText(messages.getName());
//                 holder.itemView.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v) {
//                         Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageslists.get(position).getMessage()));
//                    Log.d("uri",intent.toString());
//              holder.itemView.getContext().startActivity(intent);
//                     }
//                 });
            }
            else{
                    holder.receiverprofileimage.setVisibility(View.VISIBLE);
                holder.messagereceivertextview.setVisibility(View.VISIBLE);
                holder.messagereceivertextview.setText(messages.getName());
                    holder.messagereceiverpicture.setVisibility(View.VISIBLE);
//                    holder.messagereceiverpicture.setBackgroundResource(R.drawable.file);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/javawhatsapp-9caf9.appspot.com/o/Image_Files%2Ffile.png?alt=media&token=021f8f42-995a-45f2-b367-2d21fafa5ef0").into(holder.messagereceiverpicture);

//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageslists.get(position).getMessage()));
//                        holder.itemView.getContext().startActivity(intent);
//                    }
//                });
            }
        }
        /**deleting message,handle sender side**/
        if (fromuserid.equals(messengesenderid)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (usermessageslists.get(position).getType().equals("pdf")||usermessageslists.get(position).getType().equals("docx")){
                        Toast.makeText(v.getContext(), usermessageslists.get(position).getType()+" selected", Toast.LENGTH_SHORT).show();
                        CharSequence  options[]=new CharSequence[]
                                {
                                        "Delete for me",
                                        "Download the document",
                                        "Cancel",
                                        "Delete for everyone",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message ?");
                         builder.setItems(options, new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 if (which==0){
                                    deletesentmessages(position,holder);
                                     Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                     holder.itemView.getContext().startActivity(intent);

                                 }
                                 else if(which==1){

                                     Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageslists.get(position).getMessage()));
                                     holder.itemView.getContext().startActivity(intent);

                                 }else if (which==2){

                                 }else if (which==3){
                                    deletemessageforeveryone(position,holder);
                                     Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                     holder.itemView.getContext().startActivity(intent);

                                 }
                             }
                         });
                         builder.show();

                    }
                   else if (usermessageslists.get(position).getType().equals("text")){
                        Toast.makeText(v.getContext(), usermessageslists.get(position).getType()+" selected", Toast.LENGTH_SHORT).show();
                        CharSequence  options[]=new CharSequence[]
                                {
                                        "Delete for me",
//                                        "Download the document",
                                        "Cancel",
                                        "Delete for everyone",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    deletesentmessages(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                               else if (which==2){
                                         deletemessageforeveryone(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();

                    }
                      else if (usermessageslists.get(position).getType().equals("image")){
                        Toast.makeText(v.getContext(), usermessageslists.get(position).getType()+" selected", Toast.LENGTH_SHORT).show();
                        CharSequence  options[]=new CharSequence[]
                                {
                                        "Delete for me",
                                        "View Image",
                                        "Cancel",
                                        "Delete for everyone",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                        deletesentmessages(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else if(which==1){
                                    Intent intent=new Intent(holder.itemView.getContext(), ImageviewerActivity.class);
                                    intent.putExtra("imageurl",usermessageslists.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else if (which==3){
                                  deletemessageforeveryone(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();

                    }
                }
            });
        }
        /** handle receiver side**/
        else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (usermessageslists.get(position).getType().equals("pdf")||usermessageslists.get(position).getType().equals("docx")){
                        Toast.makeText(v.getContext(), usermessageslists.get(position).getType()+" selected", Toast.LENGTH_SHORT).show();
                        CharSequence  options[]=new CharSequence[]
                                {
                                        "Delete for me",
                                        "Download the document",
                                        "Cancel",

                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                             deleterecevemessages(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else if(which==1){

                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageslists.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();

                    }
                    else if (usermessageslists.get(position).getType().equals("text")){
                        Toast.makeText(v.getContext(), usermessageslists.get(position).getType()+" selected", Toast.LENGTH_SHORT).show();
                        CharSequence  options[]=new CharSequence[]
                                {
                                        "Delete for me",
//                                        "Download the document",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    deleterecevemessages(position, holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }

                            }
                        });
                        builder.show();

                    }
                    else if (usermessageslists.get(position).getType().equals("image")){
                        Toast.makeText(v.getContext(), usermessageslists.get(position).getType()+" selected", Toast.LENGTH_SHORT).show();
                        CharSequence  options[]=new CharSequence[]
                                {
                                        "Delete for me",
                                        "View Image",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                             deleterecevemessages(position, holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else if(which==1){
                                Intent intent=new Intent(holder.itemView.getContext(), ImageviewerActivity.class);
                                intent.putExtra("imageurl",usermessageslists.get(position).getMessage());
                                holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();

                    }
                }
            });
        }

    }
    private void deletesentmessages(final int position, final MessageViewHolder holder){

        DatabaseReference  rootref=FirebaseDatabase.getInstance().getReference();
        rootref.child("Messages").child(usermessageslists.get(position).getFrom())
                .child(usermessageslists.get(position).getTo())
                .child(usermessageslists.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                  if (task.isSuccessful()){
                      Toast.makeText(holder.itemView.getContext(), "message deleted", Toast.LENGTH_SHORT).show();
                  }else{
                      Toast.makeText(holder.itemView.getContext(), "failed to delete message: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                  }
             }
        });
    }
    private void deleterecevemessages(final int position, final MessageViewHolder holder){

        DatabaseReference  rootref=FirebaseDatabase.getInstance().getReference();
        rootref.child("Messages")
                .child(usermessageslists.get(position).getTo())
                .child(usermessageslists.get(position).getFrom())
                .child(usermessageslists.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "message deleted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(holder.itemView.getContext(), "failed to delete message: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void deletemessageforeveryone(final int position, final MessageViewHolder holder){

        final DatabaseReference  rootref=FirebaseDatabase.getInstance().getReference();
        rootref.child("Messages")
                .child(usermessageslists.get(position).getTo())
                .child(usermessageslists.get(position).getFrom())
                .child(usermessageslists.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){
                    rootref.child("Messages")
                            .child(usermessageslists.get(position).getFrom())
                            .child(usermessageslists.get(position).getTo())
                            .child(usermessageslists.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(holder.itemView.getContext(), "message deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(holder.itemView.getContext(), "failed to delete message: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usermessageslists.size();
    }
    /**prevent duplicating items on the recyclerview adapate**/
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    /**end**/


}


