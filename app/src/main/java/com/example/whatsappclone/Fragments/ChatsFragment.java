package com.example.whatsappclone.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsappclone.Chats.ChatActivity;
import com.example.whatsappclone.Friends.Contacts;
import com.example.whatsappclone.LoginandRegisterActivity.LoginActivity;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ChatsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ChatsFragment extends Fragment {
    private  View privatechatsview;
    private RecyclerView chatlist;
    private DatabaseReference contactsreference, usersreference;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private FirebaseUser currentUser;


//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ChatsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ChatsFragment newInstance(String param1, String param2) {
//        ChatsFragment fragment = new ChatsFragment();
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
//        // Inflate the layout for this fragment
        privatechatsview=inflater.inflate(R.layout.fragment_chats, container, false);
        initializefields();
             firebaseAuth=FirebaseAuth.getInstance();
          currentUser=firebaseAuth.getCurrentUser();
               return privatechatsview;

    }

    private void initializefields() {
        chatlist=(RecyclerView) privatechatsview.findViewById(R.id.chat_list);

    }


    @Override
    public void onStart() {
        super.onStart();
      if (currentUser==null) {
          sendthemtologinactivity();
      }else{
          currentUserId=firebaseAuth.getInstance().getCurrentUser().getUid();
          contactsreference= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
          usersreference=FirebaseDatabase.getInstance().getReference().child("Users");

          FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsreference, Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull @NotNull ChatsFragment.ChatsViewHolder holder, int position, @NonNull @NotNull Contacts model) {
                final String usersids = getRef(position).getKey();
                final String[] image = {"default_image"};

                usersreference.child(usersids).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                      if (snapshot.exists()){
                          if (snapshot.hasChild("image")) {
                              image[0] = snapshot.child("image").getValue().toString();
                              Picasso.get().load(image[0]).placeholder(R.drawable.profile_image).into(holder.userphoto);
                          }

                          final String retname = snapshot.child("username").getValue().toString();
                          final String status = snapshot.child("status").getValue().toString();
                          holder.username.setText(retname);
                          //   holder.userstatus.setText(status);
                          if (snapshot.child("userState").hasChild("state")){
                              String time=snapshot.child("userState").child("time").getValue().toString();
                              String date=snapshot.child("userState").child("date").getValue().toString();
                              String state=snapshot.child("userState").child("state").getValue().toString();
                             if (state.equals("online")){
                                 holder.userstatus.setText("online");
                              //   holder.userstatus.setTextColor(getResources().getColor(R.color.fui_bgAnonymous));
                              //   holder.userstatus.setVisibility(View.VISIBLE);
                             }else if(state.equals("offline")){
                                 holder.userstatus.setText("Last seen:" + date+ " " +time);
                             }
                          }else{
                              holder.userstatus.setText("offline");
                          }

                          holder.itemView.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  Intent chatactivityintent=new Intent(getContext(), ChatActivity.class);
                                  chatactivityintent.putExtra("visituserid",usersids);
                                  chatactivityintent.putExtra("visitusername",retname);
                                  chatactivityintent.putExtra("image", image[0]);
                                  startActivity(chatactivityintent);
                              }
                          });
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
            public ChatsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);

                return new ChatsViewHolder(view);
            }
        };
        chatlist.setAdapter(adapter);
        adapter.startListening();
        //adapter.notifyDataSetChanged();


    }

    }

    private void sendthemtologinactivity() {
        Intent loginactivity=new Intent(getContext(), LoginActivity.class);
        loginactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginactivity);
    }
    public static  class ChatsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userphoto;
        private TextView username,userstatus;
        public ChatsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
              userphoto=itemView.findViewById(R.id.usesrs_profile_image);
              username=itemView.findViewById(R.id.user_profile_name);
              userstatus=itemView.findViewById(R.id.user_status);
        }
    }
}