package com.example.whatsappclone.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.Friends.Contacts;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import de.hdodenhof.circleimageview.CircleImageView;

//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link RequestsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class RequestsFragment extends Fragment {
   private View RequestFragmentview;
   private RecyclerView chatrequestlist;
   private DatabaseReference chatrequestref,usersref,contactsreference;
   private FirebaseAuth firebaseAuth;
   private String currentUserId;
//    public static Button acceptchatrequest, rejectechatrequest;
//    public static TextView username, userstatus;
//    public static CircleImageView profilephoto;
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public RequestsFragment() {
        // Required empty public constructor
    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment RequestsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static RequestsFragment newInstance(String param1, String param2) {
//        RequestsFragment fragment = new RequestsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestFragmentview= inflater.inflate(R.layout.fragment_requests, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        usersref=FirebaseDatabase.getInstance().getReference().child("Users");
        chatrequestref= FirebaseDatabase.getInstance().getReference().child("Chat_requests");
        contactsreference=FirebaseDatabase.getInstance().getReference().child("Contacts");
        chatrequestlist=RequestFragmentview.findViewById(R.id.chat_friendrequestslist);


    return RequestFragmentview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatrequestref.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull RequestsFragment.RequestViewHolder holder, int position, @NonNull @NotNull Contacts model) {
                   holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                   holder.itemView.findViewById(R.id.request_reject_btn).setVisibility(View.VISIBLE);
                   final String listuserid=getRef(position).getKey();
                   DatabaseReference gettyperef=getRef(position).child("request_type").getRef();
                   gettyperef.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                           if (snapshot.exists()){
                               String type=snapshot.getValue().toString();
                               if (type.equals("received")){

                                   //Toast.makeText(getContext(), type, Toast.LENGTH_SHORT).show();
                                   usersref.child(listuserid).addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                           if (snapshot.hasChild("image")){
//                                               final String requestname=snapshot.child("username").getValue().toString();
//                                               final String requeststatus=snapshot.child("status").getValue().toString();
                                               final String requestimage=snapshot.child("image").getValue().toString();
                                             // holder.userstatus.setText(requeststatus);
                                               Picasso.get().load(requestimage).placeholder(R.drawable.profile_image).into(holder.profilephoto);
                                               //holder.username.setText(requestname);
                                           }
                                               final String requestname=snapshot.child("username").getValue().toString();
                                               final String requeststatus=snapshot.child("status").getValue().toString();
                                           holder.userstatus.setText("wants to be friends with you");
                                  //         holder.userstatus.setText(requeststatus);
                                               holder.username.setText(requestname);



                                           holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                     CharSequence options[]=new CharSequence[]
                                                             {
                                                                     "Accept",
                                                                     "Cancel"
                                                             };
                                                   AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                   builder.setTitle("Chat request from "+requestname);
                                                   builder.setItems(options, new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                         if (which==0){
                                                           contactsreference.child(currentUserId).child(listuserid).child("Contact")
                                                                   .setValue("Saved")
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            contactsreference.child(listuserid).child(currentUserId).child("Contact")
                                                                                    .setValue("Saved")
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                       if (task.isSuccessful()){
                                                                                           chatrequestref.child(currentUserId).child(listuserid)
                                                                                                   .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                               @Override
                                                                                               public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                                  if (task.isSuccessful()){
                                                                                                      chatrequestref.child(listuserid).child(currentUserId)
                                                                                                              .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                          @Override
                                                                                                          public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                                               if (task.isSuccessful()){
                                                                                                                   Toast.makeText(getContext(), "New contact added", Toast.LENGTH_SHORT).show();
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
                                                                       }
                                                                   });
                                                         }
                                                         if (which==1){
                                                             chatrequestref.child(currentUserId).child(listuserid)
                                                                     .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                     if (task.isSuccessful()){
                                                                         chatrequestref.child(listuserid).child(currentUserId)
                                                                                 .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                             @Override
                                                                             public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                 if (task.isSuccessful()){
                                                                                     Toast.makeText(getContext(), "contact removed", Toast.LENGTH_SHORT).show();
                                                                                 }
                                                                             }
                                                                         });
                                                                     }
                                                                 }
                                                             });
                                                         }
                                                       }
                                                   });
                                                   builder.show();


                                               }
                                           });
                                       }


                                       @Override
                                       public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                       }
                                   });
                               }else if (type.equals("sent")){
                                   Button requestsent=holder.itemView.findViewById(R.id.request_accept_btn);
                                   requestsent.setText("Request sent");
                                   holder.itemView.findViewById(R.id.request_reject_btn).setVisibility(View.INVISIBLE);




                                   //Toast.makeText(getContext(), type, Toast.LENGTH_SHORT).show();
                                   usersref.child(listuserid).addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                           if (snapshot.hasChild("image")){
//                                               final String requestname=snapshot.child("username").getValue().toString();
//                                               final String requeststatus=snapshot.child("status").getValue().toString();
                                               final String requestimage=snapshot.child("image").getValue().toString();
                                               // holder.userstatus.setText(requeststatus);
                                               Picasso.get().load(requestimage).placeholder(R.drawable.profile_image).into(holder.profilephoto);
                                               //holder.username.setText(requestname);
                                           }
                                           final String requestname=snapshot.child("username").getValue().toString();
                                           final String requeststatus=snapshot.child("status").getValue().toString();
                                           holder.userstatus.setText("you have sent a request to "+requestname);
                                           //         holder.userstatus.setText(requeststatus);
                                           holder.username.setText(requestname);



                                           holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   CharSequence options[]=new CharSequence[]
                                                           {
                                                                   "Cancel chat request"
                                                           };
                                                   AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                   builder.setTitle("Already sent request");
                                                   builder.setItems(options, new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {

                                                           if (which==0){
                                                               chatrequestref.child(currentUserId).child(listuserid)
                                                                       .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                       if (task.isSuccessful()){
                                                                           chatrequestref.child(listuserid).child(currentUserId)
                                                                                   .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                               @Override
                                                                               public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                   if (task.isSuccessful()){
                                                                                       Toast.makeText(getContext(), "chat request cancelled", Toast.LENGTH_SHORT).show();
                                                                                   }
                                                                               }
                                                                           });
                                                                       }
                                                                   }
                                                               });
                                                           }
                                                       }
                                                   });
                                                   builder.show();


                                               }
                                           });
                                       }


                                       @Override
                                       public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                       }
                                   });



                               }
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull @NotNull DatabaseError error) {

                       }
                   });
            }

            @NonNull
            @NotNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                RequestViewHolder viewholder=new RequestViewHolder(view);
                return viewholder;
            }
        };

          chatrequestlist.setAdapter(adapter);
          adapter.startListening();
    }
    public static  class RequestViewHolder extends RecyclerView.ViewHolder{
        private Button acceptchatrequest, rejectechatrequest;
        private TextView username, userstatus;
        private CircleImageView profilephoto;
        public RequestViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.user_profile_name);
            userstatus=itemView.findViewById(R.id.user_status);
            profilephoto=itemView.findViewById(R.id.usesrs_profile_image);
            acceptchatrequest=itemView.findViewById(R.id.request_accept_btn);
            rejectechatrequest=itemView.findViewById(R.id.request_reject_btn);
        }
    }
}