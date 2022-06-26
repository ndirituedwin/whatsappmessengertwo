package com.example.whatsappclone.Fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.whatsappclone.Group.GroupActivity;
import com.example.whatsappclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link GroupsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class GroupsFragment extends Fragment {
 private  View groupsfragmentview;
 private ListView listView;
 private ArrayAdapter<String > arrayAdapter;
 private ArrayList<String> listofgroup=new ArrayList<>();
 private DatabaseReference groupsreference;

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
    public GroupsFragment() {
        // Required empty public constructor
    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment GroupsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static GroupsFragment newInstance(String param1, String param2) {
//        GroupsFragment fragment = new GroupsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

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
        groupsfragmentview= inflater.inflate(R.layout.fragment_groups, container, false);
        groupsreference= FirebaseDatabase.getInstance().getReference().child("Groups");
        initializefields();
        fetchallgroups();
        //when a group is clcicked;
        listViewwhenclicked();

        return groupsfragmentview;
    }

    private void listViewwhenclicked() {
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedgroupname=parent.getItemAtPosition(position).toString();
            Intent groupchatintent=new Intent(getContext(), GroupActivity.class);
        groupchatintent.putExtra("groupName",selectedgroupname);
            startActivity(groupchatintent);
        }
    });
    }

    private void fetchallgroups() {
      groupsreference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
              Set<String> set=new HashSet<>();
              Iterator iterator=snapshot.getChildren().iterator();
              while (iterator.hasNext()){
                  set.add(((DataSnapshot)iterator.next()).getKey());
                //  listView.setCacheColorHint(getResources().getColor(R.color.fui_linkColor));
              }listofgroup.clear();
              listofgroup.addAll(set);
              arrayAdapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(@NonNull @NotNull DatabaseError error) {

          }
      });
    }

    private void initializefields() {
    listView= groupsfragmentview.findViewById(R.id.groups_listview);
    arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,listofgroup);
    listView.setAdapter(arrayAdapter);
    }
}