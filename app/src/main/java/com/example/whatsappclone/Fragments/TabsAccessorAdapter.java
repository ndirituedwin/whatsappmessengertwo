package com.example.whatsappclone.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm);
    }

    public TabsAccessorAdapter(@NonNull @NotNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {

        if (position==0){
            ChatsFragment chatsFragment=new ChatsFragment();
            return chatsFragment;
        }else if (position==1){
            GroupsFragment groupsFragment=new GroupsFragment();
            return groupsFragment;
        }else if (position==2) {
            ContactsFragment contactsFragment = new ContactsFragment();
            return contactsFragment;
        }else if(position==3){
           RequestsFragment requestsFragment=new RequestsFragment();
           return requestsFragment;
        }else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //return super.getPageTitle(position);
        switch (position){
            case 0:
              return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            case 3:
                return "Requests";
            default:
                return null;
        }
    }
}
