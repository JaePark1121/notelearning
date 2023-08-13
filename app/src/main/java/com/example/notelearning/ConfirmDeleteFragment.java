package com.example.notelearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConfirmDeleteFragment extends Fragment {
    View.OnClickListener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_delete, container, false);
        Button confirm = view.findViewById(R.id.confirm_delete_btn);
        Button cancel = view.findViewById(R.id.confirm_cancel_btn);

        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Users").child(MainActivity.uid).child("folder")
                        .child(MainActivity.curTab).child("memos").addListenerForSingleValueEvent(
                                new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<String> deleteList = new ArrayList<>();
                                        for(Integer pos:NoteAdapter.selected){
                                            deleteList.add(NoteAdapter.noteKeys.get(pos));
                                        }
                                        NoteAdapter.selected.clear();
                                        for(DataSnapshot ds: snapshot.getChildren()){
                                            if(deleteList.contains(ds.getKey())){
                                                ds.getRef().removeValue();
                                            }
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

}

