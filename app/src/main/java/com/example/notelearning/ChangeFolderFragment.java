package com.example.notelearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChangeFolderFragment extends Fragment{

    private RecyclerView changeFolderRecyclerView;
    private ChangeFolderAdapter changeFolderAdapter;
    private ArrayList<String> folderList = new ArrayList<>();
    View.OnClickListener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_folder, container, false);
        Button confirm = view.findViewById(R.id.change_folder_confirm);
        Button cancel = view.findViewById(R.id.change_folder_cancel);
        changeFolderRecyclerView = view.findViewById(R.id.change_folder_recycler);
        changeFolderAdapter = new ChangeFolderAdapter(folderList, new ChangeFolderAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View view, int position, String folderName) {
                if(ChangeFolderAdapter.selectedPosition == position){
                    ChangeFolderAdapter.selectedPosition = -1;
                }
                else {
                    ChangeFolderAdapter.selectedPosition = position;
                    ChangeFolderAdapter.selectedFolderName = folderName;
                }
                changeFolderAdapter.notifyDataSetChanged();
            }
        });
        changeFolderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        changeFolderRecyclerView.setAdapter(changeFolderAdapter);

        fetchFolderList();



        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Users").child(MainActivity.uid).child("folder")
                        .child(MainActivity.curTab).child("memos").addListenerForSingleValueEvent(

                                new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!MainActivity.curTab.equals("Home") && !MainActivity.curTab.equals("Bookmark")) {

                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                if (NoteAdapter.selectedMemoUIDs.contains(ds.getKey())) {
                                                    mDatabase.child("Users").child(MainActivity.uid).child("folder")
                                                            .child(ChangeFolderAdapter.selectedFolderName).child("memos").child(ds.getKey()).setValue(ds.getValue());

                                                    ds.getRef().removeValue();
                                                }
                                            }
                                        }
                                        else{
                                            Toast.makeText(getActivity(), "Cannot Change Folder from " + MainActivity.curTab, Toast.LENGTH_SHORT).show();
                                        }

                                        cancel.callOnClick();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }

                                }
                        );

                MainRecyclerFragment.toolbar.setVisibility(View.INVISIBLE);
                MainRecyclerFragment.recordButton.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(listener);


        // Your code here

        return view;
    }
    private void fetchFolderList() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(MainActivity.uid).child("folder")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        folderList.clear();
                        for (DataSnapshot folderSnapshot : dataSnapshot.getChildren()) {
                            String folderName = folderSnapshot.getKey();
                            if(!folderName.equals("Home") && !folderName.equals("Bookmark") ) {
                                folderList.add(folderName);
                            }
                        }
                        changeFolderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                    }
                });
    }



}

