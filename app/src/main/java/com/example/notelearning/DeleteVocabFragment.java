package com.example.notelearning;

import static com.example.notelearning.VocabListActivity.addVocabFragment;
import static com.example.notelearning.VocabListActivity.deleteVocabFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteVocabFragment extends Fragment {
    View.OnClickListener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_vocab, container, false);
        Button confirm = view.findViewById(R.id.vocab_delete_btn);
        Button cancel = view.findViewById(R.id.vocab_cancel_btn);

        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Users").child(MainActivity.uid).child("folder")
                        .child(MainActivity.curTab).child("memos").addListenerForSingleValueEvent(
                                new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                if (NoteAdapter.selectedMemoUIDs.contains(ds.getKey())) {
                                                    mDatabase.child("Users").child(MainActivity.uid).child("folder")
                                                            .child(ChangeFolderAdapter.selectedFolderName).child("memos").child(ds.getKey()).child("vocablist").removeValue();

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

            }
        });


        cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(deleteVocabFragment).commitAllowingStateLoss();

            }
        });



        // Your code here

        return view;
    }

}