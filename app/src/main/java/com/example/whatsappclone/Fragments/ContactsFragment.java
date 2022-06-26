package com.example.whatsappclone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatsappclone.Friends.Contacts;
import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
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
// * Use the {@link ContactsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ContactsFragment extends Fragment {
    private  View contactsview;
    private RecyclerView mycontactlist;
    private DatabaseReference  contactsref,usersref;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
     private CircleImageView c;
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public ContactsFragment() {
        // Required empty public constructor
    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ContactsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ContactsFragment newInstance(String param1, String param2) {
//        ContactsFragment fragment = new ContactsFragment();
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
        contactsview=inflater.inflate(R.layout.fragment_contacts, container, false);
       mycontactlist=(RecyclerView) contactsview.findViewById(R.id.contacts_recyclerview);
       firebaseAuth=FirebaseAuth.getInstance();
       currentUserId=firebaseAuth.getCurrentUser().getUid();
       contactsref= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
       usersref=FirebaseDatabase.getInstance().getReference().child("Users");
       return contactsview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsref,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ContactsFragment.ContactsViewHolder holder, int position, @NonNull @NotNull Contacts model) {
                   String userIDs=getRef(position).getKey();
                   usersref.child(userIDs).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {



                           if (snapshot.exists()){

                               if (snapshot.child("userState").hasChild("state")){
                                   String time=snapshot.child("userState").child("time").getValue().toString();
                                   String date=snapshot.child("userState").child("date").getValue().toString();
                                   String state=snapshot.child("userState").child("state").getValue().toString();
                                   if (state.equals("online")){
                                    //   holder.userstatus.setText("online");
                                         holder.onlineicon.setVisibility(View.VISIBLE);
                                   }else if(state.equals("offline")){
//                                       holder.userstatus.setText("Last seen:" + date+ " " +time);
                                   holder.onlineicon.setVisibility(View.INVISIBLE);
                                   }
                               }else{
//                                   holder.userstatus.setText("offline");
                               holder.onlineicon.setVisibility(View.INVISIBLE);
                               }
                             if (snapshot.hasChild("image")){
                                 String profileimage=snapshot.child("image").getValue().toString();
                                 String profilestatus=snapshot.child("status").getValue().toString();
                                 String profileusername=snapshot.child("username").getValue().toString();
                                 Picasso.get().load(profileimage).placeholder(R.drawable.profile_image).into(holder.profilephoto);
                                 holder.username.setText(profileusername);
                                 holder.userstatus.setText(profilestatus);
                             }else{
                                 String profilestatus=snapshot.child("status").getValue().toString();
                                 String profileusername=snapshot.child("username").getValue().toString();
                                 holder.username.setText(profileusername);
                                 holder.userstatus.setText(profilestatus);
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
            public ContactsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
               View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
               ContactsViewHolder viewHolder=new ContactsViewHolder(view);
                return viewHolder;
            }
        };
        mycontactlist.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
         private TextView username, userstatus;
         private CircleImageView profilephoto;
         ImageView onlineicon;
        public ContactsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            onlineicon=itemView.findViewById(R.id.user_online_status);
            username=itemView.findViewById(R.id.user_profile_name);
            userstatus=itemView.findViewById(R.id.user_status);
            profilephoto=itemView.findViewById(R.id.usesrs_profile_image);

        }
    }
}