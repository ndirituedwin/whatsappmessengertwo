package com.example.whatsappclone.Friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.whatsappclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
   private Toolbar toolbar;
   private RecyclerView findfriendsrecyclerlist;
   private DatabaseReference  usersref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        initializefields();

        usersref= FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void initializefields() {
      findfriendsrecyclerlist=findViewById(R.id.find_friends_recyclerview);
      toolbar=findViewById(R.id.find_friends_toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setTitle("Find friends");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(usersref,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull FindFriendsActivity.FindFriendsViewHolder holder, int position, @NonNull @NotNull Contacts model) {

                holder.userName.setText(model.getUsername());
                holder.userstatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileimage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selecteduserid=getRef(position).getKey();
                        Intent selecteduserintent=new Intent(FindFriendsActivity.this,SelectedProfileActivity.class);
                        selecteduserintent.putExtra("selecteduserid",selecteduserid);
                        startActivity(selecteduserintent);
                    }
                });
            }

            @NonNull
            @NotNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
               FindFriendsViewHolder viewholder=new FindFriendsViewHolder(view);
                return viewholder;
            }
        };
        findfriendsrecyclerlist.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userstatus;
        ImageView useronlinestatus;
        CircleImageView profileimage;
        public FindFriendsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_name);
            userstatus=itemView.findViewById(R.id.user_status);
            useronlinestatus=itemView.findViewById(R.id.user_online_status);
           profileimage=itemView.findViewById(R.id.usesrs_profile_image);
        }
    }

}